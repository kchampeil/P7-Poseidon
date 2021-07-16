package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.CurvePointDTO;

import java.util.List;
import java.util.Optional;

public interface ICurvePointService {
    Optional<CurvePointDTO> createCurvePoint(CurvePointDTO curvePointDTOToCreate);

    List<CurvePointDTO> findAllCurvePoint();

    CurvePointDTO findCurvePointById(Integer id);

    CurvePointDTO updateCurvePoint(CurvePointDTO curvePointDTOToUpdate);

    void deleteCurvePoint(Integer id);
}
