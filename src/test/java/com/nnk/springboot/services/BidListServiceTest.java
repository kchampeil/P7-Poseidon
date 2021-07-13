package com.nnk.springboot.services;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.contracts.IBidListService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeAll;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class BidListServiceTest {

    @MockBean
    BidListRepository bidListRepositoryMock;

    @Autowired
    IBidListService bidListService;

    private static BidListDTO bidListDTOWithValues;

    private BidList bidListInDb;

    @BeforeAll
    static void setUp() {
        bidListDTOWithValues = new BidListDTO();
        bidListDTOWithValues.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
        bidListDTOWithValues.setAccount(TestConstants.NEW_BID_LIST_ACCOUNT);
        bidListDTOWithValues.setType(TestConstants.NEW_BID_LIST_TYPE);
        bidListDTOWithValues.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);
    }

    @Nested
    @DisplayName("CreateBidList tests")
    class CreateBidListTest {

        @Test
        @DisplayName("GIVEN a new bidList (DTO) to add " +
                "WHEN saving this new bidList " +
                "THEN the returned value is the added bidList (DTO)")
        void createBidListTest_WithSuccess() {
            //GIVEN
            bidListInDb = new BidList();
            bidListInDb.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
            bidListInDb.setAccount(bidListDTOWithValues.getAccount());
            bidListInDb.setType(bidListDTOWithValues.getType());
            bidListInDb.setBidQuantity(bidListDTOWithValues.getBidQuantity());
            when(bidListRepositoryMock.save(any(BidList.class))).thenReturn(bidListInDb);

            //WHEN
            Optional<BidListDTO> createdBidListDTO = bidListService.createBidList(bidListDTOWithValues);

            //THEN
            assertTrue(createdBidListDTO.isPresent());
            assertNotNull(createdBidListDTO.get().getBidListId());
            assertEquals(bidListDTOWithValues.getAccount(), createdBidListDTO.get().getAccount());
            assertEquals(bidListDTOWithValues.getType(), createdBidListDTO.get().getType());
            assertEquals(bidListDTOWithValues.getBidQuantity(), createdBidListDTO.get().getBidQuantity());

            verify(bidListRepositoryMock, Mockito.times(1))
                    .save(any(BidList.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex bidList " +
                "THEN an exception is thrown")
        void createBidListTest_WithException() {
            //GIVEN
            when(bidListRepositoryMock.save(any(BidList.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> bidListService.createBidList(bidListDTOWithValues));

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
            bidListInDb = new BidList();
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


    @Nested
    @DisplayName("findBidListById tests")
    class FindBidListByIdTest {

        @Test
        @DisplayName("GIVEN bidList in DB for a specified id" +
                "WHEN getting the bidList on id " +
                "THEN the returned value is the bidList")
        void findBidListByIdTest_WithDataInDB() {
            //GIVEN
            bidListInDb = new BidList();
            bidListInDb.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
            bidListInDb.setAccount(TestConstants.EXISTING_BID_LIST_ACCOUNT);
            bidListInDb.setType(TestConstants.EXISTING_BID_LIST_TYPE);
            bidListInDb.setBidQuantity(TestConstants.EXISTING_BID_LIST_BID_QUANTITY);
            when(bidListRepositoryMock.findById(anyInt())).thenReturn(Optional.of(bidListInDb));

            //THEN
            BidListDTO bidListDTO = bidListService.findBidListById(TestConstants.EXISTING_BID_LIST_ID);
            assertEquals(bidListInDb.getBidListId(), bidListDTO.getBidListId());
            assertEquals(bidListInDb.getAccount(), bidListDTO.getAccount());

            verify(bidListRepositoryMock, Mockito.times(1)).findById(anyInt());
        }

        @Test
        @DisplayName("GIVEN no bidList in DB for a specified id " +
                "WHEN getting all the bidList " +
                "THEN the returned value is a null bidList")
        void findBidListByIdTest_WithNoDataInDB() {
            //GIVEN
            when(bidListRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> bidListService.findBidListById(TestConstants.EXISTING_BID_LIST_ID));
            assertEquals(PoseidonExceptionsConstants.BID_LIST_ID_NOT_VALID
                    + TestConstants.EXISTING_BID_LIST_ID, exception.getMessage());

            verify(bidListRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("updateBidList tests")
    class UpdateBidListTest {

        @Test
        @DisplayName("GIVEN a bidList to update " +
                "WHEN updating this bidList " +
                "THEN the returned value is the updated bidList")
        void updateBidListTest_WithSuccess() {
            //GIVEN
            bidListInDb = new BidList();
            bidListInDb.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
            bidListInDb.setAccount(bidListDTOWithValues.getAccount());
            bidListInDb.setType(bidListDTOWithValues.getType());
            bidListInDb.setBidQuantity(bidListDTOWithValues.getBidQuantity());
            when(bidListRepositoryMock.save(any(BidList.class))).thenReturn(bidListInDb);

            //WHEN
            BidListDTO createdBidListDTO = bidListService.updateBidList(bidListDTOWithValues);

            //THEN
            assertEquals(bidListDTOWithValues.toStringForLogs(), createdBidListDTO.toStringForLogs());

            verify(bidListRepositoryMock, Mockito.times(1))
                    .save(any(BidList.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a bidList " +
                "THEN an exception is thrown")
        void updateBidListTest_WithException() {
            //GIVEN
            when(bidListRepositoryMock.save(any(BidList.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> bidListService.updateBidList(bidListDTOWithValues));

            verify(bidListRepositoryMock, Mockito.times(1))
                    .save(any(BidList.class));
        }
    }
}
