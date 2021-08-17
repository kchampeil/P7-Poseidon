package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.RuleNameDTO;

import java.util.List;
import java.util.Optional;

public interface IRuleNameService {
        Optional<RuleNameDTO> create(RuleNameDTO ruleNameDTOToCreate);

        List<RuleNameDTO> findAll();

        RuleNameDTO findById(Integer id);

        RuleNameDTO update(RuleNameDTO ruleNameDTOToUpdate);

        void delete(Integer id);
}
