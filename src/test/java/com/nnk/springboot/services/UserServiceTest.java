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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @MockBean
    private UserRepository userRepositoryMock;

    @Autowired
    private IUserService userService;

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
    @DisplayName("create tests")
    class CreateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN saving a nex user " +
                "THEN an exception is thrown")
        void createTest_WithException() {
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
    @DisplayName("findAll tests")
    class FindAllTest {

        @Test
        @DisplayName("GIVEN no user in DB " +
                "WHEN getting all the user " +
                "THEN the returned value is an empty list of user")
        void findAllTest_WithNoDataInDB() {
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
    @DisplayName("findById tests")
    class FindByIdTest {

        @Test
        @DisplayName("GIVEN no user in DB for a specified id " +
                "WHEN getting all the user " +
                "THEN the returned value is a null user")
        void findByIdTest_WithNoDataInDB() {
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
    @DisplayName("update tests")
    class UpdateTest {

        @WithMockUser
        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN updating a user " +
                "THEN an exception is thrown")
        void updateTest_WithException() {
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
    @DisplayName("delete tests")
    class DeleteTest {

        @Test
        @DisplayName("GIVEN an exception " +
                "WHEN deleting a user " +
                "THEN an exception is thrown")
        void deleteTest_WithException() {
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
        void deleteTest_WithNoDataInDb() {
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
        void deleteTest_WithNoGivenId() {
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
