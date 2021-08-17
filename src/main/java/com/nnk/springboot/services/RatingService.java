package com.nnk.springboot.services;

import com.nnk.springboot.DTO.RatingDTO;
import com.nnk.springboot.constants.LogConstants;
import com.nnk.springboot.domain.Rating;
import com.nnk.springboot.repositories.RatingRepository;
import com.nnk.springboot.services.contracts.IRatingService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.nnk.springboot.constants.PoseidonExceptionsConstants.RATING_ID_NOT_VALID;

@Slf4j
@Service
@Transactional
public class RatingService implements IRatingService {
    private final RatingRepository ratingRepository;

    @Autowired
    RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /**
     * Create a rating
     *
     * @param ratingDTOToCreate a rating to create
     * @return the created rating
     */
    @Override
    public Optional<RatingDTO> create(RatingDTO ratingDTOToCreate) {

        log.debug(LogConstants.CREATE_RATING_CALL + ratingDTOToCreate.toString());

        ModelMapper modelMapper = new ModelMapper();
        Rating ratingCreated;

        try {
            ratingCreated = ratingRepository.save(modelMapper.map(ratingDTOToCreate, Rating.class));
            log.debug(LogConstants.CREATE_RATING_OK + ratingCreated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.CREATE_RATING_ERROR + ratingDTOToCreate);
            throw exception;
        }

        return Optional.ofNullable(modelMapper.map(ratingCreated, RatingDTO.class));
    }


    /**
     * Get all rating
     *
     * @return the list of rating
     */
    @Override
    public List<RatingDTO> findAll() {
        log.debug(LogConstants.FIND_RATING_ALL_CALL);

        List<Rating> ratingList = ratingRepository.findAll();
        List<RatingDTO> ratingDTOList = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();
        ratingList.forEach(rating ->
                ratingDTOList.add(modelMapper.map(rating, RatingDTO.class)));
        log.debug(LogConstants.FIND_RATING_ALL_OK, ratingDTOList.size());

        return ratingDTOList;
    }


    /**
     * Get a rating by its id
     *
     * @param id of the rating we want to retrieve
     * @return a RatingDTO filled with Rating informations
     * @throws IllegalArgumentException if no rating found
     */
    @Override
    public RatingDTO findById(Integer id) {
        log.debug(LogConstants.FIND_RATING_BY_ID_CALL);

        Optional<Rating> rating = ratingRepository.findById(id);

        if (rating.isPresent()) {
            ModelMapper modelMapper = new ModelMapper();
            RatingDTO ratingDTO = modelMapper.map(rating.get(), RatingDTO.class);

            log.debug(LogConstants.FIND_RATING_BY_ID_OK + id + "\n");
            return ratingDTO;
        } else {
            log.error(RATING_ID_NOT_VALID + id);
            throw new IllegalArgumentException(RATING_ID_NOT_VALID + id);
        }
    }


    /**
     * Update a rating
     *
     * @param ratingDTOToUpdate a rating to update
     * @return the created rating
     */
    @Override
    public RatingDTO update(RatingDTO ratingDTOToUpdate) {
        log.debug(LogConstants.UPDATE_RATING_CALL + ratingDTOToUpdate.toString());

        ModelMapper modelMapper = new ModelMapper();
        Rating ratingUpdated;

        try {
            ratingUpdated = ratingRepository.save(modelMapper.map(ratingDTOToUpdate, Rating.class));
            log.debug(LogConstants.UPDATE_RATING_OK + ratingUpdated.getId());

        } catch (Exception exception) {
            log.error(LogConstants.UPDATE_RATING_ERROR + ratingDTOToUpdate);
            throw exception;
        }

        return modelMapper.map(ratingUpdated, RatingDTO.class);
    }


    /**
     * delete a rating
     *
     * @param id of the rating to delete
     */
    @Override
    public void delete(Integer id) {

        log.debug(LogConstants.DELETE_RATING_CALL + id);

        if (id == null) {
            log.error(LogConstants.DELETE_RATING_ERROR + "id is null");
            throw new IllegalArgumentException(RATING_ID_NOT_VALID + "null");
        }

        //Find rating by Id
        Rating rating = ratingRepository
                .findById(id)
                .orElseThrow(() -> {
                    log.error(RATING_ID_NOT_VALID + id);
                    return new IllegalArgumentException(RATING_ID_NOT_VALID + id);
                });

        //Delete the rating
        try {
            ratingRepository.delete(rating);
            log.debug(LogConstants.DELETE_RATING_OK + id);

        } catch (Exception exception) {
            log.error(LogConstants.DELETE_RATING_ERROR + id);
            throw exception;
        }
    }
}
