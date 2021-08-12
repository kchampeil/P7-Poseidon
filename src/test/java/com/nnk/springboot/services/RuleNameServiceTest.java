package com.nnk.springboot.services;

import com.nnk.springboot.DTO.RuleNameDTO;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.RuleName;
import com.nnk.springboot.repositories.RuleNameRepository;
import com.nnk.springboot.services.contracts.IRuleNameService;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RuleNameServiceTest {

    @MockBean
    RuleNameRepository ruleNameRepositoryMock;

    @Autowired
    IRuleNameService ruleNameService;

    private static RuleNameDTO ruleNameDTOWithValues;

    private static RuleName ruleNameInDb;

    @BeforeAll
    static void setUp() {
        ruleNameDTOWithValues = new RuleNameDTO();
        ruleNameDTOWithValues.setId(TestConstants.EXISTING_RULE_NAME_ID);
        ruleNameDTOWithValues.setName(TestConstants.NEW_RULE_NAME_NAME);
        ruleNameDTOWithValues.setDescription(TestConstants.NEW_RULE_NAME_DESCRIPTION);
        ruleNameDTOWithValues.setJson(TestConstants.NEW_RULE_NAME_JSON);
        ruleNameDTOWithValues.setTemplate(TestConstants.NEW_RULE_NAME_TEMPLATE);
        ruleNameDTOWithValues.setSqlStr(TestConstants.NEW_RULE_NAME_SQLSTR);
        ruleNameDTOWithValues.setSqlPart(TestConstants.NEW_RULE_NAME_SQLPART);

        ruleNameInDb = new RuleName();
        ruleNameInDb.setId(ruleNameDTOWithValues.getId());
        ruleNameInDb.setName(ruleNameDTOWithValues.getName());
        ruleNameInDb.setDescription(ruleNameDTOWithValues.getDescription());
        ruleNameInDb.setJson(ruleNameDTOWithValues.getJson());
        ruleNameInDb.setTemplate(ruleNameDTOWithValues.getTemplate());
        ruleNameInDb.setSqlStr(ruleNameDTOWithValues.getSqlStr());
        ruleNameInDb.setSqlPart(ruleNameDTOWithValues.getSqlPart());
    }

    @Nested
    @DisplayName("CreateRuleName tests")
    class CreateRuleNameTest {

        @Test
        @DisplayName("GIVEN a new ruleName (DTO) to add " +
                "WHEN saving this new ruleName " +
                "THEN the returned value is the added ruleName (DTO)")
        void createRuleNameTest_WithSuccess() {
            //GIVEN
            when(ruleNameRepositoryMock.save(any(RuleName.class))).thenReturn(ruleNameInDb);

            //WHEN
            Optional<RuleNameDTO> createdRuleNameDTO = ruleNameService.createRuleName(ruleNameDTOWithValues);

            //THEN
            assertTrue(createdRuleNameDTO.isPresent());
            assertNotNull(createdRuleNameDTO.get().getId());
            assertEquals(ruleNameDTOWithValues.toString(), createdRuleNameDTO.get().toString());

            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .save(any(RuleName.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex ruleName " +
                "THEN an exception is thrown")
        void createRuleNameTest_WithException() {
            //GIVEN
            when(ruleNameRepositoryMock.save(any(RuleName.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ruleNameService.createRuleName(ruleNameDTOWithValues));

            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .save(any(RuleName.class));
        }
    }


    @Nested
    @DisplayName("findAllRuleName tests")
    class FindAllRuleNameTest {

        @Test
        @DisplayName("GIVEN ruleName in DB " +
                "WHEN getting all the ruleName " +
                "THEN the returned value is the list of ruleName")
        void findAllRuleNameTest_WithDataInDB() {
            //GIVEN
            List<RuleName> ruleNameList = new ArrayList<>();
            ruleNameList.add(ruleNameInDb);
            when(ruleNameRepositoryMock.findAll()).thenReturn(ruleNameList);

            //THEN
            List<RuleNameDTO> ruleNameDTOList = ruleNameService.findAllRuleName();
            assertEquals(1, ruleNameDTOList.size());
            assertEquals(ruleNameInDb.getId(), ruleNameDTOList.get(0).getId());

            verify(ruleNameRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN no ruleName in DB " +
                "WHEN getting all the ruleName " +
                "THEN the returned value is an empty list of ruleName")
        void findAllRuleNameTest_WithNoDataInDB() {
            //GIVEN
            List<RuleName> ruleNameList = new ArrayList<>();
            when(ruleNameRepositoryMock.findAll()).thenReturn(ruleNameList);

            //THEN
            List<RuleNameDTO> ruleNameDTOList = ruleNameService.findAllRuleName();
            assertThat(ruleNameDTOList).isEmpty();

            verify(ruleNameRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findRuleNameById tests")
    class FindRuleNameByIdTest {

        @Test
        @DisplayName("GIVEN ruleName in DB for a specified id" +
                "WHEN getting the ruleName on id " +
                "THEN the returned value is the ruleName")
        void findRuleNameByIdTest_WithDataInDB() {
            //GIVEN
            when(ruleNameRepositoryMock.findById(anyInt())).thenReturn(Optional.of(ruleNameInDb));

            //THEN
            RuleNameDTO ruleNameDTO = ruleNameService.findRuleNameById(TestConstants.EXISTING_RULE_NAME_ID);
            assertEquals(ruleNameInDb.getId(), ruleNameDTO.getId());
            assertEquals(ruleNameInDb.getName(), ruleNameDTO.getName());

            verify(ruleNameRepositoryMock, Mockito.times(1)).findById(anyInt());
        }

        @Test
        @DisplayName("GIVEN no ruleName in DB for a specified id " +
                "WHEN getting all the ruleName " +
                "THEN the returned value is a null ruleName")
        void findRuleNameByIdTest_WithNoDataInDB() {
            //GIVEN
            when(ruleNameRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> ruleNameService.findRuleNameById(TestConstants.EXISTING_RULE_NAME_ID));
            assertEquals(PoseidonExceptionsConstants.RULE_NAME_ID_NOT_VALID
                    + TestConstants.EXISTING_RULE_NAME_ID, exception.getMessage());

            verify(ruleNameRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("updateRuleName tests")
    class UpdateRuleNameTest {

        @Test
        @DisplayName("GIVEN a ruleName to update " +
                "WHEN updating this ruleName " +
                "THEN the returned value is the updated ruleName")
        void updateRuleNameTest_WithSuccess() {
            //GIVEN
            when(ruleNameRepositoryMock.save(any(RuleName.class))).thenReturn(ruleNameInDb);

            //WHEN
            RuleNameDTO createdRuleNameDTO = ruleNameService.updateRuleName(ruleNameDTOWithValues);

            //THEN
            assertEquals(ruleNameDTOWithValues.toString(), createdRuleNameDTO.toString());

            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .save(any(RuleName.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a ruleName " +
                "THEN an exception is thrown")
        void updateRuleNameTest_WithException() {
            //GIVEN
            when(ruleNameRepositoryMock.save(any(RuleName.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ruleNameService.updateRuleName(ruleNameDTOWithValues));

            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .save(any(RuleName.class));
        }
    }


    @Nested
    @DisplayName("deleteRuleName tests")
    class DeleteRuleNameTest {

        @Test
        @DisplayName("GIVEN a ruleName to delete " +
                "WHEN deleting this ruleName " +
                "THEN nothing is returned")
        void deleteRuleNameTest_WithSuccess() {
            //GIVEN
            when(ruleNameRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(ruleNameInDb));

            //WHEN
            ruleNameService.deleteRuleName(ruleNameInDb.getId());

            //THEN
            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .delete(any(RuleName.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a ruleName " +
                "THEN an exception is thrown")
        void deleteRuleNameTest_WithException() {
            //GIVEN
            when(ruleNameRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(ruleNameInDb));
            doThrow(new RuntimeException()).when(ruleNameRepositoryMock).delete(any(RuleName.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> ruleNameService.deleteRuleName(ruleNameInDb.getId()));

            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .delete(any(RuleName.class));
        }


        @Test
        @DisplayName("GIVEN no ruleName in DB for the specified id " +
                "WHEN deleting a ruleName " +
                "THEN an exception is thrown")
        void deleteRuleNameTest_WithNoDataInDb() {
            //GIVEN
            when(ruleNameRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> ruleNameService.deleteRuleName(ruleNameInDb.getId()));

            verify(ruleNameRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(ruleNameRepositoryMock, Mockito.times(0))
                    .delete(any(RuleName.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a ruleName " +
                "THEN an exception is thrown")
        void deleteRuleNameTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> ruleNameService.deleteRuleName(null));

            verify(ruleNameRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(ruleNameRepositoryMock, Mockito.times(0))
                    .delete(any(RuleName.class));
        }
    }
}
