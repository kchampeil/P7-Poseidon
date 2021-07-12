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

    @NotBlank(message = "Account is mandatory")
    @Size(max = 30, message = "Size should not exceed 30 characters")
    private String account;

    @NotBlank(message = "Type is mandatory")
    @Size(max = 30, message = "Size should not exceed 30 characters")
    private String type;

    @NotNull(message = "Bid quantity is mandatory")
    private Double bidQuantity;

    /**
     * concatenates the bid list informations to help showing them in logs
     *
     * @return bid list informations
     */
    public String toStringForLogs() {
        return "bidListId: " + getBidListId() + " // account: " + getAccount()
                + " // type: " + getType() + " // bidQuantity: " + getBidQuantity();
    }
}
