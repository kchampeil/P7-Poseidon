package com.nnk.springboot.services;

import com.nnk.springboot.DTO.CurvePointDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.domain.CurvePoint;
import com.nnk.springboot.repositories.CurvePointRepository;
import com.nnk.springboot.services.contracts.ICurvePointService;
import com.nnk.springboot.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nnk.springboot.constants.PoseidonExceptionsConstants.CURVE_POINT_ID_NOT_VALID;

@Slf4j
@Service
@Transactional
public class CurvePointService implements ICurvePointService {
    private final CurvePointRepository curvePointRepository;

    @Autowired
    CurvePointService(CurvePointRepository curvePointRepository) {
        this.curvePointRepository = curvePointRepository;
    }

    /**
     * Create a curvePoint
     *
     * @param curvePointDTOToCreate a curvePoint to create
     * @return the created curvePoint
     */
    @Override
    public Optional<CurvePointDTO> create(CurvePointDTO curvePointDTOToCreate) {

        log.debug(LogConstants.CREATE_CURVE_POINT_CALL + curvePointDTOToCreate.toString());

        ModelMapper modelMapper = new ModelMapper();
        CurvePoint curvePointCreated;

        try {
            CurvePoint curvePointToCreate = modelMapper.map(curvePointDTOToCreate, CurvePoint.class);
            curvePointToCreate.setCreationDate(DateUtil.getCurrentLocalDateTime());
            curvePointCreated = curvePointRepository.save(curvePointToCreate);
            log.debug(LogConstants.CREATE_CURVE_POINT_OK + curvePointCreated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.CREATE_CURVE_POINT_ERROR + curvePointDTOToCreate);
            throw exception;
        }

        return Optional.ofNullable(modelMapper.map(curvePointCreated, CurvePointDTO.class));
    }


    /**
     * Get all curvePoint
     *
     * @return the list of curvePoint
     */
    @Override
    public List<CurvePointDTO> findAll() {
        log.debug(LogConstants.FIND_CURVE_POINT_ALL_CALL);

        List<CurvePoint> curvePointList = curvePointRepository.findAll();
        List<CurvePointDTO> curvePointDTOList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        curvePointList.forEach(curvePoint ->
                curvePointDTOList.add(modelMapper.map(curvePoint, CurvePointDTO.class)));
        log.debug(LogConstants.FIND_CURVE_POINT_ALL_OK, curvePointDTOList.size());

        return curvePointDTOList;
    }


    /**
     * Get a curvePoint by its id
     *
     * @param id of the curvePoint we want to retrieve
     * @return a CurvePointDTO filled with CurvePoint informations
     * @throws IllegalArgumentException if no curvePoint found
     */
    @Override
    public CurvePointDTO findById(Integer id) {
        log.debug(LogConstants.FIND_CURVE_POINT_BY_ID_CALL);

        Optional<CurvePoint> curvePoint = curvePointRepository.findById(id);

        if (curvePoint.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            CurvePointDTO curvePointDTO = modelMapper.map(curvePoint.get(), CurvePointDTO.class);

            log.debug(LogConstants.FIND_CURVE_POINT_BY_ID_OK + id + "\n");
            return curvePointDTO;
        } else {
            log.error(CURVE_POINT_ID_NOT_VALID + id);
            throw new IllegalArgumentException(CURVE_POINT_ID_NOT_VALID + id);
        }
    }


    /**
     * Update a curvePoint
     *
     * @param curvePointDTOToUpdate a curvePoint to update
     * @return the created curvePoint
     */
    @Override
    public CurvePointDTO update(CurvePointDTO curvePointDTOToUpdate) {
        log.debug(LogConstants.UPDATE_CURVE_POINT_CALL + curvePointDTOToUpdate.toString());

        ModelMapper modelMapper = new ModelMapper();
        CurvePoint curvePointUpdated;

        try {
            curvePointUpdated = curvePointRepository.save(modelMapper.map(curvePointDTOToUpdate, CurvePoint.class));
            log.debug(LogConstants.UPDATE_CURVE_POINT_OK + curvePointUpdated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.UPDATE_CURVE_POINT_ERROR + curvePointDTOToUpdate);
            throw exception;
        }

        return modelMapper.map(curvePointUpdated, CurvePointDTO.class);
    }


    /**
     * delete a curvePoint
     *
     * @param id of the curvePoint to delete
     */
    @Override
    public void delete(Integer id) {

        log.debug(LogConstants.DELETE_CURVE_POINT_CALL + id);

        if (id == null) {
            log.error(LogConstants.DELETE_CURVE_POINT_ERROR + "id is null");
            throw new IllegalArgumentException(CURVE_POINT_ID_NOT_VALID + "null");
        }

        //Find curvePoint by Id
        CurvePoint curvePoint = curvePointRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(CURVE_POINT_ID_NOT_VALID + id);
                    return new IllegalArgumentException(CURVE_POINT_ID_NOT_VALID + id);
                });

        //Delete the curvePoint
        try {
            curvePointRepository.delete(curvePoint);
            log.debug(LogConstants.DELETE_CURVE_POINT_OK + id);

        } catch (Exception exception) {
            log.error(LogConstants.DELETE_CURVE_POINT_ERROR + id);
            throw exception;
        }
    }
}
