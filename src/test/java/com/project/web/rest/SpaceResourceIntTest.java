package com.project.web.rest;

import com.project.Application;
import com.project.domain.Space;
import com.project.repository.SpaceRepository;
import com.project.repository.search.SpaceSearchRepository;

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
 * Test class for the SpaceResource REST controller.
 *
 * @see SpaceResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SpaceResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    private static final Double DEFAULT_PRICE = 1D;
    private static final Double UPDATED_PRICE = 2D;

    private static final Integer DEFAULT_PERSON_MAX = 1;
    private static final Integer UPDATED_PERSON_MAX = 2;
    private static final String DEFAULT_STREET_ADDRESS = "AAAAA";
    private static final String UPDATED_STREET_ADDRESS = "BBBBB";

    private static final Double DEFAULT_LAT = 1D;
    private static final Double UPDATED_LAT = 2D;

    private static final Double DEFAULT_LNG = 1D;
    private static final Double UPDATED_LNG = 2D;

    @Inject
    private SpaceRepository spaceRepository;

    @Inject
    private SpaceSearchRepository spaceSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSpaceMockMvc;

    private Space space;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SpaceResource spaceResource = new SpaceResource();
        ReflectionTestUtils.setField(spaceResource, "spaceSearchRepository", spaceSearchRepository);
        ReflectionTestUtils.setField(spaceResource, "spaceRepository", spaceRepository);
        this.restSpaceMockMvc = MockMvcBuilders.standaloneSetup(spaceResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        space = new Space();
        space.setName(DEFAULT_NAME);
        space.setDescription(DEFAULT_DESCRIPTION);
        space.setPrice(DEFAULT_PRICE);
        space.setPersonMax(DEFAULT_PERSON_MAX);
        space.setStreetAddress(DEFAULT_STREET_ADDRESS);
        space.setLat(DEFAULT_LAT);
        space.setLng(DEFAULT_LNG);
    }

    @Test
    @Transactional
    public void createSpace() throws Exception {
        int databaseSizeBeforeCreate = spaceRepository.findAll().size();

        // Create the Space

        restSpaceMockMvc.perform(post("/api/spaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(space)))
                .andExpect(status().isCreated());

        // Validate the Space in the database
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeCreate + 1);
        Space testSpace = spaces.get(spaces.size() - 1);
        assertThat(testSpace.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSpace.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testSpace.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testSpace.getPersonMax()).isEqualTo(DEFAULT_PERSON_MAX);
        assertThat(testSpace.getStreetAddress()).isEqualTo(DEFAULT_STREET_ADDRESS);
        assertThat(testSpace.getLat()).isEqualTo(DEFAULT_LAT);
        assertThat(testSpace.getLng()).isEqualTo(DEFAULT_LNG);
    }

    @Test
    @Transactional
    public void getAllSpaces() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

        // Get all the spaces
        restSpaceMockMvc.perform(get("/api/spaces?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(space.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
                .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.doubleValue())))
                .andExpect(jsonPath("$.[*].personMax").value(hasItem(DEFAULT_PERSON_MAX)))
                .andExpect(jsonPath("$.[*].streetAddress").value(hasItem(DEFAULT_STREET_ADDRESS.toString())))
                .andExpect(jsonPath("$.[*].lat").value(hasItem(DEFAULT_LAT.doubleValue())))
                .andExpect(jsonPath("$.[*].lng").value(hasItem(DEFAULT_LNG.doubleValue())));
    }

    @Test
    @Transactional
    public void getSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

        // Get the space
        restSpaceMockMvc.perform(get("/api/spaces/{id}", space.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(space.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.doubleValue()))
            .andExpect(jsonPath("$.personMax").value(DEFAULT_PERSON_MAX))
            .andExpect(jsonPath("$.streetAddress").value(DEFAULT_STREET_ADDRESS.toString()))
            .andExpect(jsonPath("$.lat").value(DEFAULT_LAT.doubleValue()))
            .andExpect(jsonPath("$.lng").value(DEFAULT_LNG.doubleValue()));
    }

    @Test
    @Transactional
    public void getNonExistingSpace() throws Exception {
        // Get the space
        restSpaceMockMvc.perform(get("/api/spaces/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

		int databaseSizeBeforeUpdate = spaceRepository.findAll().size();

        // Update the space
        space.setName(UPDATED_NAME);
        space.setDescription(UPDATED_DESCRIPTION);
        space.setPrice(UPDATED_PRICE);
        space.setPersonMax(UPDATED_PERSON_MAX);
        space.setStreetAddress(UPDATED_STREET_ADDRESS);
        space.setLat(UPDATED_LAT);
        space.setLng(UPDATED_LNG);

        restSpaceMockMvc.perform(put("/api/spaces")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(space)))
                .andExpect(status().isOk());

        // Validate the Space in the database
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeUpdate);
        Space testSpace = spaces.get(spaces.size() - 1);
        assertThat(testSpace.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSpace.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testSpace.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testSpace.getPersonMax()).isEqualTo(UPDATED_PERSON_MAX);
        assertThat(testSpace.getStreetAddress()).isEqualTo(UPDATED_STREET_ADDRESS);
        assertThat(testSpace.getLat()).isEqualTo(UPDATED_LAT);
        assertThat(testSpace.getLng()).isEqualTo(UPDATED_LNG);
    }

    @Test
    @Transactional
    public void deleteSpace() throws Exception {
        // Initialize the database
        spaceRepository.saveAndFlush(space);

		int databaseSizeBeforeDelete = spaceRepository.findAll().size();

        // Get the space
        restSpaceMockMvc.perform(delete("/api/spaces/{id}", space.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Space> spaces = spaceRepository.findAll();
        assertThat(spaces).hasSize(databaseSizeBeforeDelete - 1);
    }
}
