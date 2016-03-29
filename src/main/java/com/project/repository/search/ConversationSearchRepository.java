package com.project.repository.search;

import com.project.domain.Conversation;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data ElasticSearch repository for the Conversation entity.
 */
public interface ConversationSearchRepository extends ElasticsearchRepository<Conversation, Long> {
}
