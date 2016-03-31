package com.project.repository;

import com.project.domain.Favorite;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Favorite entity.
 */
public interface FavoriteRepository extends JpaRepository<Favorite,Long> {

    @Query("select favorite from Favorite favorite where favorite.user.login = ?#{principal.username} and favorite.liked = true")
    Page<Favorite> findByUserIsCurrentUserAndLiked(Pageable pageable);

    @Query("select favorite from Favorite favorite where favorite.user.login = ?#{principal.username} AND favorite.space.id = :space_id")
    Favorite findExistUserLiked(@Param("space_id") Long id);

}
