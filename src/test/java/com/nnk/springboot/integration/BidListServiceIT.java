package com.nnk.springboot.integration;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.contracts.IBidListService;
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
public class BidListServiceIT {
    @Autowired
    IBidListService bidListService;

    @Autowired
    BidListRepository bidListRepository;

    private BidList bidListInDb;
    private BidListDTO bidListDTO;

    @BeforeEach
    private void initPerTest() {
        //init a bidList in DB for test
        bidListInDb = new BidList();
        bidListInDb.setAccount(TestConstants.EXISTING_BID_LIST_ACCOUNT);
        bidListInDb.setType(TestConstants.EXISTING_BID_LIST_TYPE);
        bidListInDb.setBidQuantity(TestConstants.EXISTING_BID_LIST_BID_QUANTITY);
        bidListInDb = bidListRepository.save(bidListInDb);

        //init common part of bidListDTO to create/update
        bidListDTO = new BidListDTO();
        bidListDTO.setAccount(TestConstants.EXISTING_BID_LIST_ACCOUNT);
        bidListDTO.setType(TestConstants.EXISTING_BID_LIST_TYPE);
        bidListDTO.setBidQuantity(TestConstants.EXISTING_BID_LIST_BID_QUANTITY);
    }

    @AfterEach
    private void cleanPerTest(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanUp")) {
            return;
        }
        //clean DB at the end of the test by deleting the bidList created at initialization
        bidListRepository.deleteById(bidListInDb.getBidListId());
    }

    @WithMockUser
    @Test
    @DisplayName("WHEN creating a new bidList with correct informations  " +
            "THEN the returned value is the added bidList, " +
            "AND the bidList is added in DB")
    public void createBidListIT_WithSuccess() {

        //WHEN
        Optional<BidListDTO> bidListDTOCreated = bidListService.create(bidListDTO);

        //THEN
        assertTrue(bidListDTOCreated.isPresent());
        assertNotNull(bidListDTOCreated.get().getBidListId());
        assertEquals(bidListDTO.getBidQuantity(), bidListDTOCreated.get().getBidQuantity());

        //cleaning of DB at the end of the test by deleting the bidList created during the test
        bidListRepository.deleteById(bidListDTOCreated.get().getBidListId());
    }


    @Test
    @DisplayName("WHEN asking for the list of all bidList " +
            "THEN the returned value is the list of all bidList in DB")
    public void findAllBidListIT_WithSuccess() {

        //WHEN
        List<BidListDTO> bidListDTOList = bidListService.findAll();

        //THEN
        assertThat(bidListDTOList.size()).isGreaterThan(0);
        assertEquals(bidListInDb.getBidListId(), bidListDTOList.get(0).getBidListId());
    }


    @Test
    @DisplayName("WHEN asking for a bidList with a specified id " +
            "THEN the returned value is the bidList in DB")
    public void findBidListByIdIT_WithSuccess() {

        //WHEN
        BidListDTO bidListDTO = bidListService.findById(bidListInDb.getBidListId());

        //THEN
        assertEquals(bidListInDb.getBidListId(), bidListDTO.getBidListId());
        assertEquals(bidListInDb.getAccount(), bidListDTO.getAccount());
        assertEquals(bidListInDb.getType(), bidListDTO.getType());
        assertEquals(bidListInDb.getBidQuantity(), bidListDTO.getBidQuantity());
    }

    @WithMockUser
    @Test
    @DisplayName("WHEN updating a bidList with correct informations  " +
            "THEN the returned value is the updated bidList, " +
            "AND the bidList is updated in DB")
    public void updateBidListIT_WithSuccess() {

        //GIVEN
        bidListDTO.setBidListId(bidListInDb.getBidListId());
        bidListDTO.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);

        //WHEN
        BidListDTO bidListDTOUpdated = bidListService.update(bidListDTO);
        Optional<BidList> bidListUpdated = bidListRepository.findById(bidListInDb.getBidListId());

        //THEN
        assertNotNull(bidListDTOUpdated);
        assertEquals(bidListDTO.getBidQuantity(), bidListDTOUpdated.getBidQuantity());
        assertEquals(bidListDTO.getAccount(), bidListDTOUpdated.getAccount());

        assertTrue(bidListUpdated.isPresent());
        assertEquals(bidListDTO.getBidQuantity(), bidListUpdated.get().getBidQuantity());
    }


    @Test
    @Tag("SkipCleanUp")
    @DisplayName("WHEN deleting a bidList with correct informations  " +
            "THEN the bidList is deleted in DB")
    public void deleteBidListIT_WithSuccess() {

        //WHEN
        bidListService.delete(bidListInDb.getBidListId());
        Optional<BidList> bidListDeleted = bidListRepository.findById(bidListInDb.getBidListId());

        //THEN
        assertFalse(bidListDeleted.isPresent());
    }
}
