package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.RuleNameDTO;

import java.util.List;
import java.util.Optional;

public interface IRuleNameService {
        Optional<RuleNameDTO> createRuleName(RuleNameDTO ruleNameDTOToCreate);

        List<RuleNameDTO> findAllRuleName();

        RuleNameDTO findRuleNameById(Integer id);

        RuleNameDTO updateRuleName(RuleNameDTO ruleNameDTOToUpdate);

        void deleteRuleName(Integer id);
}
