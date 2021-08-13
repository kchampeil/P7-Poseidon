package com.nnk.springboot.services;

import com.nnk.springboot.DTO.TradeDTO;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.contracts.ITradeService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

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
public class TradeServiceTest {

    @MockBean
    TradeRepository tradeRepositoryMock;

    @Autowired
    ITradeService tradeService;

    private static TradeDTO tradeDTOWithValues;

    private static Trade tradeInDb;

    @BeforeAll
    static void setUp() {
        tradeDTOWithValues = new TradeDTO();
        tradeDTOWithValues.setTradeId(TestConstants.EXISTING_TRADE_ID);
        tradeDTOWithValues.setAccount(TestConstants.NEW_TRADE_ACCOUNT);
        tradeDTOWithValues.setType(TestConstants.NEW_TRADE_TYPE);
        tradeDTOWithValues.setBuyQuantity(TestConstants.NEW_TRADE_BUY_QUANTITY);

        tradeInDb = new Trade();
        tradeInDb.setTradeId(tradeDTOWithValues.getTradeId());
        tradeInDb.setAccount(tradeDTOWithValues.getAccount());
        tradeInDb.setType(tradeDTOWithValues.getType());
        tradeInDb.setBuyQuantity(tradeDTOWithValues.getBuyQuantity());
    }

    @Nested
    @DisplayName("CreateTrade tests")
    class CreateTradeTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a new trade (DTO) to add " +
                "WHEN saving this new trade " +
                "THEN the returned value is the added trade (DTO)")
        void createTradeTest_WithSuccess() {
            //GIVEN
            when(tradeRepositoryMock.save(any(Trade.class))).thenReturn(tradeInDb);

            //WHEN
            Optional<TradeDTO> createdTradeDTO = tradeService.createTrade(tradeDTOWithValues);

            //THEN
            assertTrue(createdTradeDTO.isPresent());
            assertNotNull(createdTradeDTO.get().getTradeId());
            assertEquals(tradeDTOWithValues.toString(), createdTradeDTO.get().toString());

            verify(tradeRepositoryMock, Mockito.times(1))
                    .save(any(Trade.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex trade " +
                "THEN an exception is thrown")
        void createTradeTest_WithException() {
            //GIVEN
            when(tradeRepositoryMock.save(any(Trade.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> tradeService.createTrade(tradeDTOWithValues));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .save(any(Trade.class));
        }
    }


    @Nested
    @DisplayName("findAllTrade tests")
    class FindAllTradeTest {

        @Test
        @DisplayName("GIVEN trade in DB " +
                "WHEN getting all the trade " +
                "THEN the returned value is the list of trade")
        void findAllTradeTest_WithDataInDB() {
            //GIVEN
            List<Trade> tradeList = new ArrayList<>();
            tradeList.add(tradeInDb);
            when(tradeRepositoryMock.findAll()).thenReturn(tradeList);

            //THEN
            List<TradeDTO> tradeDTOList = tradeService.findAllTrade();
            assertEquals(1, tradeDTOList.size());
            assertEquals(tradeInDb.getTradeId(), tradeDTOList.get(0).getTradeId());

            verify(tradeRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN no trade in DB " +
                "WHEN getting all the trade " +
                "THEN the returned value is an empty list of trade")
        void findAllTradeTest_WithNoDataInDB() {
            //GIVEN
            List<Trade> tradeList = new ArrayList<>();
            when(tradeRepositoryMock.findAll()).thenReturn(tradeList);

            //THEN
            List<TradeDTO> tradeDTOList = tradeService.findAllTrade();
            assertThat(tradeDTOList).isEmpty();

            verify(tradeRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findTradeById tests")
    class FindTradeByIdTest {

        @Test
        @DisplayName("GIVEN trade in DB for a specified id" +
                "WHEN getting the trade on id " +
                "THEN the returned value is the trade")
        void findTradeByIdTest_WithDataInDB() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.of(tradeInDb));

            //THEN
            TradeDTO tradeDTO = tradeService.findTradeById(TestConstants.EXISTING_TRADE_ID);
            assertEquals(tradeInDb.getTradeId(), tradeDTO.getTradeId());
            assertEquals(tradeInDb.getAccount(), tradeDTO.getAccount());

            verify(tradeRepositoryMock, Mockito.times(1)).findById(anyInt());
        }

        @Test
        @DisplayName("GIVEN no trade in DB for a specified id " +
                "WHEN getting all the trade " +
                "THEN the returned value is a null trade")
        void findTradeByIdTest_WithNoDataInDB() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> tradeService.findTradeById(TestConstants.EXISTING_TRADE_ID));
            assertEquals(PoseidonExceptionsConstants.TRADE_ID_NOT_VALID
                    + TestConstants.EXISTING_TRADE_ID, exception.getMessage());

            verify(tradeRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("updateTrade tests")
    class UpdateTradeTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a trade to update " +
                "WHEN updating this trade " +
                "THEN the returned value is the updated trade")
        void updateTradeTest_WithSuccess() {
            //GIVEN
            when(tradeRepositoryMock.save(any(Trade.class))).thenReturn(tradeInDb);

            //WHEN
            TradeDTO createdTradeDTO = tradeService.updateTrade(tradeDTOWithValues);

            //THEN
            assertEquals(tradeDTOWithValues.toString(), createdTradeDTO.toString());

            verify(tradeRepositoryMock, Mockito.times(1))
                    .save(any(Trade.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a trade " +
                "THEN an exception is thrown")
        void updateTradeTest_WithException() {
            //GIVEN
            when(tradeRepositoryMock.save(any(Trade.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> tradeService.updateTrade(tradeDTOWithValues));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .save(any(Trade.class));
        }
    }


    @Nested
    @DisplayName("deleteTrade tests")
    class DeleteTradeTest {

        @Test
        @DisplayName("GIVEN a trade to delete " +
                "WHEN deleting this trade " +
                "THEN nothing is returned")
        void deleteTradeTest_WithSuccess() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(tradeInDb));

            //WHEN
            tradeService.deleteTrade(tradeInDb.getTradeId());

            //THEN
            verify(tradeRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(tradeRepositoryMock, Mockito.times(1))
                    .delete(any(Trade.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a trade " +
                "THEN an exception is thrown")
        void deleteTradeTest_WithException() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(tradeInDb));
            doThrow(new RuntimeException()).when(tradeRepositoryMock).delete(any(Trade.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> tradeService.deleteTrade(tradeInDb.getTradeId()));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(tradeRepositoryMock, Mockito.times(1))
                    .delete(any(Trade.class));
        }


        @Test
        @DisplayName("GIVEN no trade in DB for the specified id " +
                "WHEN deleting a trade " +
                "THEN an exception is thrown")
        void deleteTradeTest_WithNoDataInDb() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> tradeService.deleteTrade(tradeInDb.getTradeId()));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(tradeRepositoryMock, Mockito.times(0))
                    .delete(any(Trade.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a trade " +
                "THEN an exception is thrown")
        void deleteTradeTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> tradeService.deleteTrade(null));

            verify(tradeRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(tradeRepositoryMock, Mockito.times(0))
                    .delete(any(Trade.class));
        }
    }
}
