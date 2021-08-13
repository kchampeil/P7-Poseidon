package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.RuleNameDTO;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.services.contracts.IRuleNameService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = RuleNameController.class)
class RuleNameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRuleNameService ruleNameServiceMock;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceMock;

    @MockBean
    private PasswordEncoder passwordEncoderMock;

    private static RuleNameDTO ruleNameDTO;

    @BeforeAll
    static void setUp() {
        ruleNameDTO = new RuleNameDTO();
        ruleNameDTO.setId(TestConstants.NEW_RULE_NAME_ID);
        ruleNameDTO.setName(TestConstants.NEW_RULE_NAME_NAME);
        ruleNameDTO.setDescription(TestConstants.NEW_RULE_NAME_DESCRIPTION);
        ruleNameDTO.setJson(TestConstants.NEW_RULE_NAME_JSON);
        ruleNameDTO.setTemplate(TestConstants.NEW_RULE_NAME_TEMPLATE);
        ruleNameDTO.setSqlStr(TestConstants.NEW_RULE_NAME_SQLSTR);
        ruleNameDTO.setSqlPart(TestConstants.NEW_RULE_NAME_SQLPART);
    }

    @Nested
    @DisplayName("home tests")
    class HomeTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN asking for the ruleName list page while logged in " +
                " THEN return status is ok and the expected view is the ruleName list page")
        void homeTest_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/ruleName/list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ruleNameAll"))
                    .andExpect(view().name("ruleName/list"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .findAllRuleName();
        }


        @Test
        @DisplayName("WHEN asking for the ruleName list page while not logged in " +
                " THEN return status is Found (302) and the expected view is the login page")
        void homeTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/ruleName/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userDetailsServiceMock, Mockito.times(0))
                    .loadUserByUsername(anyString());
            verify(ruleNameServiceMock, Mockito.times(0))
                    .findAllRuleName();
        }
    }

    @Nested
    @DisplayName("addRuleNameForm tests")
    class AddRuleNameFormTest {
        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /ruleName/add request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the ruleName add form initialized")
        void addRuleNameFormTest_WithSuccess_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/ruleName/add"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ruleName"))
                    .andExpect(view().name("ruleName/add"));
        }

        @Test
        @DisplayName("WHEN processing a GET /ruleName/add request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is login page")
        void addRuleNameFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/ruleName/add"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }


    @Nested
    @DisplayName("validate tests")
    class ValidateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a new ruleName to add " +
                "WHEN processing a POST /ruleName/validate request for this ruleName " +
                "THEN return status is found (302) " +
                "AND the expected view is the ruleName list page with ruleName list updated")
        void validateTest_WithSuccess() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.createRuleName(any(RuleNameDTO.class)))
                    .thenReturn(Optional.of(ruleNameDTO));

            //WHEN-THEN
            mockMvc.perform(post("/ruleName/validate")
                    .param("name", ruleNameDTO.getName())
                    .param("description", ruleNameDTO.getDescription())
                    .param("json", ruleNameDTO.getJson())
                    .param("template", ruleNameDTO.getTemplate())
                    .param("sqlStr", ruleNameDTO.getSqlStr())
                    .param("sqlPart", ruleNameDTO.getSqlPart())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/ruleName/list"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .createRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new ruleName to add with missing name" +
                "WHEN processing a POST /ruleName/validate request for this ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/add page filled with entered ruleName")
        void validateTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/ruleName/validate")
                    .param("name", "")
                    .param("description", TestConstants.NEW_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.NEW_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.NEW_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.NEW_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ruleName"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("ruleName", "name", "NotBlank"))
                    .andExpect(view().name("ruleName/add"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .createRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new ruleName to add with invalid description (too long) " +
                "WHEN processing a POST /ruleName/validate request for this ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/add page filled with entered ruleName")
        void validateTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/ruleName/validate")
                    .param("name", TestConstants.NEW_RULE_NAME_NAME)
                    .param("description", TestConstants.NEW_RULE_NAME_DESCRIPTION_WITH_TOO_LONG_SIZE)
                    .param("json", TestConstants.NEW_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.NEW_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.NEW_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ruleName"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("ruleName", "description", "Size"))
                    .andExpect(view().name("ruleName/add"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .updateRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when saving the new ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/add page filled with entered ruleName")
        void validateTest_WithException() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.createRuleName(any(RuleNameDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/ruleName/validate")
                    .param("name", TestConstants.NEW_RULE_NAME_NAME)
                    .param("description", TestConstants.NEW_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.NEW_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.NEW_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.NEW_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("ruleName/add"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .createRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when saving the new ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/add page filled with entered ruleName")
        void validateTest_WithNoReturnedRuleNameAfterSaving() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.createRuleName(any(RuleNameDTO.class)))
                    .thenReturn(Optional.empty());

            //WHEN-THEN
            mockMvc.perform(post("/ruleName/validate")
                    .param("name", TestConstants.NEW_RULE_NAME_NAME)
                    .param("description", TestConstants.NEW_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.NEW_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.NEW_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.NEW_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("ruleName/add"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .createRuleName(any(RuleNameDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /ruleName/validate request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void validateTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/ruleName/validate")
                    .param("name", TestConstants.NEW_RULE_NAME_NAME)
                    .param("description", TestConstants.NEW_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.NEW_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.NEW_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.NEW_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .createRuleName(any(RuleNameDTO.class));
        }
    }


    @Nested
    @DisplayName("showUpdateForm tests")
    class ShowUpdateFormTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /ruleName/update/{id} request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the ruleName update form initialized")
        void showUpdateFormTest_WithSuccess_LoggedIn() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.findRuleNameById(anyInt()))
                    .thenReturn(ruleNameDTO);

            //WHEN-THEN
            mockMvc.perform(get("/ruleName/update/{id}", anyInt()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ruleName"))
                    .andExpect(view().name("ruleName/update"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .findRuleNameById(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("WHEN an exception occurs while retrieving ruleName on a GET /ruleName/update/{id} request " +
                "THEN return status is found (302) " +
                "AND the expected view is the ruleName list page")
        void showUpdateFormTest_WithException() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.findRuleNameById(TestConstants.UNKNOWN_RULE_NAME_ID))
                    .thenThrow(new IllegalArgumentException());

            //WHEN-THEN
            mockMvc.perform(get("/ruleName/update/{id}", TestConstants.UNKNOWN_RULE_NAME_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/ruleName/list"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .findRuleNameById(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /ruleName/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void showUpdateFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/ruleName/update/{id}", TestConstants.EXISTING_RULE_NAME_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .findRuleNameById(anyInt());
        }
    }

    @Nested
    @DisplayName("updateRuleName tests")
    class UpdateRuleNameTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a ruleName to update " +
                "WHEN processing a POST /ruleName/update/{id} request for this ruleName " +
                "THEN return status is found (302) " +
                "AND the expected view is the ruleName list page with ruleName list updated")
        void updateRuleNameTest_WithSuccess() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.updateRuleName(any(RuleNameDTO.class)))
                    .thenReturn(ruleNameDTO);

            //WHEN-THEN
            mockMvc.perform(post("/ruleName/update/{id}", anyInt())
                    .param("name", ruleNameDTO.getName())
                    .param("description", ruleNameDTO.getDescription())
                    .param("json", ruleNameDTO.getJson())
                    .param("template", ruleNameDTO.getTemplate())
                    .param("sqlStr", ruleNameDTO.getSqlStr())
                    .param("sqlPart", ruleNameDTO.getSqlPart())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/ruleName/list"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .updateRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a ruleName to update with missing name " +
                "WHEN processing a POST /ruleName/update/{id} request for this ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/update page filled with entered ruleName")
        void updateRuleNameTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/ruleName/update/{id}", TestConstants.EXISTING_RULE_NAME_ID)
                    .param("name", "")
                    .param("description", TestConstants.EXISTING_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.EXISTING_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.EXISTING_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.EXISTING_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ruleName"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("ruleName", "name", "NotBlank"))
                    .andExpect(view().name("ruleName/update"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .updateRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a ruleName to update with invalid description (too long) " +
                "WHEN processing a POST /ruleName/update/{id} request for this ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/update/{id} page filled with entered ruleName")
        void updateBidTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/ruleName/update/{id}", TestConstants.EXISTING_RULE_NAME_ID)
                    .param("name", TestConstants.EXISTING_RULE_NAME_NAME)
                    .param("description", TestConstants.NEW_RULE_NAME_DESCRIPTION_WITH_TOO_LONG_SIZE)
                    .param("json", TestConstants.EXISTING_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.EXISTING_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.EXISTING_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("ruleName"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("ruleName", "description", "Size"))
                    .andExpect(view().name("ruleName/update"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .updateRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when updating the ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/update/{id} page filled with entered ruleName")
        void updateRuleNameTest_WithException() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.updateRuleName(any(RuleNameDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/ruleName/update/{id}", TestConstants.EXISTING_RULE_NAME_ID)
                    .param("name", TestConstants.EXISTING_RULE_NAME_NAME)
                    .param("description", TestConstants.EXISTING_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.EXISTING_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.EXISTING_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.EXISTING_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("ruleName/update"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .updateRuleName(any(RuleNameDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when updating the new ruleName " +
                "THEN the returned code is ok " +
                "AND the expected view is the ruleName/update page filled with entered ruleName")
        void updateRuleNameTest_WithNoReturnedRuleNameAfterSaving() throws Exception {
            //GIVEN
            when(ruleNameServiceMock.updateRuleName(any(RuleNameDTO.class)))
                    .thenReturn(null);

            //WHEN-THEN
            mockMvc.perform(post("/ruleName/update/{id}", TestConstants.EXISTING_RULE_NAME_ID)
                    .param("name", TestConstants.EXISTING_RULE_NAME_NAME)
                    .param("description", TestConstants.EXISTING_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.EXISTING_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.EXISTING_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.EXISTING_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("ruleName/update"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .updateRuleName(any(RuleNameDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /ruleName/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void updateRuleNameTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/ruleName/update/{id}", TestConstants.EXISTING_RULE_NAME_ID)
                    .param("name", TestConstants.EXISTING_RULE_NAME_NAME)
                    .param("description", TestConstants.EXISTING_RULE_NAME_DESCRIPTION)
                    .param("json", TestConstants.EXISTING_RULE_NAME_JSON)
                    .param("template", TestConstants.NEW_RULE_NAME_TEMPLATE)
                    .param("sqlStr", TestConstants.EXISTING_RULE_NAME_SQLSTR)
                    .param("sqlPart", TestConstants.EXISTING_RULE_NAME_SQLPART)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .updateRuleName(any(RuleNameDTO.class));
        }
    }

    @Nested
    @DisplayName("deleteRuleName tests")
    class DeleteRuleNameTest {
        @WithMockUser
        @Test
        @DisplayName("GIVEN a ruleName to delete " +
                "WHEN processing a GET /ruleName/delete/{id} request for this ruleName " +
                "THEN return status is found (302) " +
                "AND the expected view is the ruleName list page with ruleName list updated")
        void deleteRuleNameTest_WithSuccess() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/ruleName/delete/{id}", TestConstants.EXISTING_RULE_NAME_ID)
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/ruleName/list"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .deleteRuleName(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a unknown ruleName to delete " +
                "WHEN processing a GET /ruleName/delete/{id} request for this ruleName " +
                "THEN the returned code is found " +
                "AND the expected view is the ruleName/list page")
        void deleteRuleNameTest_WithMissingInformation() throws Exception {
            //GIVEN
            doThrow(new IllegalArgumentException()).when(ruleNameServiceMock).deleteRuleName(anyInt());

            //WHEN-THEN
            mockMvc.perform(get("/ruleName/delete/{id}", TestConstants.UNKNOWN_RULE_NAME_ID)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/ruleName/list"));

            verify(ruleNameServiceMock, Mockito.times(1))
                    .deleteRuleName(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /ruleName/delete/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void deleteRuleNameTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/ruleName/delete/{id}", TestConstants.EXISTING_RULE_NAME_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(ruleNameServiceMock, Mockito.times(0))
                    .findRuleNameById(anyInt());
        }
    }
}
