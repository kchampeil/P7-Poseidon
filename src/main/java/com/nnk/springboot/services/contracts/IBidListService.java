package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.BidListDTO;

import java.util.List;
import java.util.Optional;

public interface IBidListService {
    Optional<BidListDTO> create(BidListDTO bankAccountDTOToCreate);

    List<BidListDTO> findAll();

    BidListDTO findById(Integer id);

    BidListDTO update(BidListDTO bidListDTOToUpdate);

    void delete(Integer id);
}
