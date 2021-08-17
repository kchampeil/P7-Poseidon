package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.TradeDTO;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.services.contracts.ITradeService;
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

@WebMvcTest(controllers = TradeController.class)
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITradeService tradeServiceMock;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceMock;

    @MockBean
    private PasswordEncoder passwordEncoderMock;

    private static TradeDTO tradeDTO;

    @BeforeAll
    static void setUp() {
        tradeDTO = new TradeDTO();
        tradeDTO.setTradeId(TestConstants.NEW_TRADE_ID);
        tradeDTO.setAccount(TestConstants.NEW_TRADE_ACCOUNT);
        tradeDTO.setType(TestConstants.NEW_TRADE_TYPE);
        tradeDTO.setBuyQuantity(TestConstants.NEW_TRADE_BUY_QUANTITY);
    }

    @Nested
    @DisplayName("home tests")
    class HomeTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN asking for the trade list page while logged in " +
                " THEN return status is ok and the expected view is the trade list page")
        void homeTest_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/trade/list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("tradeAll"))
                    .andExpect(view().name("trade/list"));

            verify(tradeServiceMock, Mockito.times(1))
                    .findAll();
        }


        @Test
        @DisplayName("WHEN asking for the trade list page while not logged in " +
                " THEN return status is Found (302) and the expected view is the login page")
        void homeTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/trade/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userDetailsServiceMock, Mockito.times(0))
                    .loadUserByUsername(anyString());
            verify(tradeServiceMock, Mockito.times(0))
                    .findAll();
        }
    }

    @Nested
    @DisplayName("addTradeForm tests")
    class AddTradeFormTest {
        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /trade/add request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the trade add form initialized")
        void addTradeFormTest_WithSuccess_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/trade/add"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("trade"))
                    .andExpect(view().name("trade/add"));
        }

        @Test
        @DisplayName("WHEN processing a GET /trade/add request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is login page")
        void addTradeFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/trade/add"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }


    @Nested
    @DisplayName("validate tests")
    class ValidateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a new trade to add " +
                "WHEN processing a POST /trade/validate request for this trade " +
                "THEN return status is found (302) " +
                "AND the expected view is the trade list page with trade list updated")
        void validateTest_WithSuccess() throws Exception {
            //GIVEN
            when(tradeServiceMock.create(any(TradeDTO.class)))
                    .thenReturn(Optional.of(tradeDTO));

            //WHEN-THEN
            mockMvc.perform(post("/trade/validate")
                    .param("account", tradeDTO.getAccount())
                    .param("type", tradeDTO.getType())
                    .param("buyQuantity", tradeDTO.getBuyQuantity().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeServiceMock, Mockito.times(1))
                    .create(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new trade to add with missing account" +
                "WHEN processing a POST /trade/validate request for this trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/add page filled with entered trade")
        void validateTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/trade/validate")
                    .param("account", "")
                    .param("type", TestConstants.NEW_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.NEW_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("trade"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("trade", "account", "NotBlank"))
                    .andExpect(view().name("trade/add"));

            verify(tradeServiceMock, Mockito.times(0))
                    .create(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new trade to add with invalid type (too long) " +
                "WHEN processing a POST /trade/validate request for this trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/add page filled with entered trade")
        void validateTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/trade/validate")
                    .param("account", TestConstants.NEW_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE_WITH_TOO_LONG_SIZE)
                    .param("buyQuantity", TestConstants.NEW_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("trade"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("trade", "type", "Size"))
                    .andExpect(view().name("trade/add"));

            verify(tradeServiceMock, Mockito.times(0))
                    .create(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when saving the new trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/add page filled with entered trade")
        void validateTest_WithException() throws Exception {
            //GIVEN
            when(tradeServiceMock.create(any(TradeDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/trade/validate")
                    .param("account", TestConstants.NEW_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.NEW_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("trade/add"));

            verify(tradeServiceMock, Mockito.times(1))
                    .create(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when saving the new trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/add page filled with entered trade")
        void validateTest_WithNoReturnedTradeAfterSaving() throws Exception {
            //GIVEN
            when(tradeServiceMock.create(any(TradeDTO.class)))
                    .thenReturn(Optional.empty());

            //WHEN-THEN
            mockMvc.perform(post("/trade/validate")
                    .param("account", TestConstants.NEW_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.NEW_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("trade/add"));

            verify(tradeServiceMock, Mockito.times(1))
                    .create(any(TradeDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /trade/validate request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void validateTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/trade/validate")
                    .param("account", TestConstants.NEW_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.NEW_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(tradeServiceMock, Mockito.times(0))
                    .create(any(TradeDTO.class));
        }
    }


    @Nested
    @DisplayName("showUpdateForm tests")
    class ShowUpdateFormTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /trade/update/{id} request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the trade update form initialized")
        void showUpdateFormTest_WithSuccess_LoggedIn() throws Exception {
            //GIVEN
            when(tradeServiceMock.findById(anyInt()))
                    .thenReturn(tradeDTO);

            //WHEN-THEN
            mockMvc.perform(get("/trade/update/{id}", anyInt()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("trade"))
                    .andExpect(view().name("trade/update"));

            verify(tradeServiceMock, Mockito.times(1))
                    .findById(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("WHEN an exception occurs while retrieving trade on a GET /trade/update/{id} request " +
                "THEN return status is found (302) " +
                "AND the expected view is the trade list page")
        void showUpdateFormTest_WithException() throws Exception {
            //GIVEN
            when(tradeServiceMock.findById(TestConstants.UNKNOWN_TRADE_ID))
                    .thenThrow(new IllegalArgumentException());

            //WHEN-THEN
            mockMvc.perform(get("/trade/update/{id}", TestConstants.UNKNOWN_TRADE_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeServiceMock, Mockito.times(1))
                    .findById(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /trade/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void showUpdateFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/trade/update/{id}", TestConstants.EXISTING_TRADE_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(tradeServiceMock, Mockito.times(0))
                    .findById(anyInt());
        }
    }

    @Nested
    @DisplayName("updateTrade tests")
    class UpdateTradeTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a trade to update " +
                "WHEN processing a POST /trade/update/{id} request for this trade " +
                "THEN return status is found (302) " +
                "AND the expected view is the trade list page with trade list updated")
        void updateTradeTest_WithSuccess() throws Exception {
            //GIVEN
            when(tradeServiceMock.update(any(TradeDTO.class)))
                    .thenReturn(tradeDTO);

            //WHEN-THEN
            mockMvc.perform(post("/trade/update/{id}", anyInt())
                    .param("account", tradeDTO.getAccount())
                    .param("type", tradeDTO.getType())
                    .param("buyQuantity", tradeDTO.getBuyQuantity().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeServiceMock, Mockito.times(1))
                    .update(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a trade to update with missing account " +
                "WHEN processing a POST /trade/update/{id} request for this trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/update page filled with entered trade")
        void updateTradeTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/trade/update/{id}", TestConstants.EXISTING_TRADE_ID)
                    .param("account", "")
                    .param("type", TestConstants.EXISTING_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.EXISTING_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("trade"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("trade", "account", "NotBlank"))
                    .andExpect(view().name("trade/update"));

            verify(tradeServiceMock, Mockito.times(0))
                    .update(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a trade to update with invalid type (too long) " +
                "WHEN processing a POST /trade/update/{id} request for this trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/update/{id} page filled with entered trade")
        void updateBidTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/trade/update/{id}", TestConstants.EXISTING_TRADE_ID)
                    .param("account", TestConstants.NEW_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE_WITH_TOO_LONG_SIZE)
                    .param("buyQuantity", TestConstants.NEW_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("trade"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("trade", "type", "Size"))
                    .andExpect(view().name("trade/update"));

            verify(tradeServiceMock, Mockito.times(0))
                    .update(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when updating the trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/update/{id} page filled with entered trade")
        void updateTradeTest_WithException() throws Exception {
            //GIVEN
            when(tradeServiceMock.update(any(TradeDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/trade/update/{id}", TestConstants.EXISTING_TRADE_ID)
                    .param("account", TestConstants.EXISTING_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.EXISTING_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("trade/update"));

            verify(tradeServiceMock, Mockito.times(1))
                    .update(any(TradeDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when updating the new trade " +
                "THEN the returned code is ok " +
                "AND the expected view is the trade/update page filled with entered trade")
        void updateTradeTest_WithNoReturnedTradeAfterSaving() throws Exception {
            //GIVEN
            when(tradeServiceMock.update(any(TradeDTO.class)))
                    .thenReturn(null);

            //WHEN-THEN
            mockMvc.perform(post("/trade/update/{id}", TestConstants.EXISTING_TRADE_ID)
                    .param("account", TestConstants.EXISTING_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.EXISTING_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("trade/update"));

            verify(tradeServiceMock, Mockito.times(1))
                    .update(any(TradeDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /trade/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void updateTradeTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/trade/update/{id}", TestConstants.EXISTING_TRADE_ID)
                    .param("account", TestConstants.EXISTING_TRADE_ACCOUNT)
                    .param("type", TestConstants.NEW_TRADE_TYPE)
                    .param("buyQuantity", TestConstants.EXISTING_TRADE_BUY_QUANTITY.toString())
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(tradeServiceMock, Mockito.times(0))
                    .update(any(TradeDTO.class));
        }
    }

    @Nested
    @DisplayName("deleteTrade tests")
    class DeleteTradeTest {
        @WithMockUser
        @Test
        @DisplayName("GIVEN a trade to delete " +
                "WHEN processing a GET /trade/delete/{id} request for this trade " +
                "THEN return status is found (302) " +
                "AND the expected view is the trade list page with trade list updated")
        void deleteTradeTest_WithSuccess() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/trade/delete/{id}", TestConstants.EXISTING_TRADE_ID)
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeServiceMock, Mockito.times(1))
                    .delete(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a unknown trade to delete " +
                "WHEN processing a GET /trade/delete/{id} request for this trade " +
                "THEN the returned code is found " +
                "AND the expected view is the trade/list page")
        void deleteTradeTest_WithMissingInformation() throws Exception {
            //GIVEN
            doThrow(new IllegalArgumentException()).when(tradeServiceMock).delete(anyInt());

            //WHEN-THEN
            mockMvc.perform(get("/trade/delete/{id}", TestConstants.UNKNOWN_TRADE_ID)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/trade/list"));

            verify(tradeServiceMock, Mockito.times(1))
                    .delete(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /trade/delete/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void deleteTradeTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/trade/delete/{id}", TestConstants.EXISTING_TRADE_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(tradeServiceMock, Mockito.times(0))
                    .findById(anyInt());
        }
    }
}
