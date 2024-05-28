package com.github.kingschan1204.excel.dto;

import lombok.Data;

@Data
public class Quote {
    private String symbol;
    private String name;
    private Double pettm;
    private Double pelyr;
    private Double pb;
    private Double dividendYield;
    private Double dividend;
}
