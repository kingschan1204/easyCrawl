package com.github.kingschan1204.easycrawl.sql;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class TableMaping<T> {
  final Class<T> entityClass;
  final String tableName;
}
