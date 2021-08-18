package com.nnk.springboot.services;

import com.nnk.springboot.DTO.CurvePointDTO;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.services.contracts.ICurvePointService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CurvePointServiceTest {

    @MockBean
    CurvePointRepository curvePointRepositoryMock;

    @Autowired
    ICurvePointService curvePointService;

    private static CurvePointDTO curvePointDTOWithValues;

    private static CurvePoint curvePointInDb;

    @BeforeAll
    static void setUp() {
        curvePointDTOWithValues = new CurvePointDTO();
        curvePointDTOWithValues.setId(TestConstants.EXISTING_CURVE_POINT_ID);
        curvePointDTOWithValues.setCurveId(TestConstants.NEW_CURVE_POINT_CURVE_ID);
        curvePointDTOWithValues.setTerm(TestConstants.NEW_CURVE_POINT_TERM);
        curvePointDTOWithValues.setValue(TestConstants.NEW_CURVE_POINT_VALUE);

        curvePointInDb = new CurvePoint();
        curvePointInDb.setId(curvePointDTOWithValues.getId());
        curvePointInDb.setCurveId(curvePointDTOWithValues.getCurveId());
        curvePointInDb.setTerm(curvePointDTOWithValues.getTerm());
        curvePointInDb.setValue(curvePointDTOWithValues.getValue());
    }

    @Nested
    @DisplayName("create tests")
    class CreateTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex curvePoint " +
                "THEN an exception is thrown")
        void createTest_WithException() {
            //GIVEN
            when(curvePointRepositoryMock.save(any(CurvePoint.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> curvePointService.create(curvePointDTOWithValues));

            verify(curvePointRepositoryMock, Mockito.times(1))
                    .save(any(CurvePoint.class));
        }
    }


    @Nested
    @DisplayName("findAll tests")
    class FindAllTest {

        @Test
        @DisplayName("GIVEN curvePoint in DB " +
                "WHEN getting all the curvePoint " +
                "THEN the returned value is the list of curvePoint")
        void findAllTest_WithDataInDB() {
            //GIVEN
            List<CurvePoint> curvePointList = new ArrayList<>();
            curvePointList.add(curvePointInDb);
            when(curvePointRepositoryMock.findAll()).thenReturn(curvePointList);

            //THEN
            List<CurvePointDTO> curvePointDTOList = curvePointService.findAll();
            assertEquals(1, curvePointDTOList.size());
            assertEquals(curvePointInDb.getId(), curvePointDTOList.get(0).getId());

            verify(curvePointRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN no curvePoint in DB " +
                "WHEN getting all the curvePoint " +
                "THEN the returned value is an empty list of curvePoint")
        void findAllTest_WithNoDataInDB() {
            //GIVEN
            List<CurvePoint> curvePointList = new ArrayList<>();
            when(curvePointRepositoryMock.findAll()).thenReturn(curvePointList);

            //THEN
            List<CurvePointDTO> curvePointDTOList = curvePointService.findAll();
            assertThat(curvePointDTOList).isEmpty();

            verify(curvePointRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findById tests")
    class FindByIdTest {

        @Test
        @DisplayName("GIVEN no curvePoint in DB for a specified id " +
                "WHEN getting all the curvePoint " +
                "THEN the returned value is a null curvePoint")
        void findByIdTest_WithNoDataInDB() {
            //GIVEN
            when(curvePointRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> curvePointService.findById(TestConstants.EXISTING_CURVE_POINT_ID));
            assertEquals(PoseidonExceptionsConstants.CURVE_POINT_ID_NOT_VALID
                    + TestConstants.EXISTING_CURVE_POINT_ID, exception.getMessage());

            verify(curvePointRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("update tests")
    class UpdateTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a curvePoint " +
                "THEN an exception is thrown")
        void updateTest_WithException() {
            //GIVEN
            when(curvePointRepositoryMock.save(any(CurvePoint.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> curvePointService.update(curvePointDTOWithValues));

            verify(curvePointRepositoryMock, Mockito.times(1))
                    .save(any(CurvePoint.class));
        }
    }


    @Nested
    @DisplayName("delete tests")
    class DeleteTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a curvePoint " +
                "THEN an exception is thrown")
        void deleteTest_WithException() {
            //GIVEN
            when(curvePointRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(curvePointInDb));
            doThrow(new RuntimeException()).when(curvePointRepositoryMock).delete(any(CurvePoint.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> curvePointService.delete(curvePointInDb.getId()));

            verify(curvePointRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(curvePointRepositoryMock, Mockito.times(1))
                    .delete(any(CurvePoint.class));
        }


        @Test
        @DisplayName("GIVEN no curvePoint in DB for the specified id " +
                "WHEN deleting a curvePoint " +
                "THEN an exception is thrown")
        void deleteTest_WithNoDataInDb() {
            //GIVEN
            when(curvePointRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> curvePointService.delete(curvePointInDb.getId()));

            verify(curvePointRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(curvePointRepositoryMock, Mockito.times(0))
                    .delete(any(CurvePoint.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a curvePoint " +
                "THEN an exception is thrown")
        void deleteTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> curvePointService.delete(null));

            verify(curvePointRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(curvePointRepositoryMock, Mockito.times(0))
                    .delete(any(CurvePoint.class));
        }
    }
}
