package com.nnk.springboot.integration;

import com.nnk.springboot.DTO.RatingDTO;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.contracts.IRatingService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest
public class RatingServiceIT {
    @Autowired
    IRatingService ratingService;

    @Autowired
    RatingRepository ratingRepository;

    private Rating ratingInDb;
    private RatingDTO ratingDTO;

    @BeforeEach
    private void initPerTest() {
        //init a rating in DB for test
        ratingInDb = new Rating();
        ratingInDb.setMoodysRating(TestConstants.EXISTING_RATING_MOODYS_RATING);
        ratingInDb.setSandPRating(TestConstants.EXISTING_RATING_SANDP_RATING);
        ratingInDb.setFitchRating(TestConstants.EXISTING_RATING_FITCH);
        ratingInDb.setOrderNumber(TestConstants.EXISTING_RATING_ORDER_NUMBER);
        ratingInDb = ratingRepository.save(ratingInDb);

        //init common part of ratingDTO to create/update
        ratingDTO = new RatingDTO();
        ratingDTO.setMoodysRating(TestConstants.EXISTING_RATING_MOODYS_RATING);
        ratingDTO.setSandPRating(TestConstants.EXISTING_RATING_SANDP_RATING);
        ratingDTO.setFitchRating(TestConstants.EXISTING_RATING_FITCH);
        ratingDTO.setOrderNumber(TestConstants.EXISTING_RATING_ORDER_NUMBER);
    }

    @AfterEach
    private void cleanPerTest(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanUp")) {
            return;
        }
        //clean DB at the end of the test by deleting the rating created at initialization
        ratingRepository.deleteById(ratingInDb.getId());
    }

    @Test
    @DisplayName("WHEN creating a new rating with correct informations  " +
            "THEN the returned value is the added rating, " +
            "AND the rating is added in DB")
    public void createRatingIT_WithSuccess() {

        //WHEN
        Optional<RatingDTO> ratingDTOCreated = ratingService.createRating(ratingDTO);

        //THEN
        assertTrue(ratingDTOCreated.isPresent());
        assertNotNull(ratingDTOCreated.get().getId());
        assertEquals(ratingDTO.getOrderNumber(), ratingDTOCreated.get().getOrderNumber());

        //cleaning of DB at the end of the test by deleting the rating created during the test
        ratingRepository.deleteById(ratingDTOCreated.get().getId());
    }


    @Test
    @DisplayName("WHEN asking for the list of all rating " +
            "THEN the returned value is the list of all rating in DB")
    public void findAllRatingIT_WithSuccess() {

        //WHEN
        List<RatingDTO> ratingDTOList = ratingService.findAllRating();

        //THEN
        assertThat(ratingDTOList.size()).isGreaterThan(0);
        assertEquals(ratingInDb.getId(), ratingDTOList.get(0).getId());
    }


    @Test
    @DisplayName("WHEN asking for a rating with a specified id " +
            "THEN the returned value is the rating in DB")
    public void findRatingByIdIT_WithSuccess() {

        //WHEN
        RatingDTO ratingDTO = ratingService.findRatingById(ratingInDb.getId());

        //THEN
        assertEquals(ratingInDb.getId(), ratingDTO.getId());
        assertEquals(ratingInDb.getMoodysRating(), ratingDTO.getMoodysRating());
        assertEquals(ratingInDb.getSandPRating(), ratingDTO.getSandPRating());
        assertEquals(ratingInDb.getFitchRating(), ratingDTO.getFitchRating());
        assertEquals(ratingInDb.getOrderNumber(), ratingDTO.getOrderNumber());
    }


    @Test
    @DisplayName("WHEN updating a rating with correct informations  " +
            "THEN the returned value is the updated rating, " +
            "AND the rating is updated in DB")
    public void updateRatingIT_WithSuccess() {

        //GIVEN
        ratingDTO.setId(ratingInDb.getId());
        ratingDTO.setOrderNumber(TestConstants.NEW_RATING_ORDER_NUMBER);

        //WHEN
        RatingDTO ratingDTOUpdated = ratingService.updateRating(ratingDTO);
        Optional<Rating> ratingUpdated = ratingRepository.findById(ratingInDb.getId());

        //THEN
        assertNotNull(ratingDTOUpdated);
        assertEquals(ratingDTO.getOrderNumber(), ratingDTOUpdated.getOrderNumber());

        assertTrue(ratingUpdated.isPresent());
        assertEquals(ratingDTO.getOrderNumber(), ratingUpdated.get().getOrderNumber());
    }


    @Test
    @Tag("SkipCleanUp")
    @DisplayName("WHEN deleting a rating with correct informations  " +
            "THEN the rating is deleted in DB")
    public void deleteRatingIT_WithSuccess() {

        //WHEN
        ratingService.deleteRating(ratingInDb.getId());
        Optional<Rating> ratingDeleted = ratingRepository.findById(ratingInDb.getId());

        //THEN
        assertFalse(ratingDeleted.isPresent());
    }
}
