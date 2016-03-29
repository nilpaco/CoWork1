package com.project.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.project.domain.Conversation;
import com.project.domain.Message;
import com.project.repository.ConversationRepository;
import com.project.repository.search.ConversationSearchRepository;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Conversation.
 */
@RestController
@RequestMapping("/api")
public class ConversationResource {

    private final Logger log = LoggerFactory.getLogger(ConversationResource.class);

    @Inject
    private ConversationRepository conversationRepository;

    @Inject
    private ConversationSearchRepository conversationSearchRepository;

    /**
     * POST  /conversations -> Create a new conversation.
     */
    @RequestMapping(value = "/conversations",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conversation> createConversation(@RequestBody Conversation conversation) throws URISyntaxException {
        log.debug("REST request to save Conversation : {}", conversation);
        if (conversation.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("conversation", "idexists", "A new conversation cannot already have an ID")).body(null);
        }
        Conversation result = conversationRepository.save(conversation);
        conversationSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/conversations/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("conversation", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /conversations -> Updates an existing conversation.
     */
    @RequestMapping(value = "/conversations",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conversation> updateConversation(@RequestBody Conversation conversation) throws URISyntaxException {
        log.debug("REST request to update Conversation : {}", conversation);
        if (conversation.getId() == null) {
            return createConversation(conversation);
        }
        Conversation result = conversationRepository.save(conversation);
        conversationSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("conversation", conversation.getId().toString()))
            .body(result);
    }

    /**
     * GET  /conversations -> get all the conversations.
     */
    @RequestMapping(value = "/conversations",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Conversation>> getAllConversations(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Conversations");
        Page<Conversation> page = conversationRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/conversations");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /conversations/:id -> get the "id" conversation.
     */
    @RequestMapping(value = "/conversations/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Conversation> getConversation(@PathVariable Long id) {
        log.debug("REST request to get Conversation : {}", id);
        Conversation conversation = conversationRepository.findOne(id);
        return Optional.ofNullable(conversation)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /conversations/:id -> delete the "id" conversation.
     */
    @RequestMapping(value = "/conversations/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteConversation(@PathVariable Long id) {
        log.debug("REST request to delete Conversation : {}", id);
        conversationRepository.delete(id);
        conversationSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("conversation", id.toString())).build();
    }

    /**
     * SEARCH  /_search/conversations/:query -> search for the conversation corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/conversations/{query:.+}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Conversation> searchConversations(@PathVariable String query) {
        log.debug("REST request to search Conversations for query {}", query);
        return StreamSupport
            .stream(conversationSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }

    @Transactional
    @RequestMapping(value = "/conversations/{id}/messages",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Set<Message>> getMessages(@PathVariable Long id) {
        log.debug("REST request to get Messages: {}", id);
        Conversation conversation = conversationRepository.findOne(id);

        if(conversation==null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(conversation.getMessages(), HttpStatus.OK);
    }

}
