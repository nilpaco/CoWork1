package com.project.repository;

import com.project.domain.Service;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Service entity.
 */
public interface ServiceRepository extends JpaRepository<Service,Long> {

}
