package com.nnk.springboot.integration;

import com.nnk.springboot.DTO.RuleNameDTO;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.services.contracts.IRuleNameService;
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
public class RuleNameServiceIT {
    @Autowired
    IRuleNameService ruleNameService;

    @Autowired
    RuleNameRepository ruleNameRepository;

    private RuleName ruleNameInDb;
    private RuleNameDTO ruleNameDTO;

    @BeforeEach
    private void initPerTest() {
        //init a ruleName in DB for test
        ruleNameInDb = new RuleName();
        ruleNameInDb.setName(TestConstants.EXISTING_RULE_NAME_NAME);
        ruleNameInDb.setDescription(TestConstants.EXISTING_RULE_NAME_DESCRIPTION);
        ruleNameInDb.setJson(TestConstants.EXISTING_RULE_NAME_JSON);
        ruleNameInDb.setTemplate(TestConstants.EXISTING_RULE_NAME_TEMPLATE);
        ruleNameInDb.setSqlStr(TestConstants.EXISTING_RULE_NAME_SQLSTR);
        ruleNameInDb.setSqlPart(TestConstants.EXISTING_RULE_NAME_SQLPART);
        ruleNameInDb = ruleNameRepository.save(ruleNameInDb);

        //init common part of ruleNameDTO to create/update
        ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setName(TestConstants.EXISTING_RULE_NAME_NAME);
        ruleNameDTO.setDescription(TestConstants.EXISTING_RULE_NAME_DESCRIPTION);
        ruleNameDTO.setJson(TestConstants.EXISTING_RULE_NAME_JSON);
        ruleNameDTO.setTemplate(TestConstants.EXISTING_RULE_NAME_TEMPLATE);
        ruleNameDTO.setSqlStr(TestConstants.EXISTING_RULE_NAME_SQLSTR);
        ruleNameDTO.setSqlPart(TestConstants.EXISTING_RULE_NAME_SQLPART);
    }

    @AfterEach
    private void cleanPerTest(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanUp")) {
            return;
        }
        //clean DB at the end of the test by deleting the ruleName created at initialization
        ruleNameRepository.deleteById(ruleNameInDb.getId());
    }

    @Test
    @DisplayName("WHEN creating a new ruleName with correct informations  " +
            "THEN the returned value is the added ruleName, " +
            "AND the ruleName is added in DB")
    public void createIT_WithSuccess() {

        //WHEN
        Optional<RuleNameDTO> ruleNameDTOCreated = ruleNameService.create(ruleNameDTO);

        //THEN
        assertTrue(ruleNameDTOCreated.isPresent());
        assertNotNull(ruleNameDTOCreated.get().getId());
        assertEquals(ruleNameDTO.getName(), ruleNameDTOCreated.get().getName());

        //cleaning of DB at the end of the test by deleting the ruleName created during the test
        ruleNameRepository.deleteById(ruleNameDTOCreated.get().getId());
    }


    @Test
    @DisplayName("WHEN asking for the list of all ruleName " +
            "THEN the returned value is the list of all ruleName in DB")
    public void findAllIT_WithSuccess() {

        //WHEN
        List<RuleNameDTO> ruleNameDTOList = ruleNameService.findAll();

        //THEN
        assertThat(ruleNameDTOList.size()).isGreaterThan(0);
        assertEquals(ruleNameInDb.getId(), ruleNameDTOList.get(0).getId());
    }


    @Test
    @DisplayName("WHEN asking for a ruleName with a specified id " +
            "THEN the returned value is the ruleName in DB")
    public void findByIdIT_WithSuccess() {

        //WHEN
        RuleNameDTO ruleNameDTO = ruleNameService.findById(ruleNameInDb.getId());

        //THEN
        assertEquals(ruleNameInDb.getId(), ruleNameDTO.getId());
        assertEquals(ruleNameInDb.getName(), ruleNameDTO.getName());
        assertEquals(ruleNameInDb.getDescription(), ruleNameDTO.getDescription());
        assertEquals(ruleNameInDb.getTemplate(), ruleNameDTO.getTemplate());
        assertEquals(ruleNameInDb.getSqlStr(), ruleNameDTO.getSqlStr());
    }


    @Test
    @DisplayName("WHEN updating a ruleName with correct informations  " +
            "THEN the returned value is the updated ruleName, " +
            "AND the ruleName is updated in DB")
    public void updateIT_WithSuccess() {

        //GIVEN
        ruleNameDTO.setId(ruleNameInDb.getId());
        ruleNameDTO.setDescription(TestConstants.NEW_RULE_NAME_DESCRIPTION);

        //WHEN
        RuleNameDTO ruleNameDTOUpdated = ruleNameService.update(ruleNameDTO);
        Optional<RuleName> ruleNameUpdated = ruleNameRepository.findById(ruleNameInDb.getId());

        //THEN
        assertNotNull(ruleNameDTOUpdated);
        assertEquals(ruleNameDTO.getDescription(), ruleNameDTOUpdated.getDescription());

        assertTrue(ruleNameUpdated.isPresent());
        assertEquals(ruleNameDTO.getDescription(), ruleNameUpdated.get().getDescription());
    }


    @Test
    @Tag("SkipCleanUp")
    @DisplayName("WHEN deleting a ruleName with correct informations  " +
            "THEN the ruleName is deleted in DB")
    public void deleteIT_WithSuccess() {

        //WHEN
        ruleNameService.delete(ruleNameInDb.getId());
        Optional<RuleName> ruleNameDeleted = ruleNameRepository.findById(ruleNameInDb.getId());

        //THEN
        assertFalse(ruleNameDeleted.isPresent());
    }
}
