package com.project.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.project.domain.Favorite;
import com.project.domain.Space;
import com.project.domain.User;
import com.project.repository.FavoriteRepository;
import com.project.repository.SpaceRepository;
import com.project.repository.UserRepository;
import com.project.repository.search.FavoriteSearchRepository;
import com.project.security.SecurityUtils;
import com.project.web.rest.util.HeaderUtil;
import com.project.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Favorite.
 */
@RestController
@RequestMapping("/api")
public class FavoriteResource {

    private final Logger log = LoggerFactory.getLogger(FavoriteResource.class);

    @Inject
    private FavoriteRepository favoriteRepository;

    @Inject
    private FavoriteSearchRepository favoriteSearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SpaceRepository spaceRepository;

    /**
     * POST  /favorites -> Create a new favorite.
     */
    @RequestMapping(value = "/favorites",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Favorite> createFavorite(@RequestBody Favorite favorite) throws URISyntaxException {
        log.debug("REST request to save Favorite : {}", favorite);
        if (favorite.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("favorite", "idexists", "A new favorite cannot already have an ID")).body(null);
        }
        Favorite result = favoriteRepository.save(favorite);
        favoriteSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/favorites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("favorite", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /favorites -> Updates an existing favorite.
     */
    @RequestMapping(value = "/favorites",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Favorite> updateFavorite(@RequestBody Favorite favorite) throws URISyntaxException {
        log.debug("REST request to update Favorite : {}", favorite);
        if (favorite.getId() == null) {
            return createFavorite(favorite);
        }
        Favorite result = favoriteRepository.save(favorite);
        favoriteSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("favorite", favorite.getId().toString()))
            .body(result);
    }

    /**
     * GET  /favorites -> get all the favorites.
     */
    @RequestMapping(value = "/favorites",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Favorite>> getAllFavorites(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Favorites");
        Page<Favorite> page = favoriteRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/favorites");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /favorites/:id -> get the "id" favorite.
     */
    @RequestMapping(value = "/favorites/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Favorite> getFavorite(@PathVariable Long id) {
        log.debug("REST request to get Favorite : {}", id);
        Favorite favorite = favoriteRepository.findOne(id);
        return Optional.ofNullable(favorite)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /favorites/:id -> delete the "id" favorite.
     */
    @RequestMapping(value = "/favorites/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        log.debug("REST request to delete Favorite : {}", id);
        favoriteRepository.delete(id);
        favoriteSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("favorite", id.toString())).build();
    }

    /**
     * SEARCH  /_search/favorites/:query -> search for the favorite corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/favorites/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Favorite> searchFavorites(@PathVariable String query) {
        log.debug("REST request to search Favorites for query {}", query);
        return StreamSupport
            .stream(favoriteSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
    @RequestMapping(value = "/favorites/{id}/like",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Favorite> likeSong(@PathVariable Long id) throws URISyntaxException {

        Favorite exist = favoriteRepository.findExistUserLiked(id);

        if(exist != null){
            if(exist.getLiked() == null || exist.getLiked() == false){
                exist.setLiked(true);
            }else{
                exist.setLiked(false);
            }
            return updateFavorite(exist);
        }

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        Space song = spaceRepository.findOne(id);

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setSpace(song);
        favorite.setLiked(true);

        Favorite result = favoriteRepository.save(favorite);
        return ResponseEntity.created(new URI("/api/favorites/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("favorite", result.getId().toString()))
            .body(result);
    }

}
