package com.nnk.springboot.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RatingDTO {

    private Integer id;

    @NotBlank(message = "{rating.moodysRating.notBlank}")
    @Size(max = 125, message = "{rating.moodysRating.size}")
    private String moodysRating;

    @NotBlank(message = "{rating.sandPRating.notBlank}")
    @Size(max = 125, message = "{rating.sandPRating.size}")
    private String sandPRating;

    @NotBlank(message = "{rating.fitchRating.notBlank}")
    @Size(max = 125, message = "{rating.fitchRating.size}")
    private String fitchRating;

    private Integer orderNumber;

    @Override
    public String toString() {
        return "RatingDTO{" +
                "id=" + id +
                ", moodysRating='" + moodysRating + '\'' +
                ", sandPRating='" + sandPRating + '\'' +
                ", fitchRating='" + fitchRating + '\'' +
                ", orderNumber=" + orderNumber +
                '}';
    }
}
