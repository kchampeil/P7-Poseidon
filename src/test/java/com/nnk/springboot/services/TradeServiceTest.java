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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class TradeServiceTest {

    @MockBean
    private TradeRepository tradeRepositoryMock;

    @Autowired
    private ITradeService tradeService;

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
    @DisplayName("create tests")
    class CreateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex trade " +
                "THEN an exception is thrown")
        void createTest_WithException() {
            //GIVEN
            when(tradeRepositoryMock.save(any(Trade.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> tradeService.create(tradeDTOWithValues));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .save(any(Trade.class));
        }
    }


    @Nested
    @DisplayName("findAll tests")
    class FindAllTest {

        @Test
        @DisplayName("GIVEN no trade in DB " +
                "WHEN getting all the trade " +
                "THEN the returned value is an empty list of trade")
        void findAllTest_WithNoDataInDB() {
            //GIVEN
            List<Trade> tradeList = new ArrayList<>();
            when(tradeRepositoryMock.findAll()).thenReturn(tradeList);

            //THEN
            List<TradeDTO> tradeDTOList = tradeService.findAll();
            assertThat(tradeDTOList).isEmpty();

            verify(tradeRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findById tests")
    class FindByIdTest {

        @Test
        @DisplayName("GIVEN no trade in DB for a specified id " +
                "WHEN getting all the trade " +
                "THEN the returned value is a null trade")
        void findByIdTest_WithNoDataInDB() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> tradeService.findById(TestConstants.EXISTING_TRADE_ID));
            assertEquals(PoseidonExceptionsConstants.TRADE_ID_NOT_VALID
                    + TestConstants.EXISTING_TRADE_ID, exception.getMessage());

            verify(tradeRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("update tests")
    class UpdateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a trade " +
                "THEN an exception is thrown")
        void updateTest_WithException() {
            //GIVEN
            when(tradeRepositoryMock.save(any(Trade.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> tradeService.update(tradeDTOWithValues));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .save(any(Trade.class));
        }
    }


    @Nested
    @DisplayName("delete tests")
    class DeleteTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a trade " +
                "THEN an exception is thrown")
        void deleteTest_WithException() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(tradeInDb));
            doThrow(new RuntimeException()).when(tradeRepositoryMock).delete(any(Trade.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> tradeService.delete(tradeInDb.getTradeId()));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(tradeRepositoryMock, Mockito.times(1))
                    .delete(any(Trade.class));
        }


        @Test
        @DisplayName("GIVEN no trade in DB for the specified id " +
                "WHEN deleting a trade " +
                "THEN an exception is thrown")
        void deleteTest_WithNoDataInDb() {
            //GIVEN
            when(tradeRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> tradeService.delete(tradeInDb.getTradeId()));

            verify(tradeRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(tradeRepositoryMock, Mockito.times(0))
                    .delete(any(Trade.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a trade " +
                "THEN an exception is thrown")
        void deleteTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> tradeService.delete(null));

            verify(tradeRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(tradeRepositoryMock, Mockito.times(0))
                    .delete(any(Trade.class));
        }
    }
}
