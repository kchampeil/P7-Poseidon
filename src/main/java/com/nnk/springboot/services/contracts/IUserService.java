package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.UserDTO;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Optional<UserDTO> createUser(UserDTO bankAccountDTOToCreate) throws Exception;

    List<UserDTO> findAllUser();

    UserDTO findUserById(Integer id);

    UserDTO updateUser(UserDTO userDTOToUpdate) throws Exception;

    void deleteUser(Integer id);
}
