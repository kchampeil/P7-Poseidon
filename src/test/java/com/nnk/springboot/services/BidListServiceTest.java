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
class BidListServiceTest {

    @MockBean
    BidListRepository bidListRepositoryMock;

    @Autowired
    IBidListService bidListService;

    private static BidListDTO bidListDTOWithValues;

    private static BidList bidListInDb;

    @BeforeAll
    static void setUp() {
        bidListDTOWithValues = new BidListDTO();
        bidListDTOWithValues.setBidListId(TestConstants.EXISTING_BID_LIST_ID);
        bidListDTOWithValues.setAccount(TestConstants.NEW_BID_LIST_ACCOUNT);
        bidListDTOWithValues.setType(TestConstants.NEW_BID_LIST_TYPE);
        bidListDTOWithValues.setBidQuantity(TestConstants.NEW_BID_LIST_BID_QUANTITY);

        bidListInDb = new BidList();
        bidListInDb.setBidListId(bidListDTOWithValues.getBidListId());
        bidListInDb.setAccount(bidListDTOWithValues.getAccount());
        bidListInDb.setType(bidListDTOWithValues.getType());
        bidListInDb.setBidQuantity(bidListDTOWithValues.getBidQuantity());
    }

    @Nested
    @DisplayName("create tests")
    class CreateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex bidList " +
                "THEN an exception is thrown")
        void createTest_WithException() {
            //GIVEN
            when(bidListRepositoryMock.save(any(BidList.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> bidListService.create(bidListDTOWithValues));

            verify(bidListRepositoryMock, Mockito.times(1))
                    .save(any(BidList.class));
        }
    }


    @Nested
    @DisplayName("findAll tests")
    class FindAllTest {

        @Test
        @DisplayName("GIVEN no bidList in DB " +
                "WHEN getting all the bidList " +
                "THEN the returned value is an empty list of bidList")
        void findAllTest_WithNoDataInDB() {
            //GIVEN
            List<BidList> bidListList = new ArrayList<>();
            when(bidListRepositoryMock.findAll()).thenReturn(bidListList);

            //THEN
            List<BidListDTO> bidListDTOList = bidListService.findAll();
            assertThat(bidListDTOList).isEmpty();

            verify(bidListRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findById tests")
    class FindByIdTest {

        @Test
        @DisplayName("GIVEN no bidList in DB for a specified id " +
                "WHEN getting all the bidList " +
                "THEN the returned value is a null bidList")
        void findByIdTest_WithNoDataInDB() {
            //GIVEN
            when(bidListRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> bidListService.findById(TestConstants.EXISTING_BID_LIST_ID));
            assertEquals(PoseidonExceptionsConstants.BID_LIST_ID_NOT_VALID
                    + TestConstants.EXISTING_BID_LIST_ID, exception.getMessage());

            verify(bidListRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("update tests")
    class UpdateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a bidList " +
                "THEN an exception is thrown")
        void updateTest_WithException() {
            //GIVEN
            when(bidListRepositoryMock.save(any(BidList.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> bidListService.update(bidListDTOWithValues));

            verify(bidListRepositoryMock, Mockito.times(1))
                    .save(any(BidList.class));
        }
    }


    @Nested
    @DisplayName("delete tests")
    class DeleteTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a bidList " +
                "THEN an exception is thrown")
        void deleteTest_WithException() {
            //GIVEN
            when(bidListRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(bidListInDb));
            doThrow(new RuntimeException()).when(bidListRepositoryMock).delete(any(BidList.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> bidListService.delete(bidListInDb.getBidListId()));

            verify(bidListRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(bidListRepositoryMock, Mockito.times(1))
                    .delete(any(BidList.class));
        }


        @Test
        @DisplayName("GIVEN no bidList in DB for the specified id " +
                "WHEN deleting a bidList " +
                "THEN an exception is thrown")
        void deleteTest_WithNoDataInDb() {
            //GIVEN
            when(bidListRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> bidListService.delete(bidListInDb.getBidListId()));

            verify(bidListRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(bidListRepositoryMock, Mockito.times(0))
                    .delete(any(BidList.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a bidList " +
                "THEN an exception is thrown")
        void deleteTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> bidListService.delete(null));

            verify(bidListRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(bidListRepositoryMock, Mockito.times(0))
                    .delete(any(BidList.class));
        }
    }
}
