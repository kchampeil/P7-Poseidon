package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.services.contracts.IBidListService;
import com.nnk.springboot.testconstants.TestConstants;
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

    @WithMockUser
    @Test
    @DisplayName("WHEN processing a GET /bidList/add request " +
            "THEN return status is ok " +
            "AND the expected view is the bidList add form initialized")
    void addBidFormTest_WithSuccess() throws Exception {
        mockMvc.perform(get("/bidList/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("bidList"))
                .andExpect(view().name("bidList/add"));
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


    @Test
    void showUpdateForm() {
    }

    @Test
    void updateBid() {
    }

    @Test
    void deleteBid() {
    }
}