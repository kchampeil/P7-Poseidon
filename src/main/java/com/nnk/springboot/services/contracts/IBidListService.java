package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.BidListDTO;

import java.util.List;
import java.util.Optional;

public interface IBidListService {
    Optional<BidListDTO> createBidList(BidListDTO bankAccountDTOToCreate);

    List<BidListDTO> findAllBidList();
}
