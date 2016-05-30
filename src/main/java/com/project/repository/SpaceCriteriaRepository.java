package com.project.repository;

import com.project.domain.Service;
import com.project.domain.Space;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by nilpanescoll on 9/5/16.
 */

@Repository
public class SpaceCriteriaRepository {
        @PersistenceContext
        EntityManager entityManager;

        protected Session currentSession() {
            return entityManager.unwrap(Session.class);
        }

        public List<Space> findBySpaceBetweenPrice(int minPrice, int maxPrice, int numPers) {
            return (List<Space>) currentSession()
                .createCriteria(Space.class)
                .add(Restrictions.between("price", minPrice, maxPrice))
                .add(Restrictions.eq("personMax", numPers)).list();
        }

        public List<Space> findByParameters(Map<String, Object> parameters) {

            return filterSpaces(parameters);

        }

    private List<Space> filterSpaces(Map<String, Object> parameters) {
        Criteria spaceCriteria = currentSession().createCriteria(Space.class);
        spaceCriteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
        Double minPrice = 0.0;
        Double maxPrice = 0.0;
        Integer numPers = 0;
        if(parameters.containsKey("min-price") && parameters.containsKey("maxprice")){
            minPrice = (Double) parameters.get("min-price");
            maxPrice = (Double) parameters.get("maxprice");
            spaceCriteria.add(Restrictions.between("price", minPrice, maxPrice));
        }else if(parameters.containsKey("min-price")){
            minPrice = (Double) parameters.get("min-price");
            spaceCriteria.add(Restrictions.ge("price", minPrice));
        }else if(parameters.containsKey("maxprice")){
            maxPrice = (Double) parameters.get("maxprice");
            spaceCriteria.add(Restrictions.le("price", maxPrice));
        }
        if(parameters.containsKey("num-pers")){
            numPers = (Integer) parameters.get("num-pers");
            spaceCriteria.add(Restrictions.ge("personMax", numPers));
        }
        List<Space> results = spaceCriteria.list();

        if(parameters.containsKey("services")){

            List<Long> requiredServices = (List<Long>) parameters.get("services");

            List<Space> fresults = results.parallelStream().filter(
                (space) -> space.getServices()
                    .stream()
                    .mapToLong(Service::getId)
                    .boxed().collect(Collectors.toList())
                    .containsAll(requiredServices)
            ).collect(Collectors.toList());

            return fresults;

        }

        return results;
    }

}
