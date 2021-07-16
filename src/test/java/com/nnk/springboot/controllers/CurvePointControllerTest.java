package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.CurvePointDTO;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.services.contracts.ICurvePointService;
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

@WebMvcTest(controllers = CurveController.class)
class CurveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ICurvePointService curvePointServiceMock;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceMock;

    @MockBean
    private PasswordEncoder passwordEncoderMock;

    private static CurvePointDTO curvePointDTO;

    @BeforeAll
    static void setUp() {
        curvePointDTO = new CurvePointDTO();
        curvePointDTO.setId(TestConstants.NEW_CURVE_POINT_ID);
        curvePointDTO.setCurveId(TestConstants.NEW_CURVE_POINT_CURVE_ID);
        curvePointDTO.setTerm(TestConstants.NEW_CURVE_POINT_TERM);
        curvePointDTO.setValue(TestConstants.NEW_CURVE_POINT_VALUE);
    }

    @Nested
    @DisplayName("home tests")
    class HomeTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN asking for the curvePoint list page while logged in " +
                " THEN return status is ok and the expected view is the curvePoint list page")
        void homeTest_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("curvePointAll"))
                    .andExpect(view().name("curvePoint/list"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .findAllCurvePoint();
        }


        @Test
        @DisplayName("WHEN asking for the curvePoint list page while not logged in " +
                " THEN return status is Found (302) and the expected view is the login page")
        void homeTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userDetailsServiceMock, Mockito.times(0))
                    .loadUserByUsername(anyString());
            verify(curvePointServiceMock, Mockito.times(0))
                    .findAllCurvePoint();
        }
    }

    @Nested
    @DisplayName("addCurvePointForm tests")
    class AddCurvePointFormTest {
        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /curvePoint/add request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the curvePoint add form initialized")
        void addCurvePointFormTest_WithSuccess_LoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/add"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("curvePoint"))
                    .andExpect(view().name("curvePoint/add"));
        }

        @Test
        @DisplayName("WHEN processing a GET /curvePoint/add request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is login page")
        void addCurvePointFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/add"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }


    @Nested
    @DisplayName("validate tests")
    class ValidateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a new curvePoint to add " +
                "WHEN processing a POST /curvePoint/validate request for this curvePoint " +
                "THEN return status is found (302) " +
                "AND the expected view is the curvePoint list page with curvePoint list updated")
        void validateTest_WithSuccess() throws Exception {
            //GIVEN
            when(curvePointServiceMock.createCurvePoint(any(CurvePointDTO.class)))
                    .thenReturn(Optional.of(curvePointDTO));

            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/validate")
                    .param("curveId", curvePointDTO.getCurveId().toString())
                    .param("term", curvePointDTO.getTerm().toString())
                    .param("value", curvePointDTO.getValue().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/curvePoint/list"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .createCurvePoint(any(CurvePointDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a new curvePoint to add with missing account" +
                "WHEN processing a POST /curvePoint/validate request for this curvePoint " +
                "THEN the returned code is ok " +
                "AND the expected view is the curvePoint/add page filled with entered curvePoint")
        void validateTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/validate")
                    .param("curveId", "")
                    .param("term", TestConstants.NEW_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("curvePoint"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("curvePoint", "curveId", "NotNull"))
                    .andExpect(view().name("curvePoint/add"));

            verify(curvePointServiceMock, Mockito.times(0))
                    .createCurvePoint(any(CurvePointDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when saving the new curvePoint " +
                "THEN the returned code is ok " +
                "AND the expected view is the curvePoint/add page filled with entered curvePoint")
        void validateTest_WithException() throws Exception {
            //GIVEN
            when(curvePointServiceMock.createCurvePoint(any(CurvePointDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/validate")
                    .param("curveId", TestConstants.NEW_CURVE_POINT_CURVE_ID.toString())
                    .param("term", TestConstants.NEW_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("curvePoint/add"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .createCurvePoint(any(CurvePointDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when saving the new curvePoint " +
                "THEN the returned code is ok " +
                "AND the expected view is the curvePoint/add page filled with entered curvePoint")
        void validateTest_WithNoReturnedCurvePointAfterSaving() throws Exception {
            //GIVEN
            when(curvePointServiceMock.createCurvePoint(any(CurvePointDTO.class)))
                    .thenReturn(Optional.empty());

            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/validate")
                    .param("curveId", TestConstants.NEW_CURVE_POINT_CURVE_ID.toString())
                    .param("term", TestConstants.NEW_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("curvePoint/add"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .createCurvePoint(any(CurvePointDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /curvePoint/validate request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void validateTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/validate")
                    .param("curveId", TestConstants.NEW_CURVE_POINT_CURVE_ID.toString())
                    .param("term", TestConstants.NEW_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(curvePointServiceMock, Mockito.times(0))
                    .createCurvePoint(any(CurvePointDTO.class));
        }
    }


    @Nested
    @DisplayName("showUpdateForm tests")
    class ShowUpdateFormTest {

        @WithMockUser
        @Test
        @DisplayName("WHEN processing a GET /curvePoint/update/{id} request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the curvePoint update form initialized")
        void showUpdateFormTest_WithSuccess_LoggedIn() throws Exception {
            //GIVEN
            when(curvePointServiceMock.findCurvePointById(anyInt()))
                    .thenReturn(curvePointDTO);

            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/update/{id}", anyInt()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("curvePoint"))
                    .andExpect(view().name("curvePoint/update"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .findCurvePointById(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("WHEN an exception occurs while retrieving curvePoint on a GET /curvePoint/update/{id} request " +
                "THEN return status is found (302) " +
                "AND the expected view is the curvePoint list page")
        void showUpdateFormTest_WithException() throws Exception {
            //GIVEN
            when(curvePointServiceMock.findCurvePointById(TestConstants.UNKNOWN_CURVE_POINT_ID))
                    .thenThrow(new IllegalArgumentException());

            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/update/{id}", TestConstants.UNKNOWN_CURVE_POINT_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/curvePoint/list"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .findCurvePointById(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /curvePoint/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void showUpdateFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/update/{id}", TestConstants.EXISTING_CURVE_POINT_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(curvePointServiceMock, Mockito.times(0))
                    .findCurvePointById(anyInt());
        }
    }

    @Nested
    @DisplayName("updateCurvePoint tests")
    class UpdateCurvePointTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a curvePoint to update " +
                "WHEN processing a POST /curvePoint/update/{id} request for this curvePoint " +
                "THEN return status is found (302) " +
                "AND the expected view is the curvePoint list page with curvePoint list updated")
        void updateCurvePointTest_WithSuccess() throws Exception {
            //GIVEN
            when(curvePointServiceMock.updateCurvePoint(any(CurvePointDTO.class)))
                    .thenReturn(curvePointDTO);

            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/update/{id}", anyInt())
                    .param("curveId", curvePointDTO.getCurveId().toString())
                    .param("term", curvePointDTO.getTerm().toString())
                    .param("value", curvePointDTO.getValue().toString())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/curvePoint/list"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .updateCurvePoint(any(CurvePointDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a curvePoint to update with missing account " +
                "WHEN processing a POST /curvePoint/update/{id} request for this curvePoint " +
                "THEN the returned code is ok " +
                "AND the expected view is the curvePoint/update page filled with entered curvePoint")
        void updateCurvePointTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/update/{id}", TestConstants.EXISTING_CURVE_POINT_ID)
                    .param("curveId", "")
                    .param("term", TestConstants.EXISTING_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("curvePoint"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("curvePoint", "curveId", "NotNull"))
                    .andExpect(view().name("curvePoint/update"));

            verify(curvePointServiceMock, Mockito.times(0))
                    .updateCurvePoint(any(CurvePointDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception when updating the curvePoint " +
                "THEN the returned code is ok " +
                "AND the expected view is the curvePoint/update/{id} page filled with entered curvePoint")
        void updateCurvePointTest_WithException() throws Exception {
            //GIVEN
            when(curvePointServiceMock.updateCurvePoint(any(CurvePointDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/update/{id}", TestConstants.EXISTING_CURVE_POINT_ID)
                    .param("curveId", TestConstants.EXISTING_CURVE_POINT_CURVE_ID.toString())
                    .param("term", TestConstants.EXISTING_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("curvePoint/update"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .updateCurvePoint(any(CurvePointDTO.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN no returned value when updating the new curvePoint " +
                "THEN the returned code is ok " +
                "AND the expected view is the curvePoint/update page filled with entered curvePoint")
        void updateCurvePointTest_WithNoReturnedCurvePointAfterSaving() throws Exception {
            //GIVEN
            when(curvePointServiceMock.updateCurvePoint(any(CurvePointDTO.class)))
                    .thenReturn(null);

            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/update/{id}", TestConstants.EXISTING_CURVE_POINT_ID)
                    .param("curveId", TestConstants.EXISTING_CURVE_POINT_CURVE_ID.toString())
                    .param("term", TestConstants.EXISTING_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("curvePoint/update"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .updateCurvePoint(any(CurvePointDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /curvePoint/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void updateCurvePointTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/curvePoint/update/{id}", TestConstants.EXISTING_CURVE_POINT_ID)
                    .param("curveId", TestConstants.EXISTING_CURVE_POINT_CURVE_ID.toString())
                    .param("term", TestConstants.EXISTING_CURVE_POINT_TERM.toString())
                    .param("value", TestConstants.NEW_CURVE_POINT_VALUE.toString())
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(curvePointServiceMock, Mockito.times(0))
                    .updateCurvePoint(any(CurvePointDTO.class));
        }
    }

    @Nested
    @DisplayName("deleteCurvePoint tests")
    class DeleteCurvePointTest {
        @WithMockUser
        @Test
        @DisplayName("GIVEN a curvePoint to delete " +
                "WHEN processing a GET /curvePoint/delete/{id} request for this curvePoint " +
                "THEN return status is found (302) " +
                "AND the expected view is the curvePoint list page with curvePoint list updated")
        void deleteCurvePointTest_WithSuccess() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/delete/{id}", TestConstants.EXISTING_CURVE_POINT_ID)
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/curvePoint/list"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .deleteCurvePoint(anyInt());
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN a unknown curvePoint to delete " +
                "WHEN processing a GET /curvePoint/delete/{id} request for this curvePoint " +
                "THEN the returned code is found " +
                "AND the expected view is the curvePoint/list page")
        void deleteCurvePointTest_WithMissingInformation() throws Exception {
            //GIVEN
            doThrow(new IllegalArgumentException()).when(curvePointServiceMock).deleteCurvePoint(anyInt());

            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/delete/{id}", TestConstants.UNKNOWN_CURVE_POINT_ID)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/curvePoint/list"));

            verify(curvePointServiceMock, Mockito.times(1))
                    .deleteCurvePoint(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /curvePoint/delete/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void deleteCurvePointTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/curvePoint/delete/{id}", TestConstants.EXISTING_CURVE_POINT_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(curvePointServiceMock, Mockito.times(0))
                    .findCurvePointById(anyInt());
        }
    }
}
