package com.nnk.springboot.services;

import com.nnk.springboot.DTO.RatingDTO;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.contracts.IRatingService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RatingServiceTest {

    @MockBean
    RatingRepository ratingRepositoryMock;

    @Autowired
    IRatingService ratingService;

    private static RatingDTO ratingDTOWithValues;

    private static Rating ratingInDb;

    @BeforeAll
    static void setUp() {
        ratingDTOWithValues = new RatingDTO();
        ratingDTOWithValues.setId(TestConstants.EXISTING_RATING_ID);
        ratingDTOWithValues.setMoodysRating(TestConstants.NEW_RATING_MOODYS_RATING);
        ratingDTOWithValues.setSandPRating(TestConstants.NEW_RATING_SANDP_RATING);
        ratingDTOWithValues.setFitchRating(TestConstants.NEW_RATING_FITCH_RATING);
        ratingDTOWithValues.setOrderNumber(TestConstants.NEW_RATING_ORDER_NUMBER);

        ratingInDb = new Rating();
        ratingInDb.setId(ratingDTOWithValues.getId());
        ratingInDb.setMoodysRating(ratingDTOWithValues.getMoodysRating());
        ratingInDb.setSandPRating(ratingDTOWithValues.getSandPRating());
        ratingInDb.setFitchRating(ratingDTOWithValues.getFitchRating());
        ratingInDb.setOrderNumber(ratingDTOWithValues.getOrderNumber());
    }

    @Nested
    @DisplayName("CreateRating tests")
    class CreateRatingTest {

        @Test
        @DisplayName("GIVEN a new rating (DTO) to add " +
                "WHEN saving this new rating " +
                "THEN the returned value is the added rating (DTO)")
        void createRatingTest_WithSuccess() {
            //GIVEN
            when(ratingRepositoryMock.save(any(Rating.class))).thenReturn(ratingInDb);

            //WHEN
            Optional<RatingDTO> createdRatingDTO = ratingService.createRating(ratingDTOWithValues);

            //THEN
            assertTrue(createdRatingDTO.isPresent());
            assertNotNull(createdRatingDTO.get().getId());
            assertEquals(ratingDTOWithValues.toString(), createdRatingDTO.get().toString());

            verify(ratingRepositoryMock, Mockito.times(1))
                    .save(any(Rating.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex rating " +
                "THEN an exception is thrown")
        void createRatingTest_WithException() {
            //GIVEN
            when(ratingRepositoryMock.save(any(Rating.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ratingService.createRating(ratingDTOWithValues));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .save(any(Rating.class));
        }
    }


    @Nested
    @DisplayName("findAllRating tests")
    class FindAllRatingTest {

        @Test
        @DisplayName("GIVEN rating in DB " +
                "WHEN getting all the rating " +
                "THEN the returned value is the list of rating")
        void findAllRatingTest_WithDataInDB() {
            //GIVEN
            List<Rating> ratingList = new ArrayList<>();
            ratingList.add(ratingInDb);
            when(ratingRepositoryMock.findAll()).thenReturn(ratingList);

            //THEN
            List<RatingDTO> ratingDTOList = ratingService.findAllRating();
            assertEquals(1, ratingDTOList.size());
            assertEquals(ratingInDb.getId(), ratingDTOList.get(0).getId());

            verify(ratingRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN no rating in DB " +
                "WHEN getting all the rating " +
                "THEN the returned value is an empty list of rating")
        void findAllRatingTest_WithNoDataInDB() {
            //GIVEN
            List<Rating> ratingList = new ArrayList<>();
            when(ratingRepositoryMock.findAll()).thenReturn(ratingList);

            //THEN
            List<RatingDTO> ratingDTOList = ratingService.findAllRating();
            assertThat(ratingDTOList).isEmpty();

            verify(ratingRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findRatingById tests")
    class FindRatingByIdTest {

        @Test
        @DisplayName("GIVEN rating in DB for a specified id" +
                "WHEN getting the rating on id " +
                "THEN the returned value is the rating")
        void findRatingByIdTest_WithDataInDB() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.of(ratingInDb));

            //THEN
            RatingDTO ratingDTO = ratingService.findRatingById(TestConstants.EXISTING_RATING_ID);
            assertEquals(ratingInDb.getId(), ratingDTO.getId());
            assertEquals(ratingInDb.getMoodysRating(), ratingDTO.getMoodysRating());

            verify(ratingRepositoryMock, Mockito.times(1)).findById(anyInt());
        }

        @Test
        @DisplayName("GIVEN no rating in DB for a specified id " +
                "WHEN getting all the rating " +
                "THEN the returned value is a null rating")
        void findRatingByIdTest_WithNoDataInDB() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> ratingService.findRatingById(TestConstants.EXISTING_RATING_ID));
            assertEquals(PoseidonExceptionsConstants.RATING_ID_NOT_VALID
                    + TestConstants.EXISTING_RATING_ID, exception.getMessage());

            verify(ratingRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("updateRating tests")
    class UpdateRatingTest {

        @Test
        @DisplayName("GIVEN a rating to update " +
                "WHEN updating this rating " +
                "THEN the returned value is the updated rating")
        void updateRatingTest_WithSuccess() {
            //GIVEN
            when(ratingRepositoryMock.save(any(Rating.class))).thenReturn(ratingInDb);

            //WHEN
            RatingDTO createdRatingDTO = ratingService.updateRating(ratingDTOWithValues);

            //THEN
            assertEquals(ratingDTOWithValues.toString(), createdRatingDTO.toString());

            verify(ratingRepositoryMock, Mockito.times(1))
                    .save(any(Rating.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a rating " +
                "THEN an exception is thrown")
        void updateRatingTest_WithException() {
            //GIVEN
            when(ratingRepositoryMock.save(any(Rating.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ratingService.updateRating(ratingDTOWithValues));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .save(any(Rating.class));
        }
    }


    @Nested
    @DisplayName("deleteRating tests")
    class DeleteRatingTest {

        @Test
        @DisplayName("GIVEN a rating to delete " +
                "WHEN deleting this rating " +
                "THEN nothing is returned")
        void deleteRatingTest_WithSuccess() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(ratingInDb));

            //WHEN
            ratingService.deleteRating(ratingInDb.getId());

            //THEN
            verify(ratingRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ratingRepositoryMock, Mockito.times(1))
                    .delete(any(Rating.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a rating " +
                "THEN an exception is thrown")
        void deleteRatingTest_WithException() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(ratingInDb));
            doThrow(new RuntimeException()).when(ratingRepositoryMock).delete(any(Rating.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ratingService.deleteRating(ratingInDb.getId()));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ratingRepositoryMock, Mockito.times(1))
                    .delete(any(Rating.class));
        }


        @Test
        @DisplayName("GIVEN no rating in DB for the specified id " +
                "WHEN deleting a rating " +
                "THEN an exception is thrown")
        void deleteRatingTest_WithNoDataInDb() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> ratingService.deleteRating(ratingInDb.getId()));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ratingRepositoryMock, Mockito.times(0))
                    .delete(any(Rating.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a rating " +
                "THEN an exception is thrown")
        void deleteRatingTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> ratingService.deleteRating(null));

            verify(ratingRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(ratingRepositoryMock, Mockito.times(0))
                    .delete(any(Rating.class));
        }
    }
}
