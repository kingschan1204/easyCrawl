package com.github.kingschan1204.excel.dto;

import com.github.kingschan1204.easycrawl.helper.datetime.DateHelper;
import lombok.Data;

@Data
public class Gdrs {
    private String timestamp;
    private Integer holderNum;
    private Double chg;
    private Integer ashareHolder;

    public void setTimestamp(Long timestamp) {
        this.timestamp = DateHelper.of(timestamp).date();
    }
}
