package com.nnk.springboot.services;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.contracts.IBidListService;
import com.nnk.springboot.testconstants.TestConstants;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class BidListServiceTest {

    @MockBean
    BidListRepository bidListRepositoryMock;

    @Autowired
    IBidListService bidListService;

    @Nested
    @DisplayName("CreateBidList tests")
    class CreateBidListTest {

        @Test
        @DisplayName("GIVEN a new bidList (DTO) to add " +
                "WHEN saving this new bidList " +
                "THEN the returned value is the added bidList (DTO)")
        void createBidListTest_WithSuccess() {
            //GIVEN
            BidListDTO bidListDTOToCreate = new BidListDTO();
            bidListDTOToCreate.setAccount(TestConstants.NEW_BID_LIST_ACCOUNT);
            bidListDTOToCreate.setType(TestConstants.NEW_BID_LIST_TYPE);
            bidListDTOToCreate.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);

            BidList bidListInDb = new BidList();
            bidListInDb.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
            bidListInDb.setAccount(bidListDTOToCreate.getAccount());
            bidListInDb.setType(bidListDTOToCreate.getType());
            bidListInDb.setBidQuantity(bidListDTOToCreate.getBidQuantity());
            when(bidListRepositoryMock.save(any(BidList.class))).thenReturn(bidListInDb);

            //WHEN
            Optional<BidListDTO> createdBidListDTO = bidListService.createBidList(bidListDTOToCreate);

            //THEN
            assertTrue(createdBidListDTO.isPresent());
            assertNotNull(createdBidListDTO.get().getBidListId());
            assertEquals(bidListDTOToCreate.getAccount(), createdBidListDTO.get().getAccount());
            assertEquals(bidListDTOToCreate.getType(), createdBidListDTO.get().getType());
            assertEquals(bidListDTOToCreate.getBidQuantity(), createdBidListDTO.get().getBidQuantity());

            verify(bidListRepositoryMock, Mockito.times(1))
                    .save(any(BidList.class));
        }
    }


    @Nested
    @DisplayName("findAllBidList tests")
    class FindAllBidListTest {

        @Test
        @DisplayName("GIVEN bidList in DB " +
                "WHEN getting all the bidList " +
                "THEN the returned value is the list of bidList")
        void findAllBidListTest_WithDataInDB() {
            //GIVEN
            BidList bidListInDb = new BidList();
            bidListInDb.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
            bidListInDb.setAccount(TestConstants.EXISTING_BID_LIST_ACCOUNT);
            bidListInDb.setType(TestConstants.EXISTING_BID_LIST_TYPE);
            bidListInDb.setBidQuantity(TestConstants.EXISTING_BID_LIST_BID_QUANTITY);
            List<BidList> bidListList = new ArrayList<>();
            bidListList.add(bidListInDb);
            when(bidListRepositoryMock.findAll()).thenReturn(bidListList);

            //THEN
            List<BidListDTO> bidListDTOList = bidListService.findAllBidList();
            assertEquals(1, bidListDTOList.size());
            assertEquals(bidListInDb.getBidListId(), bidListDTOList.get(0).getBidListId());

            verify(bidListRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN no bidList in DB " +
                "WHEN getting all the bidList " +
                "THEN the returned value is an empty list of bidList")
        void findAllBidListTest_WithNoDataInDB() {
            //GIVEN
            List<BidList> bidListList = new ArrayList<>();
            when(bidListRepositoryMock.findAll()).thenReturn(bidListList);

            //THEN
            List<BidListDTO> bidListDTOList = bidListService.findAllBidList();
            assertThat(bidListDTOList).isEmpty();

            verify(bidListRepositoryMock, Mockito.times(1)).findAll();
        }
    }
}
