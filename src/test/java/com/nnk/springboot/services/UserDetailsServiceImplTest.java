package com.nnk.springboot.services;

import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.testconstants.TestConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserDetailsServiceImplTest {

    @MockBean
    UserRepository userRepositoryMock;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    @Nested
    @DisplayName("loadUserByUsername tests")
    class LoadUserByUsernameTest {
        @Test
        @DisplayName("GIVEN a user in DB " +
                "WHEN getting this user information " +
                "THEN the returned value is the user information")
        void loadUserByUsernameTest_WithSuccess() {

            //GIVEN
            User userInDb = new User();
            userInDb.setId(TestConstants.EXISTING_USER_ID);
            userInDb.setUsername(TestConstants.EXISTING_USER_USERNAME);
            userInDb.setPassword(TestConstants.EXISTING_USER_PASSWORD);
            userInDb.setFullname(TestConstants.EXISTING_USER_FULLNAME);
            userInDb.setRole(TestConstants.EXISTING_USER_ROLE_USER);

            when(userRepositoryMock.findByUsernameIgnoreCase(TestConstants.EXISTING_USER_USERNAME))
                    .thenReturn(java.util.Optional.of(userInDb));

            //WHEN
            UserDetails userDetails = userDetailsService.loadUserByUsername(TestConstants.EXISTING_USER_USERNAME);

            //THEN
            assertNotNull(userDetails);
            assertEquals(userInDb.getUsername(), userDetails.getUsername());
            assertEquals(userInDb.getPassword(), userDetails.getPassword());
            assertTrue(userDetails.getAuthorities().contains(
                    new SimpleGrantedAuthority("ROLE_" + TestConstants.EXISTING_USER_ROLE_USER)));
            assertTrue(userDetails.isAccountNonExpired());
            assertTrue(userDetails.isAccountNonLocked());
            assertTrue(userDetails.isEnabled());
            assertTrue(userDetails.isCredentialsNonExpired());

            verify(userRepositoryMock, Mockito.times(1))
                    .findByUsernameIgnoreCase(TestConstants.EXISTING_USER_USERNAME);
        }


        @Test
        @DisplayName("GIVEN no user in DB for the asked username " +
                "WHEN getting this user information " +
                "THEN a UsernameNotFoundException exception is thrown")
        void loadUserByUsernameTest_WithUnknownUser() {

            //GIVEN
            when(userRepositoryMock.findByUsernameIgnoreCase(TestConstants.UNKNOWN_USERNAME))
                    .thenReturn(Optional.ofNullable(null));

            //THEN
            Exception exception = assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(TestConstants.UNKNOWN_USERNAME));
            assertEquals(PoseidonExceptionsConstants.DOES_NOT_EXISTS_USER, exception.getMessage());

            verify(userRepositoryMock, Mockito.times(1))
                    .findByUsernameIgnoreCase(TestConstants.UNKNOWN_USERNAME);
        }


        @Test
        @DisplayName("GIVEN a null username " +
                "WHEN getting this user information " +
                "THEN an UsernameNotFoundException is thrown")
        void loadUserByUsernameTest_WithMissingInformations() {
            //GIVEN
            when(userRepositoryMock.findByUsernameIgnoreCase(null))
                    .thenReturn(Optional.ofNullable(null));

            //THEN
            Exception exception = assertThrows(UsernameNotFoundException.class,
                    () -> userDetailsService.loadUserByUsername(null));
            assertEquals(PoseidonExceptionsConstants.DOES_NOT_EXISTS_USER, exception.getMessage());

            verify(userRepositoryMock, Mockito.times(1))
                    .findByUsernameIgnoreCase(null);
        }
    }
}