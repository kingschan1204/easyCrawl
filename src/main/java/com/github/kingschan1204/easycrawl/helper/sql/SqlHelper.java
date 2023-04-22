package com.github.kingschan1204.easycrawl.helper.sql;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author kingschan
 */
@Slf4j
public class SqlHelper {

    public static final Map<String, String> fieldType;

    static {
        fieldType = new HashMap<>();
        fieldType.put("String", "varchar(50)");
        fieldType.put("Integer", "int");
        fieldType.put("Long", "bigint");
        fieldType.put("Boolean", "bit");
        fieldType.put("BigDecimal", "decimal(22,4)");
    }

    public static String createTable(JSONObject json, String tableName) {
        List<String> fields = new ArrayList<>();
        for (String key : json.keySet()) {
            String type = Optional.ofNullable(json.get(key)).map(r -> json.get(key).getClass().getSimpleName()).orElse("String");
            fields.add(String.format(" `%s` %s", key, fieldType.get(type)));
        }
        return String.format("create table %s (%s)", tableName, String.join(",", fields));
    }

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

    public static String insert(Object[] columns, Object[] data, String tableName) {
        String temp = "insert into %s (%s) values (%s);";
        return String.format(temp, tableName,
                Arrays.stream(columns).map(String::valueOf).collect(Collectors.joining(",")),
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
        Assert.isTrue(data.get(0) instanceof JSONArray, "数据格式不匹配！");
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




}
