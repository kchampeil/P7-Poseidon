package com.nnk.springboot.services.contracts;

import com.nnk.springboot.DTO.RatingDTO;

import java.util.List;
import java.util.Optional;

public interface IRatingService {
    Optional<RatingDTO> createRating(RatingDTO ratingDTOToCreate);

    List<RatingDTO> findAllRating();

    RatingDTO findRatingById(Integer id);

    RatingDTO updateRating(RatingDTO ratingDTOToUpdate);

    void deleteRating(Integer id);
}
