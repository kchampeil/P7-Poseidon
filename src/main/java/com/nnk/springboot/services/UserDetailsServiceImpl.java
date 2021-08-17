package com.nnk.springboot.services;

import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Poseidon customisation of UserDetailsService to authenticate user in Poseidon database
 */
@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * load user data from his username
     *
     * @param username declare by user to access to the application
     * @return UserDetails filled with found user data information
     * @throws UsernameNotFoundException if no user found for this username
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info(LogConstants.USER_LOAD_CALL + username);

        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.error(PoseidonExceptionsConstants.DOES_NOT_EXISTS_USER + " for: " + username);
                    return new UsernameNotFoundException(PoseidonExceptionsConstants.DOES_NOT_EXISTS_USER);
                });
    }
}
