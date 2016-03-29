package com.project.web.rest;

import com.project.Application;
import com.project.domain.Favorite;
import com.project.repository.FavoriteRepository;
import com.project.repository.search.FavoriteSearchRepository;

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
 * Test class for the FavoriteResource REST controller.
 *
 * @see FavoriteResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class FavoriteResourceIntTest {


    private static final Boolean DEFAULT_LIKED = false;
    private static final Boolean UPDATED_LIKED = true;

    @Inject
    private FavoriteRepository favoriteRepository;

    @Inject
    private FavoriteSearchRepository favoriteSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restFavoriteMockMvc;

    private Favorite favorite;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        FavoriteResource favoriteResource = new FavoriteResource();
        ReflectionTestUtils.setField(favoriteResource, "favoriteSearchRepository", favoriteSearchRepository);
        ReflectionTestUtils.setField(favoriteResource, "favoriteRepository", favoriteRepository);
        this.restFavoriteMockMvc = MockMvcBuilders.standaloneSetup(favoriteResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        favorite = new Favorite();
        favorite.setLiked(DEFAULT_LIKED);
    }

    @Test
    @Transactional
    public void createFavorite() throws Exception {
        int databaseSizeBeforeCreate = favoriteRepository.findAll().size();

        // Create the Favorite

        restFavoriteMockMvc.perform(post("/api/favorites")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
                .andExpect(status().isCreated());

        // Validate the Favorite in the database
        List<Favorite> favorites = favoriteRepository.findAll();
        assertThat(favorites).hasSize(databaseSizeBeforeCreate + 1);
        Favorite testFavorite = favorites.get(favorites.size() - 1);
        assertThat(testFavorite.getLiked()).isEqualTo(DEFAULT_LIKED);
    }

    @Test
    @Transactional
    public void getAllFavorites() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get all the favorites
        restFavoriteMockMvc.perform(get("/api/favorites?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(favorite.getId().intValue())))
                .andExpect(jsonPath("$.[*].liked").value(hasItem(DEFAULT_LIKED.booleanValue())));
    }

    @Test
    @Transactional
    public void getFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

        // Get the favorite
        restFavoriteMockMvc.perform(get("/api/favorites/{id}", favorite.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(favorite.getId().intValue()))
            .andExpect(jsonPath("$.liked").value(DEFAULT_LIKED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingFavorite() throws Exception {
        // Get the favorite
        restFavoriteMockMvc.perform(get("/api/favorites/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

		int databaseSizeBeforeUpdate = favoriteRepository.findAll().size();

        // Update the favorite
        favorite.setLiked(UPDATED_LIKED);

        restFavoriteMockMvc.perform(put("/api/favorites")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(favorite)))
                .andExpect(status().isOk());

        // Validate the Favorite in the database
        List<Favorite> favorites = favoriteRepository.findAll();
        assertThat(favorites).hasSize(databaseSizeBeforeUpdate);
        Favorite testFavorite = favorites.get(favorites.size() - 1);
        assertThat(testFavorite.getLiked()).isEqualTo(UPDATED_LIKED);
    }

    @Test
    @Transactional
    public void deleteFavorite() throws Exception {
        // Initialize the database
        favoriteRepository.saveAndFlush(favorite);

		int databaseSizeBeforeDelete = favoriteRepository.findAll().size();

        // Get the favorite
        restFavoriteMockMvc.perform(delete("/api/favorites/{id}", favorite.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Favorite> favorites = favoriteRepository.findAll();
        assertThat(favorites).hasSize(databaseSizeBeforeDelete - 1);
    }
}
