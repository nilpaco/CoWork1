package com.project.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.project.domain.*;
import com.project.repository.*;
import com.project.repository.search.SpaceSearchRepository;
import com.project.security.SecurityUtils;
import com.project.web.rest.dto.SpaceDTO;
import com.project.web.rest.util.HeaderUtil;
import com.project.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Space.
 */
@RestController
@RequestMapping("/api")
public class SpaceResource {

    private final Logger log = LoggerFactory.getLogger(SpaceResource.class);

    @Inject
    private SpaceRepository spaceRepository;

    @Inject
    private SpaceCriteriaRepository spaceCriteriaRepository;


    @Inject
    private SpaceSearchRepository spaceSearchRepository;

    @Inject
    private FavoriteRepository favoriteRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private ImageRepository imageRepository;

    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private ConversationRepository conversationRepository;

    @Inject
    private MessageRepository messageRepository;


    /**
     * POST  /spaces -> Create a new space.
     */
    @RequestMapping(value = "/spaces",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Space> createSpace(@RequestBody Space space) throws URISyntaxException {
        log.debug("REST request to save Space : {}", space);
        if (space.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("space", "idexists", "A new space cannot already have an ID")).body(null);
        }
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        space.setUser(user);
        Space result = spaceRepository.save(space);
        spaceSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/spaces/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("space", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /spaces -> Updates an existing space.
     */
    @RequestMapping(value = "/spaces",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Space> updateSpace(@RequestBody Space space) throws URISyntaxException {
        log.debug("REST request to update Space : {}", space);
        if (space.getId() == null) {
            return createSpace(space);
        }
        Space result = spaceRepository.save(space);
        spaceSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("space", space.getId().toString()))
            .body(result);
    }

    /**
     * GET  /spaces -> get all the spaces.
     */
    @RequestMapping(value = "/spaces",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Space>> getAllSpaces(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a list of Spaces");
        Page<Space> page = spaceRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/spaces");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    @RequestMapping(value = "/spacesLand",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Space>> getSpacesLanding(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a list of Spaces");
        Page<Space> page = spaceRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/spacesLand");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


    /**
     * GET  /spaces/:id -> get the "id" space.
     */
    @RequestMapping(value = "/spaces/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Space> getSpace(@PathVariable Long id) {
        log.debug("REST request to get Space : {}", id);
        Space space = spaceRepository.findOneWithEagerRelationships(id);
        return Optional.ofNullable(space)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /spaces/:id -> delete the "id" space.
     */
    @RequestMapping(value = "/spaces/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        log.debug("REST request to delete Space : {}", id);
        List<Image> image = imageRepository.findImagesBySpace(id);
        for (int i = 0; i < image.size(); i++) {
            imageRepository.delete(image.get(i));
        }
        List<Review> review = reviewRepository.findReviewsBySpace(id);
        for (int i = 0; i < review.size(); i++) {
            reviewRepository.delete(review.get(i));
        }
        List<Favorite> favorite = favoriteRepository.findBySpace(id);
        for (int i = 0; i < favorite.size(); i++) {
            favoriteRepository.delete(favorite.get(i));
        }
        List<Conversation> conversation = conversationRepository.findBySpace(id);
        for (int i = 0; i < conversation.size(); i++) {
            List<Message> message = messageRepository.findBySpace(conversation.get(i).getId());
            for (int j = 0; j < message.size(); j++) {
                messageRepository.delete(message.get(i));
            }
        }
        for (int i = 0; i <conversation.size(); i++) {
            conversationRepository.delete(conversation.get(i));
        }


        spaceRepository.delete(id);
        spaceSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("space", id.toString())).build();
    }

    /**
     * SEARCH  /_search/spaces/:query -> search for the space corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/spaces/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Space> searchSpaces(@PathVariable String query) {
        log.debug("REST request to search Spaces for query {}", query);
        return StreamSupport
            .stream(spaceSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    /**
     * SEARCH  //spaces/userliked -> get all spaces by user liked
     * to the query.
     */
    @RequestMapping(value = "/spaces/userliked",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SpaceDTO>> getSpaces(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a list of Spaces");
        Page<Space> page = spaceRepository.findAll(pageable);

        List<SpaceDTO> listSpaceDTO = getSpaceDTOs(page.getContent());

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/spaces");
        return new ResponseEntity<>(listSpaceDTO, headers, HttpStatus.OK);

    }

    private List<SpaceDTO> getSpaceDTOs(List<Space> list) {
        List<SpaceDTO> listSpaceDTO = new ArrayList<>();

        for (Space space : list) {
            Favorite favorite = favoriteRepository.findExistUserLiked(space.getId());
            SpaceDTO spaceDTO = new SpaceDTO();
            spaceDTO.setSpace(space);
            List<Image> listImage = imageRepository.findImagesBySpace(space.getId());

            if(listImage.size() != 0){
                spaceDTO.setMasterImage(listImage.get(0).getImage());
            }

            if (favorite == null || favorite.getLiked() == null || !favorite.getLiked()) {
                spaceDTO.setLiked(false);
            } else {
                spaceDTO.setLiked(true);
            }
            listSpaceDTO.add(spaceDTO);

        }

        return listSpaceDTO;
    }

    @RequestMapping(value = "/spaces/userliked/{price}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SpaceDTO>> getSpacesByPrice(Pageable pageable, @PathVariable Integer price)
        throws URISyntaxException {
        log.debug("REST request to get a list of Spaces");
        Page<Space> page = spaceRepository.findAllByPrice(pageable, price.doubleValue());

        List<SpaceDTO> listSpaceDTO = getSpaceDTOs(page.getContent());

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/spaces");
        return new ResponseEntity<>(listSpaceDTO, headers, HttpStatus.OK);

    }

    @RequestMapping(value = "/spaces/byfilters",
    method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public ResponseEntity<List<SpaceDTO>> getPlayersByParams(
        @RequestParam(value = "minprice", required = false) Double minPrice,
        @RequestParam(value = "maxprice", required = false) Double maxPrice,
        @RequestParam(value = "numpers", required = false) Integer numPers,
        @RequestParam(value = "services", required = false) String services,
        @RequestParam(value = "address", required = false) String address
        ) {
        Map<String, Object> params = new HashMap<>();


        if (minPrice != null) {
            params.put("minprice", minPrice);
        }
        if (maxPrice != null) {
            params.put("maxprice", maxPrice);
        }
        if(numPers != null){
            params.put("numpers", numPers);
        }
        if(services != null && !services.equals("")){
            //arrays de longs
            String[] servicesArray = services.split("-");

            List<Long> servicesLong = new ArrayList<>();

            for (int i = 0; i < servicesArray.length; i++) {
                servicesLong.add(Long.valueOf(servicesArray[i]));
            }
            params.put("services", servicesLong);
        }

        if(address != null){
            params.put("address", address);
        }

        if (params.isEmpty()) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("player", "filtersEmpty", "You must at least add one filter")).body(null);
        }

        List<Space> result = spaceCriteriaRepository.findByParameters(params);

        List<SpaceDTO> spaceDTOs = getSpaceDTOs(result);


        return new ResponseEntity<>(
            spaceDTOs,
            HttpStatus.OK);

    }


}
