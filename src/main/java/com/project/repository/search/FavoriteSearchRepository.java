package com.project.repository.search;

import com.project.domain.Favorite;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Favorite entity.
 */
public interface FavoriteSearchRepository extends ElasticsearchRepository<Favorite, Long> {
}
