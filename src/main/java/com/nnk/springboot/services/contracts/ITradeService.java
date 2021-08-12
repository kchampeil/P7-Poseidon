package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.TradeDTO;

import java.util.List;
import java.util.Optional;

public interface ITradeService {
    Optional<TradeDTO> createTrade(TradeDTO bankAccountDTOToCreate);

    List<TradeDTO> findAllTrade();

    TradeDTO findTradeById(Integer id);

    TradeDTO updateTrade(TradeDTO tradeDTOToUpdate);

    void deleteTrade(Integer id);
}
