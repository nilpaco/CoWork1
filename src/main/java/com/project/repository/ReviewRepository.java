package com.project.repository;

import com.project.domain.Review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Review entity.
 */
public interface ReviewRepository extends JpaRepository<Review,Long> {

    @Query("select review from Review review where review.user.login = ?#{principal.username}")
    List<Review> findByUserIsCurrentUser();

    @Query("select review from Review review where review.space.user.login = ?#{principal.username}")
    List<Review> findByUserIsCurrentUserAndSpace();

    @Query("select review from Review review where review.space.id = :space_id")
    List<Review> findReviewsBySpace(@Param("space_id") Long id);

}
