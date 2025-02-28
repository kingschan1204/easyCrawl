package com.github.kingschan1204.easycrawl.sql;

import java.util.stream.Collectors;

public class MysqlSqlBuilder implements SqlBuilder {
  final TableMaping tableMaping;

  public MysqlSqlBuilder(TableMaping tableMaping) {
    this.tableMaping = tableMaping;
  }

  @Override
  public String insert() {
    String columns =
        (String) tableMaping.getInsertColumns().stream().collect(Collectors.joining(","));
    String columnValue =
        (String)
            tableMaping.getInsertColumns().stream()
                .map(s -> String.format(":%s", s))
                .collect(Collectors.joining(","));
    return "insert into %s (%s) values (%s)"
        .formatted(tableMaping.getTableName(), columns, columnValue);
  }

  @Override
  public String updateByPrimary() {
    String columns =
        (String)
            tableMaping.getUpdateColumns().stream()
                .map(s -> String.format("%s = :%s", s, s))
                .collect(Collectors.joining(","));
    String primaryKey =
        (String)
            tableMaping.primaryKeys.keySet().stream()
                .map(s -> String.format("%s = :%s", s, s))
                .collect(Collectors.joining("and"));
    return "update %s set %s where %s".formatted(tableMaping.tableName, columns, primaryKey);
  }

  @Override
  public String upsert() {
    String columns =
        (String) tableMaping.getInsertColumns().stream().collect(Collectors.joining(","));
    String columnValue =
        (String)
            tableMaping.getInsertColumns().stream()
                .map(s -> String.format(":%s", s))
                .collect(Collectors.joining(","));
    String upsertColumns =
        (String)
            tableMaping.getUpdateColumns().stream()
                .map(s -> String.format("%s = values(%s)", s, s))
                .collect(Collectors.joining(","));
    return "insert into %s (%s) values (%s) on duplicate key update %s"
        .formatted(tableMaping.tableName, columns, columnValue, upsertColumns);
  }

  @Override
  public String deleteByPrimary() {
    String primaryKey =
        (String)
            tableMaping.primaryKeys.keySet().stream()
                .map(s -> String.format("%s = :%s", s, s))
                .collect(Collectors.joining("and"));
    return "delete from %s where %s ".formatted(tableMaping.getTableName(), primaryKey);
  }

  @Override
  public String selectByPrimary() {
    String primaryKey =
        (String)
            tableMaping.primaryKeys.keySet().stream()
                .map(s -> String.format("%s = :%s", s, s))
                .collect(Collectors.joining("and"));
    return "select * from %s where %s ".formatted(tableMaping.getTableName(), primaryKey);
  }
}
