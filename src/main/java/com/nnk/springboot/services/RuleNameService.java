package com.nnk.springboot.services;

import com.nnk.springboot.DTO.RuleNameDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.services.contracts.IRuleNameService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nnk.springboot.constants.PoseidonExceptionsConstants.RULE_NAME_ID_NOT_VALID;

@Slf4j
@Service
@Transactional
public class RuleNameService implements IRuleNameService {
    private final RuleNameRepository ruleNameRepository;

    @Autowired
    RuleNameService(RuleNameRepository ruleNameRepository) {
        this.ruleNameRepository = ruleNameRepository;
    }

    /**
     * Create a ruleName
     *
     * @param ruleNameDTOToCreate a ruleName to create
     * @return the created ruleName
     */
    @Override
    public Optional<RuleNameDTO> create(RuleNameDTO ruleNameDTOToCreate) {

        log.debug(LogConstants.CREATE_RULE_NAME_CALL + ruleNameDTOToCreate.toString());

        ModelMapper modelMapper = new ModelMapper();
        RuleName ruleNameCreated;

        try {
            ruleNameCreated = ruleNameRepository.save(modelMapper.map(ruleNameDTOToCreate, RuleName.class));
            log.debug(LogConstants.CREATE_RULE_NAME_OK + ruleNameCreated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.CREATE_RULE_NAME_ERROR + ruleNameDTOToCreate);
            throw exception;
        }

        return Optional.ofNullable(modelMapper.map(ruleNameCreated, RuleNameDTO.class));
    }


    /**
     * Get all ruleName
     *
     * @return the list of ruleName
     */
    @Override
    public List<RuleNameDTO> findAll() {
        log.debug(LogConstants.FIND_RULE_NAME_ALL_CALL);

        List<RuleName> ruleNameList = ruleNameRepository.findAll();
        List<RuleNameDTO> ruleNameDTOList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        ruleNameList.forEach(ruleName ->
                ruleNameDTOList.add(modelMapper.map(ruleName, RuleNameDTO.class)));
        log.debug(LogConstants.FIND_RULE_NAME_ALL_OK, ruleNameDTOList.size());

        return ruleNameDTOList;
    }


    /**
     * Get a ruleName by its id
     *
     * @param id of the ruleName we want to retrieve
     * @return a RuleNameDTO filled with RuleName informations
     * @throws IllegalArgumentException if no ruleName found
     */
    @Override
    public RuleNameDTO findById(Integer id) {
        log.debug(LogConstants.FIND_RULE_NAME_BY_ID_CALL);

        Optional<RuleName> ruleName = ruleNameRepository.findById(id);

        if (ruleName.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            RuleNameDTO ruleNameDTO = modelMapper.map(ruleName.get(), RuleNameDTO.class);

            log.debug(LogConstants.FIND_RULE_NAME_BY_ID_OK + id + "\n");
            return ruleNameDTO;
        } else {
            log.error(RULE_NAME_ID_NOT_VALID + id);
            throw new IllegalArgumentException(RULE_NAME_ID_NOT_VALID + id);
        }
    }


    /**
     * Update a ruleName
     *
     * @param ruleNameDTOToUpdate a ruleName to update
     * @return the created ruleName
     */
    @Override
    public RuleNameDTO update(RuleNameDTO ruleNameDTOToUpdate) {
        log.debug(LogConstants.UPDATE_RULE_NAME_CALL + ruleNameDTOToUpdate.toString());

        ModelMapper modelMapper = new ModelMapper();
        RuleName ruleNameUpdated;

        try {
            ruleNameUpdated = ruleNameRepository.save(modelMapper.map(ruleNameDTOToUpdate, RuleName.class));
            log.debug(LogConstants.UPDATE_RULE_NAME_OK + ruleNameUpdated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.UPDATE_RULE_NAME_ERROR + ruleNameDTOToUpdate);
            throw exception;
        }

        return modelMapper.map(ruleNameUpdated, RuleNameDTO.class);
    }


    /**
     * delete a ruleName
     *
     * @param id of the ruleName to delete
     */
    @Override
    public void delete(Integer id) {

        log.debug(LogConstants.DELETE_RULE_NAME_CALL + id);

        if (id == null) {
            log.error(LogConstants.DELETE_RULE_NAME_ERROR + "id is null");
            throw new IllegalArgumentException(RULE_NAME_ID_NOT_VALID + "null");
        }

        //Find ruleName by Id
        RuleName ruleName = ruleNameRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(RULE_NAME_ID_NOT_VALID + id);
                    return new IllegalArgumentException(RULE_NAME_ID_NOT_VALID + id);
                });

        //Delete the ruleName
        try {
            ruleNameRepository.delete(ruleName);
            log.debug(LogConstants.DELETE_RULE_NAME_OK + id);

        } catch (Exception exception) {
            log.error(LogConstants.DELETE_RULE_NAME_ERROR + id);
            throw exception;
        }
    }
}
