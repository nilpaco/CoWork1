package com.project.web.rest;

import com.project.Application;
import com.project.domain.Conversation;
import com.project.repository.ConversationRepository;
import com.project.repository.search.ConversationSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the ConversationResource REST controller.
 *
 * @see ConversationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class ConversationResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private ConversationRepository conversationRepository;

    @Inject
    private ConversationSearchRepository conversationSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restConversationMockMvc;

    private Conversation conversation;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ConversationResource conversationResource = new ConversationResource();
        ReflectionTestUtils.setField(conversationResource, "conversationSearchRepository", conversationSearchRepository);
        ReflectionTestUtils.setField(conversationResource, "conversationRepository", conversationRepository);
        this.restConversationMockMvc = MockMvcBuilders.standaloneSetup(conversationResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        conversation = new Conversation();
        conversation.setName(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createConversation() throws Exception {
        int databaseSizeBeforeCreate = conversationRepository.findAll().size();

        // Create the Conversation

        restConversationMockMvc.perform(post("/api/conversations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conversation)))
                .andExpect(status().isCreated());

        // Validate the Conversation in the database
        List<Conversation> conversations = conversationRepository.findAll();
        assertThat(conversations).hasSize(databaseSizeBeforeCreate + 1);
        Conversation testConversation = conversations.get(conversations.size() - 1);
        assertThat(testConversation.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllConversations() throws Exception {
        // Initialize the database
        conversationRepository.saveAndFlush(conversation);

        // Get all the conversations
        restConversationMockMvc.perform(get("/api/conversations?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(conversation.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getConversation() throws Exception {
        // Initialize the database
        conversationRepository.saveAndFlush(conversation);

        // Get the conversation
        restConversationMockMvc.perform(get("/api/conversations/{id}", conversation.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(conversation.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingConversation() throws Exception {
        // Get the conversation
        restConversationMockMvc.perform(get("/api/conversations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateConversation() throws Exception {
        // Initialize the database
        conversationRepository.saveAndFlush(conversation);

		int databaseSizeBeforeUpdate = conversationRepository.findAll().size();

        // Update the conversation
        conversation.setName(UPDATED_NAME);

        restConversationMockMvc.perform(put("/api/conversations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(conversation)))
                .andExpect(status().isOk());

        // Validate the Conversation in the database
        List<Conversation> conversations = conversationRepository.findAll();
        assertThat(conversations).hasSize(databaseSizeBeforeUpdate);
        Conversation testConversation = conversations.get(conversations.size() - 1);
        assertThat(testConversation.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteConversation() throws Exception {
        // Initialize the database
        conversationRepository.saveAndFlush(conversation);

		int databaseSizeBeforeDelete = conversationRepository.findAll().size();

        // Get the conversation
        restConversationMockMvc.perform(delete("/api/conversations/{id}", conversation.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Conversation> conversations = conversationRepository.findAll();
        assertThat(conversations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
