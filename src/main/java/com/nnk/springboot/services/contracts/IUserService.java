package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.UserDTO;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    Optional<UserDTO> create(UserDTO bankAccountDTOToCreate) throws Exception;

    List<UserDTO> findAll();

    UserDTO findById(Integer id);

    UserDTO update(UserDTO userDTOToUpdate) throws Exception;

    void delete(Integer id);
}
