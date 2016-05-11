package com.project.repository;

import com.project.domain.Service;
import com.project.domain.Space;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.commons.collections.map.HashedMap;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            Criteria spaceCriteria = currentSession().createCriteria(Space.class);
            Double minPrice = 0.0;
            Double maxPrice = 0.0;
            Integer numPers = 0;
            if(parameters.containsKey("min-price") && parameters.containsKey("max-price")){
                minPrice = (Double) parameters.get("min-price");
                maxPrice = (Double) parameters.get("max-price");
                spaceCriteria.add(Restrictions.between("price", minPrice, maxPrice));
            }else if(parameters.containsKey("min-price")){
                minPrice = (Double) parameters.get("min-price");
                spaceCriteria.add(Restrictions.ge("price", minPrice));
            }else if(parameters.containsKey("max-price")){
                maxPrice = (Double) parameters.get("max-price");
                spaceCriteria.add(Restrictions.le("price", maxPrice));
            }
            if(parameters.containsKey("num-pers")){
                numPers = (Integer) parameters.get("num-pers");
                spaceCriteria.add(Restrictions.ge("personMax", numPers));
            }

/*
            if(parameters.containsKey("services")) {

                Long[] servicesLong = (Long[]) parameters.get("services");

                for (Long id : servicesLong) {
                    DetachedCriteria subquery = DetachedCriteria.forClass(Service.class, "service");
                    subquery.add(Restrictions.eq("id", id));
                    subquery.setProjection(Projections.property("id"));
                    subquery.createAlias("spaces", "space");
                    subquery.add(Restrictions.eqProperty("space.id", "space.id"));
                    spaceCriteria.add(Subqueries.exists(subquery));
                }
            }
*/
            List<Space> results = spaceCriteria.list();





/*
            for (int i=0; i<results.size(); i++) {

                Space space = results.get(i);

                Boolean found = false;

                for (String currentService : services) {


                    for (Service spaceService: space.getServices()){
                        if(currentService.equals(spaceService.getName())){
                            found = true;
                        }
                    }

                }

                if(!found){
                    results.remove(space);
                }


            }
*/





            return results;

        }

        private void addFilter(String param, Object value, Criteria playerCriteria) {

            if (param.equals("id") || param.equals("posicionCampo")) {
                playerCriteria.add(Restrictions.eq(param, value));
            }

            if (param.equals("baskets")) {
                playerCriteria.add(Restrictions.ge(param, value));
            }

            if (param.equals("rebotes")) {
                playerCriteria.add(Restrictions.le(param, value));
            }

        }

}
