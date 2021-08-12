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

    /* TOASK : pas demandés dans la liste => comme bid list ?
    private Double sellQuantity;

    private Double buyPrice;

    private Double sellPrice;

    private LocalDateTime tradeDate;

    @Size(max = 125, message = "{trade.security.size}")
    private String security;

    @Size(max = 10, message = "{trade.status.size}")
    private String status;

    @Size(max = 125, message = "{trade.trader.size}")
    private String trader;

    @Size(max = 125, message = "{trade.benchmark.size}")
    private String benchmark;

    @Size(max = 125, message = "{trade.book.size}")
    private String book;

    @Size(max = 125, message = "{trade.dealName.size}")
    private String dealName;

    @Size(max = 125, message = "{trade.dealType.size}")
    private String dealType;

    @Size(max = 125, message = "{trade.sourceListId.size}")
    private String sourceListId;

    @Size(max = 125, message = "{trade.side.size}")
    private String side;

     */

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
