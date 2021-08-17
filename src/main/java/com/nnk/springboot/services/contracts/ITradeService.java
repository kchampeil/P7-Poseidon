package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.TradeDTO;

import java.util.List;
import java.util.Optional;

public interface ITradeService {
    Optional<TradeDTO> create(TradeDTO bankAccountDTOToCreate);

    List<TradeDTO> findAll();

    TradeDTO findById(Integer id);

    TradeDTO update(TradeDTO tradeDTOToUpdate);

    void delete(Integer id);
}
