package com.nnk.springboot.DTO;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class CurvePointDTO {
    private Integer id;

    @NotNull(message = "{curvePoint.curveId.notNull}")
    private Integer curveId;

    private Double term;

    private Double value;

    @Override
    public String toString() {
        return "CurvePointDTO{" +
                "id=" + id +
                ", curveId=" + curveId +
                ", term=" + term +
                ", value=" + value +
                '}';
    }
}
