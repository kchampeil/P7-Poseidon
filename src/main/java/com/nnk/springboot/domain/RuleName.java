package com.nnk.springboot.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * RuleName Entity allows to register a Rule
 */
@Getter
@Setter
@Entity
@Table(name = "rulename")
public class RuleName {
    // DONE: Map columns in data table RULENAME with corresponding java fields
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "name", length = 125)
    private String name;

    @Column(name = "description", length = 125)
    private String description;

    @Column(name = "json", length = 125)
    private String json;

    @Column(name = "template", length = 512)
    private String template;

    @Column(name = "sqlStr", length = 125)
    private String sqlStr;

    @Column(name = "sqlPart", length = 125)
    private String sqlPart;
}
