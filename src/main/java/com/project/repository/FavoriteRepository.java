package com.project.repository;

import com.project.domain.Favorite;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Favorite entity.
 */
public interface FavoriteRepository extends JpaRepository<Favorite,Long> {

    @Query("select favorite from Favorite favorite where favorite.user.login = ?#{principal.username}")
    List<Favorite> findByUserIsCurrentUser();

    @Query("select favorite from Favorite favorite where favorite.user.login = ?#{principal.username} AND favorite.space.id = :space_id")
    Favorite findExistUserLiked(@Param("space_id") Long id);

}
