package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.services.contracts.IBidListService;
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

@WebMvcTest(controllers = BidListController.class)
class BidListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBidListService bidListServiceMock;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceMock;

    @MockBean
    private PasswordEncoder passwordEncoderMock;

    private static User userInDb;

    @BeforeAll
    static void setUp() {
        userInDb = new User();
        userInDb.setId(TestConstants.EXISTING_USER_ID);
        userInDb.setUsername(TestConstants.EXISTING_USER_USERNAME);
        userInDb.setPassword(TestConstants.EXISTING_USER_PASSWORD);
        userInDb.setFullname(TestConstants.EXISTING_USER_FULLNAME);
        userInDb.setRole(TestConstants.EXISTING_USER_ROLE_USER);
    }

    @Nested
    @DisplayName("home tests")
    class HomeTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN asking for the bidList list page while logged in " +
                " THEN return status is ok and the expected view is the bidList list page")
        void homeTest_LoggedIn() throws Exception {
            //THEN
            mockMvc.perform(get("/bidList/list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("bidListAll"))
                    .andExpect(view().name("bidList/list"));

            verify(bidListServiceMock, Mockito.times(1))
                    .findAllBidList();
        }


        @Test
        @DisplayName("WHEN asking for the bidList list page while not logged in " +
                " THEN return status is Found (302) and the expected view is the login page")
        void homeTest_NotLoggedIn() throws Exception {
            mockMvc.perform(get("/bidList/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userDetailsServiceMock, Mockito.times(0))
                    .loadUserByUsername(anyString());
            verify(bidListServiceMock, Mockito.times(0))
                    .findAllBidList();
        }
    }

    @Nested
    @DisplayName("addBidForm tests")
    class AddBidFormTest {
        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /bidList/add request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the bidList add form initialized")
        void addBidFormTest_WithSuccess_LoggedIn() throws Exception {
            mockMvc.perform(get("/bidList/add"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("bidList"))
                    .andExpect(view().name("bidList/add"));
        }

        @Test
        @DisplayName("WHEN processing a GET /bidList/add request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is login page")
        void addBidFormTest_NotLoggedIn() throws Exception {
            mockMvc.perform(get("/bidList/add"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }


    @Nested
    @DisplayName("validate tests")
    class ValidateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a new bidList to add " +
                "WHEN processing a POST /bidList/validate request for this bidList " +
                "THEN return status is found (302) " +
                "AND the expected view is the bidList list page with bidList list updated")
        void validateTest_WithSuccess() throws Exception {
            //GIVEN
            BidListDTO bidListDTOAdded = new BidListDTO();
            bidListDTOAdded.setBidListId(TestConstants.NEW_BID_LIST_ID);
            bidListDTOAdded.setAccount(TestConstants.NEW_BID_LIST_ACCOUNT);
            bidListDTOAdded.setType(TestConstants.NEW_BID_LIST_TYPE);
            bidListDTOAdded.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);

            when(bidListServiceMock.createBidList(any(BidListDTO.class)))
                    .thenReturn(Optional.of(bidListDTOAdded));

            //THEN
            mockMvc.perform(post("/bidList/validate")
                    .param("account", bidListDTOAdded.getAccount())
                    .param("type", bidListDTOAdded.getType())
                    .param("bidQuantity", bidListDTOAdded.getBidQuantity().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/bidList/list"));

            verify(bidListServiceMock, Mockito.times(1))
                    .createBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new bidList to add with missing account" +
                "WHEN processing a POST /bidList/validate request for this bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/add page filled with entered bidList")
        void validateTest_WithMissingInformation() throws Exception {
            //THEN
            mockMvc.perform(post("/bidList/validate")
                    .param("account", "")
                    .param("type", TestConstants.NEW_BID_LIST_TYPE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("bidList"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("bidList", "account", "NotBlank"))
                    .andExpect(view().name("bidList/add"));

            verify(bidListServiceMock, Mockito.times(0))
                    .createBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new bidList to add with invalid type (too long) " +
                "WHEN processing a POST /bidList/validate request for this bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/add page filled with entered bidList")
        void validateTest_WithInvalidInformation() throws Exception {
            //THEN
            mockMvc.perform(post("/bidList/validate")
                    .param("account", TestConstants.NEW_BID_LIST_ACCOUNT)
                    .param("type", TestConstants.NEW_BID_LIST_TYPE_WITH_TOO_LONG_SIZE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("bidList"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("bidList", "type", "Size"))
                    .andExpect(view().name("bidList/add"));

            verify(bidListServiceMock, Mockito.times(0))
                    .createBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an SQL exception when saving the new bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/add page filled with entered bidList")
        void validateTest_WithExceptionWhenSaving() throws Exception {
            //GIVEN
            when(bidListServiceMock.createBidList(any(BidListDTO.class))).thenThrow(new RuntimeException());

            //THEN
            mockMvc.perform(post("/bidList/validate")
                    .param("account", TestConstants.NEW_BID_LIST_ACCOUNT)
                    .param("type", TestConstants.NEW_BID_LIST_TYPE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("bidList/add"));

            verify(bidListServiceMock, Mockito.times(1))
                    .createBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when saving the new bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/add page filled with entered bidList")
        void validateTest_WithNoReturnedBidListAfterSaving() throws Exception {
            //GIVEN
            when(bidListServiceMock.createBidList(any(BidListDTO.class)))
                    .thenReturn(Optional.empty());

            //THEN
            mockMvc.perform(post("/bidList/validate")
                    .param("account", TestConstants.NEW_BID_LIST_ACCOUNT)
                    .param("type", TestConstants.NEW_BID_LIST_TYPE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("bidList/add"));

            verify(bidListServiceMock, Mockito.times(1))
                    .createBidList(any(BidListDTO.class));
        }
    }


    @Nested
    @DisplayName("showUpdateForm tests")
    class ShowUpdateFormTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /bidList/update/{id} request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the bidList update form initialized")
        void showUpdateFormTest_WithSuccess_LoggedIn() throws Exception {
            //GIVEN
            BidListDTO bidListDTOToUpdate = new BidListDTO();
            bidListDTOToUpdate.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
            bidListDTOToUpdate.setAccount(TestConstants.EXISTING_BID_LIST_ACCOUNT);
            bidListDTOToUpdate.setType(TestConstants.EXISTING_BID_LIST_TYPE);
            bidListDTOToUpdate.setBidQuantity(TestConstants.EXISTING_BID_LIST_BID_QUANTITY);
            when(bidListServiceMock.findBidListById(TestConstants.EXISTING_BID_LIST_ID))
                    .thenReturn(bidListDTOToUpdate);

            //WHEN-THEN
            mockMvc.perform(get("/bidList/update/{id}", TestConstants.EXISTING_BID_LIST_ID))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("bidList"))
                    .andExpect(view().name("bidList/update"));

            verify(bidListServiceMock, Mockito.times(1))
                    .findBidListById(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("WHEN an exception occurs while retrieving bidList on a GET /bidList/update/{id} request " +
                "THEN return status is ok " +
                "AND the expected view is the bidList list page")
        void showUpdateFormTest_WithException() throws Exception {
            //TODO à revoir si ajout message pour l'assertion correspondante
            //GIVEN
            when(bidListServiceMock.findBidListById(TestConstants.UNKNOWN_BID_LIST_ID))
                    .thenThrow(new IllegalArgumentException());

            //WHEN-THEN
            mockMvc.perform(get("/bidList/update/{id}", TestConstants.UNKNOWN_BID_LIST_ID))
                    .andExpect(status().isOk())
                    .andExpect(view().name("bidList/list"));

            verify(bidListServiceMock, Mockito.times(1))
                    .findBidListById(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /bidList/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void showUpdateFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/bidList/update/{id}", TestConstants.EXISTING_BID_LIST_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(bidListServiceMock, Mockito.times(0))
                    .findBidListById(anyInt());
        }
    }

    @Nested
    @DisplayName("updateBid tests")
    class UpdateBidTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a bidList to update " +
                "WHEN processing a POST /bidList/update/{id} request for this bidList " +
                "THEN return status is found (302) " +
                "AND the expected view is the bidList list page with bidList list updated")
        void updateBidTest_WithSuccess() throws Exception {
            //GIVEN
            BidListDTO bidListDTOUpdated = new BidListDTO();
            bidListDTOUpdated.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
            bidListDTOUpdated.setAccount(TestConstants.EXISTING_BID_LIST_ACCOUNT);
            bidListDTOUpdated.setType(TestConstants.EXISTING_BID_LIST_TYPE);
            bidListDTOUpdated.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);

            when(bidListServiceMock.updateBidList(any(BidListDTO.class)))
                    .thenReturn(bidListDTOUpdated);

            //THEN
            mockMvc.perform(post("/bidList/update/{id}", TestConstants.EXISTING_BID_LIST_ID)
                    .param("account", bidListDTOUpdated.getAccount())
                    .param("type", bidListDTOUpdated.getType())
                    .param("bidQuantity", bidListDTOUpdated.getBidQuantity().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/bidList/list"));

            verify(bidListServiceMock, Mockito.times(1))
                    .updateBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a bidList to update with missing account " +
                "WHEN processing a POST /bidList/update/{id} request for this bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/add page filled with entered bidList")
        void updateBidTest_WithMissingInformation() throws Exception {
            //THEN
            mockMvc.perform(post("/bidList/update/{id}", TestConstants.EXISTING_BID_LIST_ID)
                    .param("account", "")
                    .param("type", TestConstants.EXISTING_BID_LIST_TYPE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("bidList"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("bidList", "account", "NotBlank"))
                    .andExpect(view().name("bidList/update"));

            verify(bidListServiceMock, Mockito.times(0))
                    .updateBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a bidList to update with invalid type (too long) " +
                "WHEN processing a POST /bidList/update/{id} request for this bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/update/{id} page filled with entered bidList")
        void updateBidTest_WithInvalidInformation() throws Exception {
            //THEN
            mockMvc.perform(post("/bidList/update/{id}", TestConstants.EXISTING_BID_LIST_ID)
                    .param("account", TestConstants.NEW_BID_LIST_ACCOUNT)
                    .param("type", TestConstants.NEW_BID_LIST_TYPE_WITH_TOO_LONG_SIZE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("bidList"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("bidList", "type", "Size"))
                    .andExpect(view().name("bidList/update"));

            verify(bidListServiceMock, Mockito.times(0))
                    .updateBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an SQL exception when saving the new bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/update/{id} page filled with entered bidList")
        void updateBidTest_WithExceptionWhenSaving() throws Exception {
            //GIVEN
            when(bidListServiceMock.updateBidList(any(BidListDTO.class))).thenThrow(new RuntimeException());

            //THEN
            mockMvc.perform(post("/bidList/update/{id}", TestConstants.EXISTING_BID_LIST_ID)
                    .param("account", TestConstants.EXISTING_BID_LIST_ACCOUNT)
                    .param("type", TestConstants.EXISTING_BID_LIST_TYPE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("bidList/update"));

            verify(bidListServiceMock, Mockito.times(1))
                    .updateBidList(any(BidListDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when updating the new bidList " +
                "THEN the returned code is ok " +
                "AND the expected view is the bidList/update page filled with entered bidList")
        void updateBidTest_WithNoReturnedBidListAfterSaving() throws Exception {
            //GIVEN
            when(bidListServiceMock.updateBidList(any(BidListDTO.class)))
                    .thenReturn(null);

            //THEN
            mockMvc.perform(post("/bidList/update/{id}", TestConstants.EXISTING_BID_LIST_ID)
                    .param("account", TestConstants.EXISTING_BID_LIST_ACCOUNT)
                    .param("type", TestConstants.EXISTING_BID_LIST_TYPE)
                    .param("bidQuantity", TestConstants.NEW_BID_LIST_BID_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("bidList/update"));

            verify(bidListServiceMock, Mockito.times(1))
                    .updateBidList(any(BidListDTO.class));
        }
    }

    @Test
    void deleteBid() {
    }
}