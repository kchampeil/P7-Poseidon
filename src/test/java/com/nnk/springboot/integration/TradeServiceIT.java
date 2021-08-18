package com.nnk.springboot.integration;

import com.nnk.springboot.DTO.TradeDTO;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.contracts.ITradeService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
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
public class TradeServiceIT {
    @Autowired
    ITradeService tradeService;

    @Autowired
    TradeRepository tradeRepository;

    private Trade tradeInDb;
    private TradeDTO tradeDTO;

    @BeforeEach
    private void initPerTest() {
        //init a trade in DB for test
        tradeInDb = new Trade();
        tradeInDb.setAccount(TestConstants.EXISTING_TRADE_ACCOUNT);
        tradeInDb.setType(TestConstants.EXISTING_TRADE_TYPE);
        tradeInDb.setBuyQuantity(TestConstants.EXISTING_TRADE_BUY_QUANTITY);
        tradeInDb = tradeRepository.save(tradeInDb);

        //init common part of tradeDTO to create/update
        tradeDTO = new TradeDTO();
        tradeDTO.setAccount(TestConstants.EXISTING_TRADE_ACCOUNT);
        tradeDTO.setType(TestConstants.EXISTING_TRADE_TYPE);
        tradeDTO.setBuyQuantity(TestConstants.EXISTING_TRADE_BUY_QUANTITY);
    }

    @AfterEach
    private void cleanPerTest(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanUp")) {
            return;
        }
        //clean DB at the end of the test by deleting the trade created at initialization
        tradeRepository.deleteById(tradeInDb.getTradeId());
    }

    @WithMockUser
    @Test
    @DisplayName("WHEN creating a new trade with correct informations  " +
            "THEN the returned value is the added trade, " +
            "AND the trade is added in DB")
    public void createIT_WithSuccess() {

        //WHEN
        Optional<TradeDTO> tradeDTOCreated = tradeService.create(tradeDTO);

        //THEN
        assertTrue(tradeDTOCreated.isPresent());
        assertNotNull(tradeDTOCreated.get().getTradeId());
        assertEquals(tradeDTO.getAccount(), tradeDTOCreated.get().getAccount());

        //cleaning of DB at the end of the test by deleting the trade created during the test
        tradeRepository.deleteById(tradeDTOCreated.get().getTradeId());
    }


    @Test
    @DisplayName("WHEN asking for the list of all trade " +
            "THEN the returned value is the list of all trade in DB")
    public void findAllIT_WithSuccess() {

        //WHEN
        List<TradeDTO> tradeDTOList = tradeService.findAll();

        //THEN
        assertThat(tradeDTOList.size()).isGreaterThan(0);
        assertEquals(tradeInDb.getTradeId(), tradeDTOList.get(0).getTradeId());
    }


    @Test
    @DisplayName("WHEN asking for a trade with a specified id " +
            "THEN the returned value is the trade in DB")
    public void findByIdIT_WithSuccess() {

        //WHEN
        TradeDTO tradeDTO = tradeService.findById(tradeInDb.getTradeId());

        //THEN
        assertEquals(tradeInDb.getTradeId(), tradeDTO.getTradeId());
        assertEquals(tradeInDb.getAccount(), tradeDTO.getAccount());
        assertEquals(tradeInDb.getType(), tradeDTO.getType());
        assertEquals(tradeInDb.getBuyQuantity(), tradeDTO.getBuyQuantity());
    }


    @WithMockUser
    @Test
    @DisplayName("WHEN updating a trade with correct informations  " +
            "THEN the returned value is the updated trade, " +
            "AND the trade is updated in DB")
    public void updateIT_WithSuccess() {

        //GIVEN
        tradeDTO.setTradeId(tradeInDb.getTradeId());
        tradeDTO.setType(TestConstants.NEW_TRADE_TYPE);

        //WHEN
        TradeDTO tradeDTOUpdated = tradeService.update(tradeDTO);
        Optional<Trade> tradeUpdated = tradeRepository.findById(tradeInDb.getTradeId());

        //THEN
        assertNotNull(tradeDTOUpdated);
        assertEquals(tradeDTO.getType(), tradeDTOUpdated.getType());

        assertTrue(tradeUpdated.isPresent());
        assertEquals(tradeDTO.getType(), tradeUpdated.get().getType());
    }


    @Test
    @Tag("SkipCleanUp")
    @DisplayName("WHEN deleting a trade with correct informations  " +
            "THEN the trade is deleted in DB")
    public void deleteIT_WithSuccess() {

        //WHEN
        tradeService.delete(tradeInDb.getTradeId());
        Optional<Trade> tradeDeleted = tradeRepository.findById(tradeInDb.getTradeId());

        //THEN
        assertFalse(tradeDeleted.isPresent());
    }
}
