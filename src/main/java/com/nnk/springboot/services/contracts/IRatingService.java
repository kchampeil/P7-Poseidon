package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.RatingDTO;

import java.util.List;
import java.util.Optional;

public interface IRatingService {
    Optional<RatingDTO> create(RatingDTO ratingDTOToCreate);

    List<RatingDTO> findAll();

    RatingDTO findById(Integer id);

    RatingDTO update(RatingDTO ratingDTOToUpdate);

    void delete(Integer id);
}
