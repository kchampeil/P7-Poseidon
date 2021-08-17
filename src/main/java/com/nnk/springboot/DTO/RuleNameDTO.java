package com.nnk.springboot.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
public class RuleNameDTO {

    private Integer id;

    @NotBlank(message = "{ruleName.name.notBlank}")
    @Size(max = 125, message = "{ruleName.name.size}")
    private String name;

    @NotBlank(message = "{ruleName.description.notBlank}")
    @Size(max = 125, message = "{ruleName.description.size}")
    private String description;

    @Size(max = 125, message = "{ruleName.json.size}")
    private String json;

    @Size(max = 512, message = "{ruleName.template.size}")
    private String template;

    @Size(max = 125, message = "{ruleName.sqlStr.size}")
    private String sqlStr;

    @Size(max = 125, message = "{ruleName.sqlPart.size}")
    private String sqlPart;

    @Override
    public String toString() {
        return "RuleNameDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", json='" + json + '\'' +
                ", template='" + template + '\'' +
                ", sqlStr='" + sqlStr + '\'' +
                ", sqlPart='" + sqlPart + '\'' +
                '}';
    }
}
