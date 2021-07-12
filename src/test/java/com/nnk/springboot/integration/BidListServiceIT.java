package com.nnk.springboot.integration;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.contracts.IBidListService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
// TOASK @Sql(value = "/sql/cleaningDbBeforeIT.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class BidListServiceIT {
    @Autowired
    IBidListService bidListService;

    @Autowired
    BidListRepository bidListRepository;

    @Test
    @DisplayName("WHEN creating a new bidList with correct informations  " +
            "THEN the returned value is the added bidList, " +
            "AND the bidList is added in DB")
    public void createBidListIT_WithSuccess() {

        //GIVEN
        BidListDTO bidListDTOToCreate = new BidListDTO();
        bidListDTOToCreate.setAccount(TestConstants.NEW_BID_LIST_ACCOUNT);
        bidListDTOToCreate.setType(TestConstants.NEW_BID_LIST_TYPE);
        bidListDTOToCreate.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);

        //WHEN
        Optional<BidListDTO> bidListDTOCreated = bidListService.createBidList(bidListDTOToCreate);

        //THEN
        assertThat(bidListDTOCreated).isPresent();
        assertNotNull(bidListDTOCreated.get().getBidListId());
        assertEquals(bidListDTOToCreate.getBidQuantity(), bidListDTOCreated.get().getBidQuantity());

        //cleaning of DB at the end of the test by deleting the bidList created
        bidListRepository.deleteById(bidListDTOCreated.get().getBidListId());
    }

    @Test
    @DisplayName("WHEN asking for the list of all bidList " +
            "THEN the returned value is the list of all bidList in DB")
    public void findAllBidListIT_WithSuccess() {

        //GIVEN (init a bidList in DB for test)
        BidList bidListInDb = new BidList();
        bidListInDb.setAccount(TestConstants.NEW_BID_LIST_ACCOUNT);
        bidListInDb.setType(TestConstants.NEW_BID_LIST_TYPE);
        bidListInDb.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);
        bidListInDb = bidListRepository.save(bidListInDb);

        //WHEN
        List<BidListDTO> bidListDTOList = bidListService.findAllBidList();

        //THEN
        assertThat(bidListDTOList.size()).isGreaterThan(0);
        assertEquals(bidListInDb.getBidListId(), bidListDTOList.get(0).getBidListId());

        //cleaning of DB at the end of the test by deleting the bidList created at initialization
        bidListRepository.deleteById(bidListInDb.getBidListId());
    }
}
