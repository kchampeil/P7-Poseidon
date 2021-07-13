package com.nnk.springboot.controllers;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.UserDetailsServiceImpl;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest {

    private static User userInDb;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserDetailsServiceImpl userDetailsServiceMock;

    @MockBean
    private PasswordEncoder passwordEncoderMock;

    @BeforeAll
    private static void setUp() {
        userInDb = new User();
        userInDb.setId(TestConstants.EXISTING_USER_ID);
        userInDb.setUsername(TestConstants.EXISTING_USER_USERNAME);
        userInDb.setPassword(TestConstants.EXISTING_USER_PASSWORD);
        userInDb.setFullname(TestConstants.EXISTING_USER_FULLNAME);
        userInDb.setRole(TestConstants.EXISTING_USER_ROLE_USER);
    }

    //TODO à revoir car normalement non connecté passe ==> à exclure du controllerAdvice ?
    @WithMockUser
    @Test
    @DisplayName("WHEN asking for the home page while logged in as user " +
            "THEN return status is ok and the expected view is the homepage")
    void homeTest_LoggedIn() throws Exception {
        //GIVEN
        when(userDetailsServiceMock.getUserInfoByUsername(anyString())).thenReturn(userInDb);

        //THEN
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"));
    }


    @WithMockUser
    @Test
    @DisplayName("WHEN asking for the /admin/home page while logged in " +
            "THEN return status is found (302) and the expected view is the homepage")
    void adminHomeTest_LoggedIn() throws Exception {
        //GIVEN
        when(userDetailsServiceMock.getUserInfoByUsername(anyString())).thenReturn(userInDb);

        //THEN
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/bidList/list"));
    }


    @Test
    @DisplayName("WHEN asking for the /admin/home page while logged in " +
            "THEN return status is found (302) and the expected view is the login page")
    void adminHomeTest_NotLoggedIn() throws Exception {
        //THEN
        mockMvc.perform(get("/admin/home"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}