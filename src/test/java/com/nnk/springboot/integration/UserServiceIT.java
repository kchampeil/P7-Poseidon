package com.nnk.springboot.integration;

import com.nnk.springboot.DTO.UserDTO;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.contracts.IUserService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
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
public class UserServiceIT {
    @Autowired
    IUserService userService;

    @Autowired
    UserRepository userRepository;

    private User userInDb;
    private UserDTO userDTO;

    @BeforeEach
    private void initPerTest() {
        //init a user in DB for test
        userInDb = new User();
        userInDb.setUsername(TestConstants.EXISTING_USER_USERNAME);
        userInDb.setPassword(TestConstants.EXISTING_USER_PASSWORD);
        userInDb.setFullname(TestConstants.EXISTING_USER_FULLNAME);
        userInDb.setRole(TestConstants.USER_ROLE_USER);
        userInDb = userRepository.save(userInDb);

        //init common part of userDTO to create/update
        userDTO = new UserDTO();
        userDTO.setUsername(TestConstants.NEW_USER_USERNAME);
        userDTO.setPassword(TestConstants.EXISTING_USER_PASSWORD);
        userDTO.setFullname(TestConstants.EXISTING_USER_FULLNAME);
        userDTO.setRole(TestConstants.USER_ROLE_USER);
    }

    @AfterEach
    private void cleanPerTest(TestInfo testInfo) {
        if (testInfo.getTags().contains("SkipCleanUp")) {
            return;
        }
        //clean DB at the end of the test by deleting the user created at initialization
        userRepository.deleteById(userInDb.getId());
    }

    @WithMockUser
    @Test
    @DisplayName("WHEN creating a new user with correct informations  " +
            "THEN the returned value is the added user, " +
            "AND the user is added in DB")
    public void createUserIT_WithSuccess() throws Exception {

        //WHEN
        Optional<UserDTO> userDTOCreated = userService.createUser(userDTO);

        //THEN
        assertTrue(userDTOCreated.isPresent());
        assertNotNull(userDTOCreated.get().getId());
        assertEquals(userDTO.getUsername(), userDTOCreated.get().getUsername());

        //cleaning of DB at the end of the test by deleting the user created during the test
        userRepository.deleteById(userDTOCreated.get().getId());
    }


    @Test
    @DisplayName("WHEN asking for the list of all user " +
            "THEN the returned value is the list of all user in DB")
    public void findAllUserIT_WithSuccess() {

        //WHEN
        List<UserDTO> userDTOList = userService.findAllUser();

        //THEN
        assertThat(userDTOList.size()).isGreaterThan(0);
        assertEquals(userInDb.getId(), userDTOList.get(0).getId());
    }


    @Test
    @DisplayName("WHEN asking for a user with a specified id " +
            "THEN the returned value is the user in DB")
    public void findUserByIdIT_WithSuccess() {

        //WHEN
        UserDTO userDTO = userService.findUserById(userInDb.getId());

        //THEN
        assertEquals(userInDb.getId(), userDTO.getId());
        assertEquals(userInDb.getUsername(), userDTO.getUsername());
        assertEquals(userInDb.getPassword(), userDTO.getPassword());
        assertEquals(userInDb.getFullname(), userDTO.getFullname());
    }


    @WithMockUser
    @Test
    @DisplayName("WHEN updating a user with correct informations  " +
            "THEN the returned value is the updated user, " +
            "AND the user is updated in DB")
    public void updateUserIT_WithSuccess() throws Exception {

        //GIVEN
        userDTO.setId(userInDb.getId());
        userDTO.setUsername(TestConstants.NEW_USER_USERNAME);

        //WHEN
        UserDTO userDTOUpdated = userService.updateUser(userDTO);
        Optional<User> userUpdated = userRepository.findById(userInDb.getId());

        //THEN
        assertNotNull(userDTOUpdated);
        assertEquals(userDTO.getFullname(), userDTOUpdated.getFullname());

        assertTrue(userUpdated.isPresent());
        assertEquals(userDTO.getFullname(), userUpdated.get().getFullname());
    }


    @Test
    @Tag("SkipCleanUp")
    @DisplayName("WHEN deleting a user with correct informations  " +
            "THEN the user is deleted in DB")
    public void deleteUserIT_WithSuccess() {

        //WHEN
        userService.deleteUser(userInDb.getId());
        Optional<User> userDeleted = userRepository.findById(userInDb.getId());

        //THEN
        assertFalse(userDeleted.isPresent());
    }
}
