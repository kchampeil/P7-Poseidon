package com.nnk.springboot.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class TradeDTO {

    private Integer tradeId;

    @NotBlank(message = "{trade.account.notBlank}")
    @Size(max = 30, message = "{trade.account.size}")
    private String account;

    @NotBlank(message = "{trade.type.notBlank}")
    @Size(max = 30, message = "{trade.type.size}")
    private String type;

    @NotNull(message = "{trade.buyQuantity.notNull}")
    private Double buyQuantity;

    @Override
    public String toString() {
        return "TradeDTO{" +
                "tradeId=" + tradeId +
                ", account='" + account + '\'' +
                ", type='" + type + '\'' +
                ", buyQuantity=" + buyQuantity +
                '}';
    }
}
