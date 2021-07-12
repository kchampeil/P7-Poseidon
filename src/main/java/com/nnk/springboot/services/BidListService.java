package com.nnk.springboot.services;

import com.nnk.springboot.DTO.BidListDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.domain.BidList;
import com.nnk.springboot.repositories.BidListRepository;
import com.nnk.springboot.services.contracts.IBidListService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
     * @param bidListToCreate a bidList to create
     * @return the created bidList
     */
    @Override
    public Optional<BidListDTO> createBidList(BidListDTO bidListToCreate) {

        //TOASK passage par un DTO ?

        log.info(LogConstants.CREATE_BID_LIST_CALL + bidListToCreate.toStringForLogs());

        ModelMapper modelMapper = new ModelMapper();
        BidList bidListCreated;

        try {
            bidListCreated = bidListRepository.save(modelMapper.map(bidListToCreate, BidList.class));
            log.info(LogConstants.CREATE_BID_LIST_OK + bidListCreated.getBidListId());

        } catch (Exception exception) {
            log.error(LogConstants.CREATE_BID_LIST_ERROR + bidListToCreate.toStringForLogs());
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
        log.info(LogConstants.FIND_BID_LIST_CALL);

        List<BidList> bidListList = bidListRepository.findAll();
        List<BidListDTO> bidListDTOList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        bidListList.forEach(bidList ->
                bidListDTOList.add(modelMapper.map(bidList, BidListDTO.class)));
        log.info(LogConstants.FIND_BID_LIST_OK + bidListDTOList.size() + "\n");

        return bidListDTOList;
    }
}
