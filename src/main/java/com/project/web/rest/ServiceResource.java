package com.project.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.project.domain.Service;
import com.project.repository.ServiceRepository;
import com.project.repository.search.ServiceSearchRepository;
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
 * REST controller for managing Service.
 */
@RestController
@RequestMapping("/api")
public class ServiceResource {

    private final Logger log = LoggerFactory.getLogger(ServiceResource.class);
        
    @Inject
    private ServiceRepository serviceRepository;
    
    @Inject
    private ServiceSearchRepository serviceSearchRepository;
    
    /**
     * POST  /services -> Create a new service.
     */
    @RequestMapping(value = "/services",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Service> createService(@RequestBody Service service) throws URISyntaxException {
        log.debug("REST request to save Service : {}", service);
        if (service.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("service", "idexists", "A new service cannot already have an ID")).body(null);
        }
        Service result = serviceRepository.save(service);
        serviceSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/services/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("service", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /services -> Updates an existing service.
     */
    @RequestMapping(value = "/services",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Service> updateService(@RequestBody Service service) throws URISyntaxException {
        log.debug("REST request to update Service : {}", service);
        if (service.getId() == null) {
            return createService(service);
        }
        Service result = serviceRepository.save(service);
        serviceSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("service", service.getId().toString()))
            .body(result);
    }

    /**
     * GET  /services -> get all the services.
     */
    @RequestMapping(value = "/services",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Service>> getAllServices(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Services");
        Page<Service> page = serviceRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/services");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /services/:id -> get the "id" service.
     */
    @RequestMapping(value = "/services/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Service> getService(@PathVariable Long id) {
        log.debug("REST request to get Service : {}", id);
        Service service = serviceRepository.findOne(id);
        return Optional.ofNullable(service)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /services/:id -> delete the "id" service.
     */
    @RequestMapping(value = "/services/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        log.debug("REST request to delete Service : {}", id);
        serviceRepository.delete(id);
        serviceSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("service", id.toString())).build();
    }

    /**
     * SEARCH  /_search/services/:query -> search for the service corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/services/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Service> searchServices(@PathVariable String query) {
        log.debug("REST request to search Services for query {}", query);
        return StreamSupport
            .stream(serviceSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
