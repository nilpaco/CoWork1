package com.project.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.project.domain.Review;
import com.project.domain.Space;
import com.project.domain.User;
import com.project.repository.ReviewRepository;
import com.project.repository.SpaceRepository;
import com.project.repository.UserRepository;
import com.project.repository.search.ReviewSearchRepository;
import com.project.security.SecurityUtils;
import com.project.web.rest.util.HeaderUtil;
import com.project.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Review.
 */
@RestController
@RequestMapping("/api")
public class ReviewResource {

    private final Logger log = LoggerFactory.getLogger(ReviewResource.class);

    @Inject
    private ReviewRepository reviewRepository;

    @Inject
    private ReviewSearchRepository reviewSearchRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private SpaceRepository spaceRepository;


    /**
     * POST  /reviews -> Create a new review.
     */
    @RequestMapping(value = "/space/{id}/reviews",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> createReview(@RequestBody Review review, @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to save Review : {}", review);
        if (review.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("review", "idexists", "A new review cannot already have an ID")).body(null);
        }

        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        Space space = spaceRepository.findOne(id);
        review.setUser(user);
        review.setSpace(space);
        ZonedDateTime today = ZonedDateTime.now();
        review.setTime(today);


        Review result = reviewRepository.save(review);
        reviewSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/reviews/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("review", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /reviews -> Updates an existing review.
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> updateReview(@RequestBody Review review, @PathVariable Long id) throws URISyntaxException {
        log.debug("REST request to update Review : {}", review);
        if (review.getId() == null) {
            return createReview(review, id);
        }
        Review result = reviewRepository.save(review);
        reviewSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("review", review.getId().toString()))
            .body(result);
    }

    /**
     * GET  /reviews -> get all the reviews.
     */
    @RequestMapping(value = "/reviews",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Review> getAllReviews() {
        log.debug("REST request to get all Reviews");
        return reviewRepository.findByUserIsCurrentUser();
            }

    /**
     * GET  /reviews -> get all the reviews.
     */
    @RequestMapping(value = "/reviewsbyspace",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Review> getAllReviewsBySpace() {
        log.debug("REST request to get all Reviews");
        return reviewRepository.findByUserIsCurrentUserAndSpace();
    }

    /**
     * GET  /reviews/:id -> get the "id" review.
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Review> getReview(@PathVariable Long id) {
        log.debug("REST request to get Review : {}", id);
        Review review = reviewRepository.findOne(id);
        return Optional.ofNullable(review)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /reviews/:id -> delete the "id" review.
     */
    @RequestMapping(value = "/reviews/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.debug("REST request to delete Review : {}", id);
        reviewRepository.delete(id);
        reviewSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("review", id.toString())).build();
    }

    /**
     * SEARCH  /_search/reviews/:query -> search for the review corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/reviews/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Review> searchReviews(@PathVariable String query) {
        log.debug("REST request to search Reviews for query {}", query);
        return StreamSupport
            .stream(reviewSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    /**
     * GET  /reviews/:id -> get the "id" review.
     */
    @Transactional
    @RequestMapping(value = "/space/{id}/reviews",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Review>> getReviewBySpace(@PathVariable Long id) {
        log.debug("REST request to get Review : {}", id);
        List<Review> review = reviewRepository.findReviewsBySpace(id);
        return new ResponseEntity<>(review, HttpStatus.OK);
    }

}
