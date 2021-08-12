package com.nnk.springboot.services;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.contracts.IBidListService;
import com.nnk.springboot.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nnk.springboot.constants.PoseidonExceptionsConstants.BID_LIST_ID_NOT_VALID;

@Slf4j
@Service
@Transactional
public class BidListService implements IBidListService {

    private final BidListRepository bidListRepository;

    @Autowired
    BidListService(BidListRepository bidListRepository) {
        this.bidListRepository = bidListRepository;
    }

    /**
     * Create a bidList
     *
     * @param bidListDTOToCreate a bidList to create
     * @return the created bidList
     */
    @Override
    public Optional<BidListDTO> createBidList(BidListDTO bidListDTOToCreate) {

        log.debug(LogConstants.CREATE_BID_LIST_CALL + bidListDTOToCreate.toString());

        ModelMapper modelMapper = new ModelMapper();
        BidList bidListCreated;

        try {
            BidList bidListToCreate = modelMapper.map(bidListDTOToCreate, BidList.class);
            bidListToCreate.setCreationDate(DateUtil.getCurrentLocalDateTime());
            bidListToCreate.setCreationName(SecurityContextHolder.getContext().getAuthentication().getName());
            bidListCreated = bidListRepository.save(bidListToCreate);
            log.debug(LogConstants.CREATE_BID_LIST_OK + bidListCreated.getBidListId());

        } catch (Exception exception) {
            log.error(LogConstants.CREATE_BID_LIST_ERROR + bidListDTOToCreate);
            throw exception;
        }

        return Optional.ofNullable(modelMapper.map(bidListCreated, BidListDTO.class));
    }


    /**
     * Get all bidList
     *
     * @return the list of bidList
     */
    @Override
    public List<BidListDTO> findAllBidList() {
        log.debug(LogConstants.FIND_BID_LIST_ALL_CALL);

        List<BidList> bidListList = bidListRepository.findAll();
        List<BidListDTO> bidListDTOList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        bidListList.forEach(bidList ->
                bidListDTOList.add(modelMapper.map(bidList, BidListDTO.class)));
        log.debug(LogConstants.FIND_BID_LIST_ALL_OK, bidListDTOList.size());

        return bidListDTOList;
    }


    /**
     * Get a bitList by its id
     *
     * @param id of the bidList we want to retrieve
     * @return a BidListDTO filled with BidList informations
     * @throws IllegalArgumentException if no bidList found
     */
    @Override
    public BidListDTO findBidListById(Integer id) {
        log.debug(LogConstants.FIND_BID_LIST_BY_ID_CALL);

        Optional<BidList> bidList = bidListRepository.findById(id);

        if (bidList.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            BidListDTO bidListDTO = modelMapper.map(bidList.get(), BidListDTO.class);

            log.debug(LogConstants.FIND_BID_LIST_BY_ID_OK + id + "\n");
            return bidListDTO;
        } else {
            log.error(BID_LIST_ID_NOT_VALID + id);
            throw new IllegalArgumentException(BID_LIST_ID_NOT_VALID + id);
        }
    }


    /**
     * Update a bidList
     *
     * @param bidListDTOToUpdate a bidList to update
     * @return the created bidList
     */
    @Override
    public BidListDTO updateBidList(BidListDTO bidListDTOToUpdate) {
        log.debug(LogConstants.UPDATE_BID_LIST_CALL + bidListDTOToUpdate.toString());

        ModelMapper modelMapper = new ModelMapper();
        BidList bidListUpdated;

        try {
            BidList bidListToUpdate = modelMapper.map(bidListDTOToUpdate, BidList.class);
            bidListToUpdate.setRevisionDate(DateUtil.getCurrentLocalDateTime());
            bidListToUpdate.setRevisionName(SecurityContextHolder.getContext().getAuthentication().getName());
            bidListUpdated = bidListRepository.save(bidListToUpdate);
            log.debug(LogConstants.UPDATE_BID_LIST_OK + bidListUpdated.getBidListId());

        } catch (Exception exception) {
            log.error(LogConstants.UPDATE_BID_LIST_ERROR + bidListDTOToUpdate);
            throw exception;
        }

        return modelMapper.map(bidListUpdated, BidListDTO.class);
    }


    /**
     * delete a bidList
     *
     * @param id of the bidList to delete
     */
    @Override
    public void deleteBidList(Integer id) {

        log.debug(LogConstants.DELETE_BID_LIST_CALL + id);

        if (id == null) {
            log.error(LogConstants.DELETE_BID_LIST_ERROR + "id is null");
            throw new IllegalArgumentException(BID_LIST_ID_NOT_VALID + "null");
        }

        //Find bidList by Id
        BidList bidList = bidListRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(BID_LIST_ID_NOT_VALID + id);
                    return new IllegalArgumentException(BID_LIST_ID_NOT_VALID + id);
                });

        //Delete the bidList
        try {
            bidListRepository.delete(bidList);
            log.debug(LogConstants.DELETE_BID_LIST_OK + id);

        } catch (Exception exception) {
            log.error(LogConstants.DELETE_BID_LIST_ERROR + id);
            throw exception;
        }
    }
}
