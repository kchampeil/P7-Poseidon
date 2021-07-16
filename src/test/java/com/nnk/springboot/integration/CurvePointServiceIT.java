package com.nnk.springboot.integration;

import com.nnk.springboot.DTO.CurvePointDTO;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.services.contracts.ICurvePointService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
public class CurvePointServiceIT {
    @Autowired
    ICurvePointService curvePointService;

    @Autowired
    CurvePointRepository curvePointRepository;

    private CurvePoint curvePointInDb;
    private CurvePointDTO curvePointDTO;

    @BeforeEach
    private void initPerTest() {
        //init a curvePoint in DB for test
        curvePointInDb = new CurvePoint();
        curvePointInDb.setCurveId(TestConstants.EXISTING_CURVE_POINT_CURVE_ID);
        curvePointInDb.setTerm(TestConstants.EXISTING_CURVE_POINT_TERM);
        curvePointInDb.setValue(TestConstants.EXISTING_CURVE_POINT_VALUE);
        curvePointInDb = curvePointRepository.save(curvePointInDb);

        //init common part of curvePointDTO to create/update
        curvePointDTO = new CurvePointDTO();
        curvePointDTO.setCurveId(TestConstants.EXISTING_CURVE_POINT_CURVE_ID);
        curvePointDTO.setTerm(TestConstants.EXISTING_CURVE_POINT_TERM);
        curvePointDTO.setValue(TestConstants.EXISTING_CURVE_POINT_VALUE);
    }

    @AfterEach
    private void cleanPerTest(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanUp")) {
            return;
        }
        //clean DB at the end of the test by deleting the curvePoint created at initialization
        curvePointRepository.deleteById(curvePointInDb.getId());
    }

    @Test
    @DisplayName("WHEN creating a new curvePoint with correct informations  " +
            "THEN the returned value is the added curvePoint, " +
            "AND the curvePoint is added in DB")
    public void createCurvePointIT_WithSuccess() {

        //WHEN
        Optional<CurvePointDTO> curvePointDTOCreated = curvePointService.createCurvePoint(curvePointDTO);

        //THEN
        assertTrue(curvePointDTOCreated.isPresent());
        assertNotNull(curvePointDTOCreated.get().getId());
        assertEquals(curvePointDTO.getValue(), curvePointDTOCreated.get().getValue());

        //cleaning of DB at the end of the test by deleting the curvePoint created during the test
        curvePointRepository.deleteById(curvePointDTOCreated.get().getId());
    }


    @Test
    @DisplayName("WHEN asking for the list of all curvePoint " +
            "THEN the returned value is the list of all curvePoint in DB")
    public void findAllCurvePointIT_WithSuccess() {

        //WHEN
        List<CurvePointDTO> curvePointDTOList = curvePointService.findAllCurvePoint();

        //THEN
        assertThat(curvePointDTOList.size()).isGreaterThan(0);
        assertEquals(curvePointInDb.getId(), curvePointDTOList.get(0).getId());
    }


    @Test
    @DisplayName("WHEN asking for a curvePoint with a specified id " +
            "THEN the returned value is the curvePoint in DB")
    public void findCurvePointByIdIT_WithSuccess() {

        //WHEN
        CurvePointDTO curvePointDTO = curvePointService.findCurvePointById(curvePointInDb.getId());

        //THEN
        assertEquals(curvePointInDb.getId(), curvePointDTO.getId());
        assertEquals(curvePointInDb.getCurveId(), curvePointDTO.getCurveId());
        assertEquals(curvePointInDb.getTerm(), curvePointDTO.getTerm());
        assertEquals(curvePointInDb.getValue(), curvePointDTO.getValue());
    }


    @Test
    @DisplayName("WHEN updating a curvePoint with correct informations  " +
            "THEN the returned value is the updated curvePoint, " +
            "AND the curvePoint is updated in DB")
    public void updateCurvePointIT_WithSuccess() {

        //GIVEN
        curvePointDTO.setId(curvePointInDb.getId());
        curvePointDTO.setValue(TestConstants.NEW_CURVE_POINT_VALUE);

        //WHEN
        CurvePointDTO curvePointDTOUpdated = curvePointService.updateCurvePoint(curvePointDTO);
        Optional<CurvePoint> curvePointUpdated = curvePointRepository.findById(curvePointInDb.getId());

        //THEN
        assertNotNull(curvePointDTOUpdated);
        assertEquals(curvePointDTO.getValue(), curvePointDTOUpdated.getValue());
        assertEquals(curvePointDTO.getCurveId(), curvePointDTOUpdated.getCurveId());

        assertTrue(curvePointUpdated.isPresent());
        assertEquals(curvePointDTO.getValue(), curvePointUpdated.get().getValue());
    }


    @Test
    @Tag("SkipCleanUp")
    @DisplayName("WHEN deleting a curvePoint with correct informations  " +
            "THEN the curvePoint is deleted in DB")
    public void deleteCurvePointIT_WithSuccess() {

        //WHEN
        curvePointService.deleteCurvePoint(curvePointInDb.getId());
        Optional<CurvePoint> curvePointDeleted = curvePointRepository.findById(curvePointInDb.getId());

        //THEN
        assertFalse(curvePointDeleted.isPresent());
    }    
}
