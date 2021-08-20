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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RatingServiceTest {

    @MockBean
    private RatingRepository ratingRepositoryMock;

    @Autowired
    private IRatingService ratingService;

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
    @DisplayName("create tests")
    class CreateTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex rating " +
                "THEN an exception is thrown")
        void createTest_WithException() {
            //GIVEN
            when(ratingRepositoryMock.save(any(Rating.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ratingService.create(ratingDTOWithValues));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .save(any(Rating.class));
        }
    }


    @Nested
    @DisplayName("findAll tests")
    class FindAllTest {

        @Test
        @DisplayName("GIVEN no rating in DB " +
                "WHEN getting all the rating " +
                "THEN the returned value is an empty list of rating")
        void findAllTest_WithNoDataInDB() {
            //GIVEN
            List<Rating> ratingList = new ArrayList<>();
            when(ratingRepositoryMock.findAll()).thenReturn(ratingList);

            //THEN
            List<RatingDTO> ratingDTOList = ratingService.findAll();
            assertThat(ratingDTOList).isEmpty();

            verify(ratingRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findById tests")
    class FindByIdTest {

        @Test
        @DisplayName("GIVEN no rating in DB for a specified id " +
                "WHEN getting all the rating " +
                "THEN the returned value is a null rating")
        void findByIdTest_WithNoDataInDB() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> ratingService.findById(TestConstants.EXISTING_RATING_ID));
            assertEquals(PoseidonExceptionsConstants.RATING_ID_NOT_VALID
                    + TestConstants.EXISTING_RATING_ID, exception.getMessage());

            verify(ratingRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("update tests")
    class UpdateTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a rating " +
                "THEN an exception is thrown")
        void updateTest_WithException() {
            //GIVEN
            when(ratingRepositoryMock.save(any(Rating.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ratingService.update(ratingDTOWithValues));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .save(any(Rating.class));
        }
    }


    @Nested
    @DisplayName("delete tests")
    class DeleteTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a rating " +
                "THEN an exception is thrown")
        void deleteTest_WithException() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(ratingInDb));
            doThrow(new RuntimeException()).when(ratingRepositoryMock).delete(any(Rating.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ratingService.delete(ratingInDb.getId()));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ratingRepositoryMock, Mockito.times(1))
                    .delete(any(Rating.class));
        }


        @Test
        @DisplayName("GIVEN no rating in DB for the specified id " +
                "WHEN deleting a rating " +
                "THEN an exception is thrown")
        void deleteTest_WithNoDataInDb() {
            //GIVEN
            when(ratingRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> ratingService.delete(ratingInDb.getId()));

            verify(ratingRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ratingRepositoryMock, Mockito.times(0))
                    .delete(any(Rating.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a rating " +
                "THEN an exception is thrown")
        void deleteTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> ratingService.delete(null));

            verify(ratingRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(ratingRepositoryMock, Mockito.times(0))
                    .delete(any(Rating.class));
        }
    }
}
