package com.nnk.springboot.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class BidListDTO {
    private Integer bidListId;

    @NotBlank(message = "{bidList.account.notBlank}")
    @Size(max = 30, message = "{bidList.account.size}")
    private String account;

    @NotBlank(message = "{bidList.type.notBlank}")
    @Size(max = 30, message = "Size should not exceed 30 characters")
    private String type;

    @NotNull(message = "Bid quantity is mandatory")
    private Double bidQuantity;

    @Override
    public String toString() {
        return "BidListDTO{" +
                "bidListId=" + bidListId +
                ", account='" + account + '\'' +
                ", type='" + type + '\'' +
                ", bidQuantity=" + bidQuantity +
                '}';
    }
}
