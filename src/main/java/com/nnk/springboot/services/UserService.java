package com.nnk.springboot.services;

import com.nnk.springboot.DTO.UserDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.constants.PoseidonExceptionsConstants;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.contracts.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nnk.springboot.constants.PoseidonExceptionsConstants.USER_ID_NOT_VALID;

@Slf4j
@Service
@Transactional
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Autowired
    UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Create a user
     *
     * @param userDTOToCreate a user to create
     * @return the created user
     */
    @Override
    public Optional<UserDTO> createUser(UserDTO userDTOToCreate) throws Exception {

        log.debug(LogConstants.CREATE_USER_CALL + userDTOToCreate.toString());

        if (userRepository.findByUsernameIgnoreCase(userDTOToCreate.getUsername()).isPresent()) {
            log.error(PoseidonExceptionsConstants.ALREADY_EXISTS_USER + " for: " + userDTOToCreate.getUsername());
            throw new Exception(PoseidonExceptionsConstants.ALREADY_EXISTS_USER);
        }

        ModelMapper modelMapper = new ModelMapper();
        User userCreated;

        try {
            User userToCreate = modelMapper.map(userDTOToCreate, User.class);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userToCreate.setPassword(encoder.encode(userToCreate.getPassword()));

            userCreated = userRepository.save(userToCreate);
            log.debug(LogConstants.CREATE_USER_OK + userCreated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.CREATE_USER_ERROR + userDTOToCreate);
            throw exception;
        }

        return Optional.ofNullable(modelMapper.map(userCreated, UserDTO.class));
    }


    /**
     * Get all user
     *
     * @return the list of user
     */
    @Override
    public List<UserDTO> findAllUser() {
        log.debug(LogConstants.FIND_USER_ALL_CALL);

        List<User> userList = userRepository.findAll();
        List<UserDTO> userDTOList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        userList.forEach(user ->
                userDTOList.add(modelMapper.map(user, UserDTO.class)));
        log.debug(LogConstants.FIND_USER_ALL_OK, userDTOList.size());

        return userDTOList;
    }


    /**
     * Get a bitList by its id
     *
     * @param id of the user we want to retrieve
     * @return a UserDTO filled with User informations
     * @throws IllegalArgumentException if no user found
     */
    @Override
    public UserDTO findUserById(Integer id) {
        log.debug(LogConstants.FIND_USER_BY_ID_CALL);

        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            UserDTO userDTO = modelMapper.map(user.get(), UserDTO.class);

            log.debug(LogConstants.FIND_USER_BY_ID_OK + id + "\n");
            return userDTO;
        } else {
            log.error(USER_ID_NOT_VALID + id);
            throw new IllegalArgumentException(USER_ID_NOT_VALID + id);
        }
    }


    /**
     * Update a user
     *
     * @param userDTOToUpdate a user to update
     * @return the created user
     */
    @Override
    public UserDTO updateUser(UserDTO userDTOToUpdate) throws Exception {
        log.debug(LogConstants.UPDATE_USER_CALL + userDTOToUpdate.toString());

        /* checks that no other user already have the same username */
        Optional<User> userInDb = userRepository.findByUsernameIgnoreCase(userDTOToUpdate.getUsername());
        if (userInDb.isPresent() && !userInDb.get().getId().equals(userDTOToUpdate.getId())) {
            log.error(PoseidonExceptionsConstants.ALREADY_EXISTS_USER + " for: " + userDTOToUpdate.getUsername());
            throw new Exception(PoseidonExceptionsConstants.ALREADY_EXISTS_USER);
        }

        ModelMapper modelMapper = new ModelMapper();
        User userUpdated;

        try {
            User userToUpdate = modelMapper.map(userDTOToUpdate, User.class);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            userToUpdate.setPassword(encoder.encode(userToUpdate.getPassword()));

            userUpdated = userRepository.save(userToUpdate);
            log.debug(LogConstants.UPDATE_USER_OK + userUpdated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.UPDATE_USER_ERROR + userDTOToUpdate);
            throw exception;
        }

        return modelMapper.map(userUpdated, UserDTO.class);
    }


    /**
     * delete a user
     *
     * @param id of the user to delete
     */
    @Override
    public void deleteUser(Integer id) {

        log.debug(LogConstants.DELETE_USER_CALL + id);

        if (id == null) {
            log.error(LogConstants.DELETE_USER_ERROR + "id is null");
            throw new IllegalArgumentException(USER_ID_NOT_VALID + "null");
        }

        //Find user by Id
        User user = userRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(USER_ID_NOT_VALID + id);
                    return new IllegalArgumentException(USER_ID_NOT_VALID + id);
                });

        //Delete the user
        try {
            userRepository.delete(user);
            log.debug(LogConstants.DELETE_USER_OK + id);

        } catch (Exception exception) {
            log.error(LogConstants.DELETE_USER_ERROR + id);
            throw exception;
        }
    }
}
