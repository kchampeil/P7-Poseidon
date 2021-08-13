package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.RatingDTO;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.services.contracts.IRatingService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = RatingController.class)
class RatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRatingService ratingServiceMock;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceMock;

    @MockBean
    private PasswordEncoder passwordEncoderMock;

    private static RatingDTO ratingDTO;

    @BeforeAll
    static void setUp() {
        ratingDTO = new RatingDTO();
        ratingDTO.setId(TestConstants.NEW_RATING_ID);
        ratingDTO.setMoodysRating(TestConstants.NEW_RATING_MOODYS_RATING);
        ratingDTO.setSandPRating(TestConstants.NEW_RATING_SANDP_RATING);
        ratingDTO.setFitchRating(TestConstants.NEW_RATING_FITCH_RATING);
        ratingDTO.setOrderNumber(TestConstants.NEW_RATING_ORDER_NUMBER);
    }

    @Nested
    @DisplayName("home tests")
    class HomeTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN asking for the rating list page while logged in " +
                " THEN return status is ok and the expected view is the rating list page")
        void homeTest_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/rating/list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ratingAll"))
                    .andExpect(view().name("rating/list"));

            verify(ratingServiceMock, Mockito.times(1))
                    .findAllRating();
        }


        @Test
        @DisplayName("WHEN asking for the rating list page while not logged in " +
                " THEN return status is Found (302) and the expected view is the login page")
        void homeTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/rating/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userDetailsServiceMock, Mockito.times(0))
                    .loadUserByUsername(anyString());
            verify(ratingServiceMock, Mockito.times(0))
                    .findAllRating();
        }
    }

    @Nested
    @DisplayName("addRatingForm tests")
    class AddRatingFormTest {
        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /rating/add request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the rating add form initialized")
        void addRatingFormTest_WithSuccess_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/rating/add"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("rating"))
                    .andExpect(view().name("rating/add"));
        }

        @Test
        @DisplayName("WHEN processing a GET /rating/add request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is login page")
        void addRatingFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/rating/add"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }


    @Nested
    @DisplayName("validate tests")
    class ValidateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a new rating to add " +
                "WHEN processing a POST /rating/validate request for this rating " +
                "THEN return status is found (302) " +
                "AND the expected view is the rating list page with rating list updated")
        void validateTest_WithSuccess() throws Exception {
            //GIVEN
            when(ratingServiceMock.createRating(any(RatingDTO.class)))
                    .thenReturn(Optional.of(ratingDTO));

            //WHEN-THEN
            mockMvc.perform(post("/rating/validate")
                    .param("moodysRating", ratingDTO.getMoodysRating())
                    .param("sandPRating", ratingDTO.getSandPRating())
                    .param("fitchRating", ratingDTO.getFitchRating())
                    .param("orderNumber", ratingDTO.getOrderNumber().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/rating/list"));

            verify(ratingServiceMock, Mockito.times(1))
                    .createRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new rating to add with missing account" +
                "WHEN processing a POST /rating/validate request for this rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/add page filled with entered rating")
        void validateTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/rating/validate")
                    .param("moodysRating", "")
                    .param("sandPRating", TestConstants.NEW_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.NEW_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("rating"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("rating", "moodysRating", "NotBlank"))
                    .andExpect(view().name("rating/add"));

            verify(ratingServiceMock, Mockito.times(0))
                    .createRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new rating to add with invalid sandPRating (too long) " +
                "WHEN processing a POST /rating/validate request for this rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/add page filled with entered rating")
        void validateTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/rating/validate")
                    .param("moodysRating", TestConstants.NEW_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.NEW_RATING_SANDP_RATING_WITH_TOO_LONG_SIZE)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.NEW_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("rating"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("rating", "sandPRating", "Size"))
                    .andExpect(view().name("rating/add"));

            verify(ratingServiceMock, Mockito.times(0))
                    .createRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when saving the new rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/add page filled with entered rating")
        void validateTest_WithException() throws Exception {
            //GIVEN
            when(ratingServiceMock.createRating(any(RatingDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/rating/validate")
                    .param("moodysRating", TestConstants.NEW_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.NEW_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.NEW_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("rating/add"));

            verify(ratingServiceMock, Mockito.times(1))
                    .createRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when saving the new rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/add page filled with entered rating")
        void validateTest_WithNoReturnedRatingAfterSaving() throws Exception {
            //GIVEN
            when(ratingServiceMock.createRating(any(RatingDTO.class)))
                    .thenReturn(Optional.empty());

            //WHEN-THEN
            mockMvc.perform(post("/rating/validate")
                    .param("moodysRating", TestConstants.NEW_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.NEW_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.NEW_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("rating/add"));

            verify(ratingServiceMock, Mockito.times(1))
                    .createRating(any(RatingDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /rating/validate request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void validateTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/rating/validate")
                    .param("moodysRating", TestConstants.NEW_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.NEW_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.NEW_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ratingServiceMock, Mockito.times(0))
                    .createRating(any(RatingDTO.class));
        }
    }


    @Nested
    @DisplayName("showUpdateForm tests")
    class ShowUpdateFormTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /rating/update/{id} request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the rating update form initialized")
        void showUpdateFormTest_WithSuccess_LoggedIn() throws Exception {
            //GIVEN
            when(ratingServiceMock.findRatingById(anyInt()))
                    .thenReturn(ratingDTO);

            //WHEN-THEN
            mockMvc.perform(get("/rating/update/{id}", anyInt()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("rating"))
                    .andExpect(view().name("rating/update"));

            verify(ratingServiceMock, Mockito.times(1))
                    .findRatingById(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("WHEN an exception occurs while retrieving rating on a GET /rating/update/{id} request " +
                "THEN return status is found (302) " +
                "AND the expected view is the rating list page")
        void showUpdateFormTest_WithException() throws Exception {
            //GIVEN
            when(ratingServiceMock.findRatingById(TestConstants.UNKNOWN_RATING_ID))
                    .thenThrow(new IllegalArgumentException());

            //WHEN-THEN
            mockMvc.perform(get("/rating/update/{id}", TestConstants.UNKNOWN_RATING_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/rating/list"));

            verify(ratingServiceMock, Mockito.times(1))
                    .findRatingById(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /rating/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void showUpdateFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/rating/update/{id}", TestConstants.EXISTING_RATING_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ratingServiceMock, Mockito.times(0))
                    .findRatingById(anyInt());
        }
    }

    @Nested
    @DisplayName("updateRating tests")
    class UpdateRatingTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a rating to update " +
                "WHEN processing a POST /rating/update/{id} request for this rating " +
                "THEN return status is found (302) " +
                "AND the expected view is the rating list page with rating list updated")
        void updateRatingTest_WithSuccess() throws Exception {
            //GIVEN
            when(ratingServiceMock.updateRating(any(RatingDTO.class)))
                    .thenReturn(ratingDTO);

            //WHEN-THEN
            mockMvc.perform(post("/rating/update/{id}", anyInt())
                    .param("moodysRating", ratingDTO.getMoodysRating())
                    .param("sandPRating", ratingDTO.getSandPRating())
                    .param("fitchRating", ratingDTO.getFitchRating())
                    .param("orderNumber", ratingDTO.getOrderNumber().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/rating/list"));

            verify(ratingServiceMock, Mockito.times(1))
                    .updateRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a rating to update with missing account " +
                "WHEN processing a POST /rating/update/{id} request for this rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/update page filled with entered rating")
        void updateRatingTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/rating/update/{id}", TestConstants.EXISTING_RATING_ID)
                    .param("moodysRating", "")
                    .param("sandPRating", TestConstants.EXISTING_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.EXISTING_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("rating"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("rating", "moodysRating", "NotBlank"))
                    .andExpect(view().name("rating/update"));

            verify(ratingServiceMock, Mockito.times(0))
                    .updateRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a rating to update with invalid sandPRating (too long) " +
                "WHEN processing a POST /rating/update/{id} request for this rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/update/{id} page filled with entered rating")
        void updateBidTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/rating/update/{id}", TestConstants.EXISTING_RATING_ID)
                    .param("moodysRating", TestConstants.EXISTING_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.NEW_RATING_SANDP_RATING_WITH_TOO_LONG_SIZE)
                    .param("fitchRating", TestConstants.EXISTING_RATING_FITCH)
                    .param("orderNumber", TestConstants.EXISTING_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("rating"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("rating", "sandPRating", "Size"))
                    .andExpect(view().name("rating/update"));

            verify(ratingServiceMock, Mockito.times(0))
                    .updateRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when updating the rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/update/{id} page filled with entered rating")
        void updateRatingTest_WithException() throws Exception {
            //GIVEN
            when(ratingServiceMock.updateRating(any(RatingDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/rating/update/{id}", TestConstants.EXISTING_RATING_ID)
                    .param("moodysRating", TestConstants.EXISTING_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.EXISTING_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.EXISTING_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("rating/update"));

            verify(ratingServiceMock, Mockito.times(1))
                    .updateRating(any(RatingDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when updating the new rating " +
                "THEN the returned code is ok " +
                "AND the expected view is the rating/update page filled with entered rating")
        void updateRatingTest_WithNoReturnedRatingAfterSaving() throws Exception {
            //GIVEN
            when(ratingServiceMock.updateRating(any(RatingDTO.class)))
                    .thenReturn(null);

            //WHEN-THEN
            mockMvc.perform(post("/rating/update/{id}", TestConstants.EXISTING_RATING_ID)
                    .param("moodysRating", TestConstants.EXISTING_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.EXISTING_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.EXISTING_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("rating/update"));

            verify(ratingServiceMock, Mockito.times(1))
                    .updateRating(any(RatingDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /rating/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void updateRatingTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/rating/update/{id}", TestConstants.EXISTING_RATING_ID)
                    .param("moodysRating", TestConstants.EXISTING_RATING_MOODYS_RATING)
                    .param("sandPRating", TestConstants.EXISTING_RATING_SANDP_RATING)
                    .param("fitchRating", TestConstants.NEW_RATING_FITCH_RATING)
                    .param("orderNumber", TestConstants.EXISTING_RATING_ORDER_NUMBER.toString())
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ratingServiceMock, Mockito.times(0))
                    .updateRating(any(RatingDTO.class));
        }
    }

    @Nested
    @DisplayName("deleteRating tests")
    class DeleteRatingTest {
        @WithMockUser
        @Test
        @DisplayName("GIVEN a rating to delete " +
                "WHEN processing a GET /rating/delete/{id} request for this rating " +
                "THEN return status is found (302) " +
                "AND the expected view is the rating list page with rating list updated")
        void deleteRatingTest_WithSuccess() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/rating/delete/{id}", TestConstants.EXISTING_RATING_ID)
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/rating/list"));

            verify(ratingServiceMock, Mockito.times(1))
                    .deleteRating(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a unknown rating to delete " +
                "WHEN processing a GET /rating/delete/{id} request for this rating " +
                "THEN the returned code is found " +
                "AND the expected view is the rating/list page")
        void deleteRatingTest_WithMissingInformation() throws Exception {
            //GIVEN
            doThrow(new IllegalArgumentException()).when(ratingServiceMock).deleteRating(anyInt());

            //WHEN-THEN
            mockMvc.perform(get("/rating/delete/{id}", TestConstants.UNKNOWN_RATING_ID)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/rating/list"));

            verify(ratingServiceMock, Mockito.times(1))
                    .deleteRating(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /rating/delete/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void deleteRatingTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/rating/delete/{id}", TestConstants.EXISTING_RATING_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ratingServiceMock, Mockito.times(0))
                    .findRatingById(anyInt());
        }
    }
}
