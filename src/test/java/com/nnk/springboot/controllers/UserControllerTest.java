package com.nnk.springboot.controllers;

import com.nnk.springboot.DTO.UserDTO;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.services.contracts.IUserService;
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

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUserService userServiceMock;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceMock;

    @MockBean
    private PasswordEncoder passwordEncoderMock;

    private static UserDTO userDTO;

    @BeforeAll
    static void setUp() {
        userDTO = new UserDTO();
        userDTO.setId(TestConstants.NEW_USER_ID);
        userDTO.setUsername(TestConstants.NEW_USER_USERNAME);
        userDTO.setPassword(TestConstants.NEW_USER_PASSWORD);
        userDTO.setFullname(TestConstants.NEW_USER_FULLNAME);
        userDTO.setRole(TestConstants.USER_ROLE_USER);
    }

    @Nested
    @DisplayName("home tests")
    class HomeTest {

        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("WHEN asking for the user list page while logged in as ADMIN " +
                "THEN return status is ok and the expected view is the user list page")
        void homeTest_LoggedInAsAdmin() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/list"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("users"))
                    .andExpect(view().name("user/list"));

            verify(userServiceMock, Mockito.times(1))
                    .findAllUser();
        }


        @WithMockUser(username = "user", roles = "USER")
        @Test
        @DisplayName("WHEN asking for the user list page while logged in as USER " +
                "THEN return status is Forbidden (403)")
        void homeTest_LoggedInAsUser() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/list"))
                    .andExpect(status().isForbidden());

            verify(userServiceMock, Mockito.times(0))
                    .findAllUser();
        }


        @Test
        @DisplayName("WHEN asking for the user list page while not logged in " +
                "THEN return status is Found (302) and the expected view is the login page")
        void homeTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/list"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userDetailsServiceMock, Mockito.times(0))
                    .loadUserByUsername(anyString());
            verify(userServiceMock, Mockito.times(0))
                    .findAllUser();
        }
    }

    @Nested
    @DisplayName("addUserForm tests")
    class AddUserFormTest {

        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("WHEN processing a GET /user/add request while logged in as admin " +
                "THEN return status is ok " +
                "AND the expected view is the user add form initialized")
        void addUserFormTest_WithSuccess_LoggedInAsAdmin() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/add"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(view().name("user/add"));
        }


        @WithMockUser(username = "user", roles = "USER")
        @Test
        @DisplayName("WHEN processing a GET /user/add request while logged in as user " +
                "THEN return status is Forbidden (403)")
        void addUserFormTest_WithSuccess_LoggedInAsUser() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/add"))
                    .andExpect(status().isForbidden());
        }


        @Test
        @DisplayName("WHEN processing a GET /user/add request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is login page")
        void addUserFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/add"))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));
        }
    }


    @Nested
    @DisplayName("validate tests")
    class ValidateTest {

        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a new user to add " +
                "WHEN processing a POST /user/validate request for this user " +
                "THEN return status is found (302) " +
                "AND the expected view is the user list page with user list updated")
        void validateTest_WithSuccess() throws Exception {
            //GIVEN
            when(userServiceMock.createUser(any(UserDTO.class)))
                    .thenReturn(Optional.of(userDTO));

            //WHEN-THEN
            mockMvc.perform(post("/user/validate")
                    .param("username", userDTO.getUsername())
                    .param("password", userDTO.getPassword())
                    .param("fullname", userDTO.getFullname())
                    .param("role", userDTO.getRole())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/user/list"));

            verify(userServiceMock, Mockito.times(1))
                    .createUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a new user to add with missing fullname" +
                "WHEN processing a POST /user/validate request for this user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/add page filled with entered user")
        void validateTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/user/validate")
                    .param("username", TestConstants.NEW_USER_USERNAME)
                    .param("password", TestConstants.NEW_USER_PASSWORD)
                    .param("fullname", "")
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("user", "fullname", "NotBlank"))
                    .andExpect(view().name("user/add"));

            verify(userServiceMock, Mockito.times(0))
                    .createUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a new user to add with invalid fullname (too long) " +
                "WHEN processing a POST /user/validate request for this user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/add page filled with entered user")
        void validateTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/user/validate")
                    .param("username", TestConstants.NEW_USER_USERNAME)
                    .param("password", TestConstants.NEW_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME_WITH_TOO_LONG_SIZE)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("user", "fullname", "Size"))
                    .andExpect(view().name("user/add"));

            verify(userServiceMock, Mockito.times(0))
                    .createUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN an exception when saving the new user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/add page filled with entered user")
        void validateTest_WithException() throws Exception {
            //GIVEN
            when(userServiceMock.createUser(any(UserDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/user/validate")
                    .param("username", TestConstants.NEW_USER_USERNAME)
                    .param("password", TestConstants.NEW_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("user/add"));

            verify(userServiceMock, Mockito.times(1))
                    .createUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN no returned value when saving the new user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/add page filled with entered user")
        void validateTest_WithNoReturnedUserAfterSaving() throws Exception {
            //GIVEN
            when(userServiceMock.createUser(any(UserDTO.class)))
                    .thenReturn(Optional.empty());

            //WHEN-THEN
            mockMvc.perform(post("/user/validate")
                    .param("username", TestConstants.NEW_USER_USERNAME)
                    .param("password", TestConstants.NEW_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("user/add"));

            verify(userServiceMock, Mockito.times(1))
                    .createUser(any(UserDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /user/validate request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void validateTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/user/validate")
                    .param("username", TestConstants.NEW_USER_USERNAME)
                    .param("password", TestConstants.NEW_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userServiceMock, Mockito.times(0))
                    .createUser(any(UserDTO.class));
        }
    }


    @Nested
    @DisplayName("showUpdateForm tests")
    class ShowUpdateFormTest {

        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("WHEN processing a GET /user/update/{id} request while logged in " +
                "THEN return status is ok " +
                "AND the expected view is the user update form initialized")
        void showUpdateFormTest_WithSuccess_LoggedIn() throws Exception {
            //GIVEN
            when(userServiceMock.findUserById(anyInt()))
                    .thenReturn(userDTO);

            //WHEN-THEN
            mockMvc.perform(get("/user/update/{id}", anyInt()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(view().name("user/update"));

            verify(userServiceMock, Mockito.times(1))
                    .findUserById(anyInt());
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("WHEN an exception occurs while retrieving user on a GET /user/update/{id} request " +
                "THEN return status is found (302) " +
                "AND the expected view is the user list page")
        void showUpdateFormTest_WithException() throws Exception {
            //GIVEN
            when(userServiceMock.findUserById(TestConstants.UNKNOWN_USER_ID))
                    .thenThrow(new IllegalArgumentException());

            //WHEN-THEN
            mockMvc.perform(get("/user/update/{id}", TestConstants.UNKNOWN_USER_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/user/list"));

            verify(userServiceMock, Mockito.times(1))
                    .findUserById(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /user/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void showUpdateFormTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/update/{id}", TestConstants.EXISTING_USER_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userServiceMock, Mockito.times(0))
                    .findUserById(anyInt());
        }
    }

    @Nested
    @DisplayName("updateUser tests")
    class UpdateUserTest {

        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a user to update " +
                "WHEN processing a POST /user/update/{id} request for this user " +
                "THEN return status is found (302) " +
                "AND the expected view is the user list page with user list updated")
        void updateUserTest_WithSuccess() throws Exception {
            //GIVEN
            when(userServiceMock.updateUser(any(UserDTO.class)))
                    .thenReturn(userDTO);

            //WHEN-THEN
            mockMvc.perform(post("/user/update/{id}", anyInt())
                    .param("username", userDTO.getUsername())
                    .param("password", userDTO.getPassword())
                    .param("fullname", userDTO.getFullname())
                    .param("role", userDTO.getRole())
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/user/list"));

            verify(userServiceMock, Mockito.times(1))
                    .updateUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a user to update with missing fullname " +
                "WHEN processing a POST /user/update/{id} request for this user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/update page filled with entered user")
        void updateUserTest_WithMissingInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/user/update/{id}", TestConstants.EXISTING_USER_ID)
                    .param("username", TestConstants.EXISTING_USER_USERNAME)
                    .param("password", TestConstants.EXISTING_USER_PASSWORD)
                    .param("fullname", "")
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("user", "fullname", "NotBlank"))
                    .andExpect(view().name("user/update"));

            verify(userServiceMock, Mockito.times(0))
                    .updateUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a user to update with invalid fullname (too long) " +
                "WHEN processing a POST /user/update/{id} request for this user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/update/{id} page filled with entered user")
        void updateBidTest_WithInvalidInformation() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/user/update/{id}", TestConstants.EXISTING_USER_ID)
                    .param("username", TestConstants.EXISTING_USER_USERNAME)
                    .param("password", TestConstants.EXISTING_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME_WITH_TOO_LONG_SIZE)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeExists("user"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrorCode("user", "fullname", "Size"))
                    .andExpect(view().name("user/update"));

            verify(userServiceMock, Mockito.times(0))
                    .updateUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN an exception when updating the user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/update/{id} page filled with entered user")
        void updateUserTest_WithException() throws Exception {
            //GIVEN
            when(userServiceMock.updateUser(any(UserDTO.class))).thenThrow(new RuntimeException());

            //WHEN-THEN
            mockMvc.perform(post("/user/update/{id}", TestConstants.EXISTING_USER_ID)
                    .param("username", TestConstants.EXISTING_USER_USERNAME)
                    .param("password", TestConstants.EXISTING_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("user/update"));

            verify(userServiceMock, Mockito.times(1))
                    .updateUser(any(UserDTO.class));
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN no returned value when updating the new user " +
                "THEN the returned code is ok " +
                "AND the expected view is the user/update page filled with entered user")
        void updateUserTest_WithNoReturnedUserAfterSaving() throws Exception {
            //GIVEN
            when(userServiceMock.updateUser(any(UserDTO.class)))
                    .thenReturn(null);

            //WHEN-THEN
            mockMvc.perform(post("/user/update/{id}", TestConstants.EXISTING_USER_ID)
                    .param("username", TestConstants.EXISTING_USER_USERNAME)
                    .param("password", TestConstants.EXISTING_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(model().hasNoErrors())
                    .andExpect(view().name("user/update"));

            verify(userServiceMock, Mockito.times(1))
                    .updateUser(any(UserDTO.class));
        }


        @Test
        @DisplayName("WHEN processing a POST /user/update/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void updateUserTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(post("/user/update/{id}", TestConstants.EXISTING_USER_ID)
                    .param("username", TestConstants.EXISTING_USER_USERNAME)
                    .param("password", TestConstants.EXISTING_USER_PASSWORD)
                    .param("fullname", TestConstants.NEW_USER_FULLNAME)
                    .param("role", TestConstants.USER_ROLE_USER)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userServiceMock, Mockito.times(0))
                    .updateUser(any(UserDTO.class));
        }
    }

    @Nested
    @DisplayName("deleteUser tests")
    class DeleteUserTest {

        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a user to delete " +
                "WHEN processing a GET /user/delete/{id} request for this user " +
                "THEN return status is found (302) " +
                "AND the expected view is the user list page with user list updated")
        void deleteUserTest_WithSuccess() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/delete/{id}", TestConstants.EXISTING_USER_ID)
                    .with(csrf()))
                    .andExpect(model().hasNoErrors())
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/user/list"));

            verify(userServiceMock, Mockito.times(1))
                    .deleteUser(anyInt());
        }


        @WithMockUser(username = "admin", roles = "ADMIN")
        @Test
        @DisplayName("GIVEN a unknown user to delete " +
                "WHEN processing a GET /user/delete/{id} request for this user " +
                "THEN the returned code is found " +
                "AND the expected view is the user/list page")
        void deleteUserTest_WithMissingInformation() throws Exception {
            //GIVEN
            doThrow(new IllegalArgumentException()).when(userServiceMock).deleteUser(anyInt());

            //WHEN-THEN
            mockMvc.perform(get("/user/delete/{id}", TestConstants.UNKNOWN_USER_ID)
                    .with(csrf()))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/user/list"));

            verify(userServiceMock, Mockito.times(1))
                    .deleteUser(anyInt());
        }


        @Test
        @DisplayName("WHEN processing a GET /user/delete/{id} request while not logged in " +
                "THEN return status is found " +
                "AND the expected view is the login page")
        void deleteUserTest_NotLoggedIn() throws Exception {
            //WHEN-THEN
            mockMvc.perform(get("/user/delete/{id}", TestConstants.EXISTING_USER_ID))
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrlPattern("**/login"));

            verify(userServiceMock, Mockito.times(0))
                    .findUserById(anyInt());
        }
    }
}
