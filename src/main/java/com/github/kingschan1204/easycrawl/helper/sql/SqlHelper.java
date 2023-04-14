package com.github.kingschan1204.easycrawl.helper.sql;

import com.alibaba.fastjson.JSONArray;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
public class SqlHelper {

    public static String createTable(String[] columns, String tableName) {
        String temp = "create table %s (%s)";
        String fields = " `%s` varchar(50) not null default '' ";
        StringBuffer field = new StringBuffer();
        for (int i = 0; i < columns.length; i++) {
            field.append(String.format(fields, columns[i]));
            if (i < columns.length - 1) {
                field.append(",");
            }
        }
        return String.format(temp, tableName, field.toString());
    }

    public static String insert(String[] columns, Object[] data, String tableName) {
        String temp = "insert into %s (%s) values (%s);";
        return String.format(temp, tableName,
                Arrays.stream(columns).collect(Collectors.joining(",")),
                Arrays.stream(data).map(v -> {
                    String val = String.valueOf(v);
                    //科学计数转换
                    if (val.matches(RegexHelper.REGEX_SCIENTIFIC_NOTATION)) {
                        return String.format("'%s'", null == v ? "" : new BigDecimal(val).toPlainString());
                    }
                    return String.format("'%s'", null == v ? "" : v);
                }).collect(Collectors.joining(","))
        );
    }

    public static String insertBatch(String[] columns, JSONArray data, String tableName) {
        String temp = "insert into %s (%s) values %s ;";
        StringBuffer values = new StringBuffer();
        for (int i = 0; i < data.size(); i++) {
            JSONArray row = data.getJSONArray(i);
            values.append(String.format("(%s)",
                    Arrays.stream(row.toArray(new Object[]{})).map(v -> String.format("'%s'", null == v ? "" : v)).collect(Collectors.joining(","))
            ));
            if (i < columns.length - 1) {
                values.append(",");
            }
        }
        return String.format(temp, tableName,
                Arrays.stream(columns).map(r -> String.format("`%s`", r)).collect(Collectors.joining(",")),
                values.toString()
        );
    }


    public static void main(String[] args) {
       /* String columns ="[\"timestamp\",\"volume\",\"open\",\"high\",\"low\",\"close\",\"chg\",\"percent\",\"turnoverrate\",\"amount\",\"volume_post\",\"amount_post\",\"pe\",\"pb\",\"ps\",\"pcf\",\"market_capital\",\"balance\",\"hold_volume_cn\",\"hold_ratio_cn\",\"net_volume_cn\",\"hold_volume_hk\",\"hold_ratio_hk\",\"net_volume_hk\"]\n";
        JSONArray js = JSONArray.parseArray(columns);
        String sql = SqlHelper.createTable(js.toArray(new String[]{}),"kline");
        System.out.println(sql);

        String datas="[\"2022-02-16\",32285305,39.0584,39.3508,38.5223,39.2728,0.3996,1.03,0.54,1293066616,null,null,29.6225,5.405,2.3316,16.6067,257861274686.22,3432664538.16,1000384954,16.78,1489254,null,null,null]\n";
        JSONArray jsons = JSONArray.parseArray(datas);
        System.out.println(SqlHelper.insert(js.toArray(new String[]{}),jsons.toArray(new Object[]{}),"kline"));*/
        System.out.println("8.9222348E+8".matches("\\d+\\.\\d+E(\\+)?\\d+"));
    }

}
