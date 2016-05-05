package com.project.repository;

import com.project.domain.Image;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Image entity.
 */
public interface ImageRepository extends JpaRepository<Image,Long> {

    @Query("select image from Image image where image.space.id = :space_id")
    List<Image> findImagesBySpace(@Param("space_id") Long id);

}
