package com.nnk.springboot.services;

import com.nnk.springboot.DTO.UserDTO;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.contracts.IUserService;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

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
public class UserServiceTest {

    @MockBean
    UserRepository userRepositoryMock;

    @Autowired
    IUserService userService;

    private static UserDTO userDTOWithValues;

    private static User userInDb;

    @BeforeAll
    static void setUp() {
        userDTOWithValues = new UserDTO();
        userDTOWithValues.setId(TestConstants.EXISTING_USER_ID);
        userDTOWithValues.setUsername(TestConstants.NEW_USER_USERNAME);
        userDTOWithValues.setPassword(TestConstants.NEW_USER_PASSWORD);
        userDTOWithValues.setFullname(TestConstants.NEW_USER_FULLNAME);
        userDTOWithValues.setRole(TestConstants.USER_ROLE_USER);

        userInDb = new User();
        userInDb.setId(userDTOWithValues.getId());
        userInDb.setUsername(userDTOWithValues.getUsername());
        userInDb.setPassword(userDTOWithValues.getPassword());
        userInDb.setFullname(userDTOWithValues.getFullname());
        userInDb.setRole(userDTOWithValues.getRole());
    }

    @Nested
    @DisplayName("CreateUser tests")
    class CreateUserTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a new user (DTO) to add " +
                "WHEN saving this new user " +
                "THEN the returned value is the added user (DTO)")
        void createUserTest_WithSuccess() throws Exception {
            //GIVEN
            when(userRepositoryMock.save(any(User.class))).thenReturn(userInDb);

            //WHEN
            Optional<UserDTO> createdUserDTO = userService.create(userDTOWithValues);

            //THEN
            assertTrue(createdUserDTO.isPresent());
            assertNotNull(createdUserDTO.get().getId());
            assertEquals(userDTOWithValues.toString(), createdUserDTO.get().toString());

            verify(userRepositoryMock, Mockito.times(1))
                    .save(any(User.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex user " +
                "THEN an exception is thrown")
        void createUserTest_WithException() {
            //GIVEN
            when(userRepositoryMock.save(any(User.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> userService.create(userDTOWithValues));

            verify(userRepositoryMock, Mockito.times(1))
                    .save(any(User.class));
        }
    }


    @Nested
    @DisplayName("findAllUser tests")
    class FindAllUserTest {

        @Test
        @DisplayName("GIVEN user in DB " +
                "WHEN getting all the user " +
                "THEN the returned value is the list of user")
        void findAllUserTest_WithDataInDB() {
            //GIVEN
            List<User> userList = new ArrayList<>();
            userList.add(userInDb);
            when(userRepositoryMock.findAll()).thenReturn(userList);

            //THEN
            List<UserDTO> userDTOList = userService.findAll();
            assertEquals(1, userDTOList.size());
            assertEquals(userInDb.getId(), userDTOList.get(0).getId());

            verify(userRepositoryMock, Mockito.times(1)).findAll();
        }

        @Test
        @DisplayName("GIVEN no user in DB " +
                "WHEN getting all the user " +
                "THEN the returned value is an empty list of user")
        void findAllUserTest_WithNoDataInDB() {
            //GIVEN
            List<User> userList = new ArrayList<>();
            when(userRepositoryMock.findAll()).thenReturn(userList);

            //THEN
            List<UserDTO> userDTOList = userService.findAll();
            assertThat(userDTOList).isEmpty();

            verify(userRepositoryMock, Mockito.times(1)).findAll();
        }
    }


    @Nested
    @DisplayName("findUserById tests")
    class FindUserByIdTest {

        @Test
        @DisplayName("GIVEN user in DB for a specified id" +
                "WHEN getting the user on id " +
                "THEN the returned value is the user")
        void findUserByIdTest_WithDataInDB() {
            //GIVEN
            when(userRepositoryMock.findById(anyInt())).thenReturn(Optional.of(userInDb));

            //THEN
            UserDTO userDTO = userService.findById(TestConstants.EXISTING_USER_ID);
            assertEquals(userInDb.getId(), userDTO.getId());
            assertEquals(userInDb.getUsername(), userDTO.getUsername());

            verify(userRepositoryMock, Mockito.times(1)).findById(anyInt());
        }

        @Test
        @DisplayName("GIVEN no user in DB for a specified id " +
                "WHEN getting all the user " +
                "THEN the returned value is a null user")
        void findUserByIdTest_WithNoDataInDB() {
            //GIVEN
            when(userRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            Exception exception = assertThrows(IllegalArgumentException.class,
                    () -> userService.findById(TestConstants.EXISTING_USER_ID));
            assertEquals(PoseidonExceptionsConstants.USER_ID_NOT_VALID
                    + TestConstants.EXISTING_USER_ID, exception.getMessage());

            verify(userRepositoryMock, Mockito.times(1)).findById(anyInt());
        }
    }


    @Nested
    @DisplayName("updateUser tests")
    class UpdateUserTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN a user to update " +
                "WHEN updating this user " +
                "THEN the returned value is the updated user")
        void updateUserTest_WithSuccess() throws Exception {
            //GIVEN
            when(userRepositoryMock.save(any(User.class))).thenReturn(userInDb);

            //WHEN
            UserDTO createdUserDTO = userService.update(userDTOWithValues);

            //THEN
            assertEquals(userDTOWithValues.toString(), createdUserDTO.toString());

            verify(userRepositoryMock, Mockito.times(1))
                    .save(any(User.class));
        }


        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a user " +
                "THEN an exception is thrown")
        void updateUserTest_WithException() {
            //GIVEN
            when(userRepositoryMock.save(any(User.class))).thenThrow(new RuntimeException());

            //THEN
            assertThrows(RuntimeException.class,
                    () -> userService.update(userDTOWithValues));

            verify(userRepositoryMock, Mockito.times(1))
                    .save(any(User.class));
        }
    }


    @Nested
    @DisplayName("deleteUser tests")
    class DeleteUserTest {

        @Test
        @DisplayName("GIVEN a user to delete " +
                "WHEN deleting this user " +
                "THEN nothing is returned")
        void deleteUserTest_WithSuccess() {
            //GIVEN
            when(userRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(userInDb));

            //WHEN
            userService.delete(userInDb.getId());

            //THEN
            verify(userRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(userRepositoryMock, Mockito.times(1))
                    .delete(any(User.class));
        }


        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a user " +
                "THEN an exception is thrown")
        void deleteUserTest_WithException() {
            //GIVEN
            when(userRepositoryMock.findById(anyInt())).thenReturn(Optional.ofNullable(userInDb));
            doThrow(new RuntimeException()).when(userRepositoryMock).delete(any(User.class));

            //THEN
            assertThrows(RuntimeException.class,
                    () -> userService.delete(userInDb.getId()));

            verify(userRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(userRepositoryMock, Mockito.times(1))
                    .delete(any(User.class));
        }


        @Test
        @DisplayName("GIVEN no user in DB for the specified id " +
                "WHEN deleting a user " +
                "THEN an exception is thrown")
        void deleteUserTest_WithNoDataInDb() {
            //GIVEN
            when(userRepositoryMock.findById(anyInt())).thenReturn(Optional.empty());

            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> userService.delete(userInDb.getId()));

            verify(userRepositoryMock, Mockito.times(1))
                    .findById(anyInt());
            verify(userRepositoryMock, Mockito.times(0))
                    .delete(any(User.class));
        }


        @Test
        @DisplayName("GIVEN no id is specified " +
                "WHEN asking for the deletion of a user " +
                "THEN an exception is thrown")
        void deleteUserTest_WithNoGivenId() {
            //THEN
            assertThrows(IllegalArgumentException.class,
                    () -> userService.delete(null));

            verify(userRepositoryMock, Mockito.times(0))
                    .findById(anyInt());
            verify(userRepositoryMock, Mockito.times(0))
                    .delete(any(User.class));
        }
    }
}
