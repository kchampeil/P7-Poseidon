package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.CurvePointDTO;

import java.util.List;
import java.util.Optional;

public interface ICurvePointService {
    Optional<CurvePointDTO> create(CurvePointDTO curvePointDTOToCreate);

    List<CurvePointDTO> findAll();

    CurvePointDTO findById(Integer id);

    CurvePointDTO update(CurvePointDTO curvePointDTOToUpdate);

    void delete(Integer id);
}
