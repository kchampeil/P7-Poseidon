package com.nnk.springboot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


/**
 * Trade Entity allows to register a Trade
 */
@Getter
@Setter
@Entity
@Table(name = "trade")
public class Trade {
    // TODO: Map columns in data table TRADE with corresponding java fields

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "tradeId", nullable = false)
    private Integer tradeId;

    @NotBlank(message = "Account is mandatory")
    @Size(max = 30)
    @Column(name = "account", length = 30)
    private String account;

    @NotBlank(message = "Type is mandatory")
    @Size(max = 30)
    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "buyQuantity")
    private Double buyQuantity;

    @Column(name = "sellQuantity")
    private Double sellQuantity;

    @Column(name = "buyPrice")
    private Double buyPrice;

    @Column(name = "sellPrice")
    private Double sellPrice;

    @Column(name = "tradeDate")
    private LocalDateTime tradeDate;

    @Column(name = "security", length = 125)
    private String security;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "trader", length = 125)
    private String trader;

    @Column(name = "benchmark", length = 125)
    private String benchmark;

    @Column(name = "book", length = 125)
    private String book;

    @Column(name = "creationName", length = 125)
    private String creationName;

    @Column(name = "creationDate")
    private LocalDateTime creationDate;

    @Column(name = "revisionName", length = 125)
    private String revisionName;

    @Column(name = "revisionDate")
    private LocalDateTime revisionDate;

    @Column(name = "dealName", length = 125)
    private String dealName;

    @Column(name = "dealType", length = 125)
    private String dealType;

    @Column(name = "sourceListId", length = 125)
    private String sourceListId;

    @Column(name = "side", length = 125)
    private String side;
}
