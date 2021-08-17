package com.nnk.springboot.services;

import com.nnk.springboot.DTO.TradeDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.domain.Trade;
import com.nnk.springboot.repositories.TradeRepository;
import com.nnk.springboot.services.contracts.ITradeService;
import com.nnk.springboot.utils.DateUtil;
import com.nnk.springboot.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nnk.springboot.constants.PoseidonExceptionsConstants.TRADE_ID_NOT_VALID;

@Slf4j
@Service
@Transactional
public class TradeService implements ITradeService {

    private final TradeRepository tradeRepository;

    @Autowired
    TradeService(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    /**
     * Create a trade
     *
     * @param tradeDTOToCreate a trade to create
     * @return the created trade
     */
    @Override
    public Optional<TradeDTO> createTrade(TradeDTO tradeDTOToCreate) {

        log.debug(LogConstants.CREATE_TRADE_CALL + tradeDTOToCreate.toString());

        ModelMapper modelMapper = new ModelMapper();
        Trade tradeCreated;

        try {
            Trade tradeToCreate = modelMapper.map(tradeDTOToCreate, Trade.class);
            tradeToCreate.setCreationDate(DateUtil.getCurrentLocalDateTime());
            tradeToCreate.setCreationName(UserUtil.getCurrentUser());
            tradeCreated = tradeRepository.save(tradeToCreate);
            log.debug(LogConstants.CREATE_TRADE_OK + tradeCreated.getTradeId());

        } catch (Exception exception) {
            log.error(LogConstants.CREATE_TRADE_ERROR + tradeDTOToCreate);
            throw exception;
        }

        return Optional.ofNullable(modelMapper.map(tradeCreated, TradeDTO.class));
    }


    /**
     * Get all trade
     *
     * @return the list of trade
     */
    @Override
    public List<TradeDTO> findAllTrade() {
        log.debug(LogConstants.FIND_TRADE_ALL_CALL);

        List<Trade> tradeList = tradeRepository.findAll();
        List<TradeDTO> tradeDTOList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        tradeList.forEach(trade ->
                tradeDTOList.add(modelMapper.map(trade, TradeDTO.class)));
        log.debug(LogConstants.FIND_TRADE_ALL_OK, tradeDTOList.size());

        return tradeDTOList;
    }


    /**
     * Get a bitList by its id
     *
     * @param id of the trade we want to retrieve
     * @return a TradeDTO filled with Trade informations
     * @throws IllegalArgumentException if no trade found
     */
    @Override
    public TradeDTO findTradeById(Integer id) {
        log.debug(LogConstants.FIND_TRADE_BY_ID_CALL);

        Optional<Trade> trade = tradeRepository.findById(id);

        if (trade.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            TradeDTO tradeDTO = modelMapper.map(trade.get(), TradeDTO.class);

            log.debug(LogConstants.FIND_TRADE_BY_ID_OK + id + "\n");
            return tradeDTO;
        } else {
            log.error(TRADE_ID_NOT_VALID + id);
            throw new IllegalArgumentException(TRADE_ID_NOT_VALID + id);
        }
    }


    /**
     * Update a trade
     *
     * @param tradeDTOToUpdate a trade to update
     * @return the created trade
     */
    @Override
    public TradeDTO updateTrade(TradeDTO tradeDTOToUpdate) {
        log.debug(LogConstants.UPDATE_TRADE_CALL + tradeDTOToUpdate.toString());

        ModelMapper modelMapper = new ModelMapper();
        Trade tradeUpdated;

        try {
            Trade tradeToUpdate = modelMapper.map(tradeDTOToUpdate, Trade.class);
            tradeToUpdate.setRevisionDate(DateUtil.getCurrentLocalDateTime());
            tradeToUpdate.setRevisionName(UserUtil.getCurrentUser());
            tradeUpdated = tradeRepository.save(tradeToUpdate);
            log.debug(LogConstants.UPDATE_TRADE_OK + tradeUpdated.getTradeId());

        } catch (Exception exception) {
            log.error(LogConstants.UPDATE_TRADE_ERROR + tradeDTOToUpdate);
            throw exception;
        }

        return modelMapper.map(tradeUpdated, TradeDTO.class);
    }


    /**
     * delete a trade
     *
     * @param id of the trade to delete
     */
    @Override
    public void deleteTrade(Integer id) {

        log.debug(LogConstants.DELETE_TRADE_CALL + id);

        if (id == null) {
            log.error(LogConstants.DELETE_TRADE_ERROR + "id is null");
            throw new IllegalArgumentException(TRADE_ID_NOT_VALID + "null");
        }

        //Find trade by Id
        Trade trade = tradeRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(TRADE_ID_NOT_VALID + id);
                    return new IllegalArgumentException(TRADE_ID_NOT_VALID + id);
                });

        //Delete the trade
        try {
            tradeRepository.delete(trade);
            log.debug(LogConstants.DELETE_TRADE_OK + id);

        } catch (Exception exception) {
            log.error(LogConstants.DELETE_TRADE_ERROR + id);
            throw exception;
        }
    }
}
