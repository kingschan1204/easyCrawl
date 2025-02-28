package com.github.kingschan1204.easycrawl.sql;

import com.github.kingschan1204.easycrawl.helper.validation.Assert;
import jakarta.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * @author kingschan 2025-02-23
 */
public class TableMaping<T> {
  final DbType dbType;
  final Class<T> entityClass;
  @Getter final String tableName;
  //
  final Table table;
  // properties -> table columns
  @Getter final LinkedHashMap<String, Column> columns;
  // properties -> table primary keys
  @Getter final LinkedHashMap<String, GeneratedValue> primaryKeys;
  final SqlBuilder sqlBuilder;

  public List<String> getInsertColumns() {
    List<String> insertColumns = new ArrayList<>();
    for (Map.Entry<String, Column> column : columns.entrySet()) {
      // if primary key
      if (primaryKeys.containsKey(column.getKey())) {
        // No primary key strategy
        if (null == primaryKeys.get(column.getKey())) {
          insertColumns.add(column.getKey());
          continue;
        }
        /* if (primaryKeys.get(column.getKey()).strategy().equals(GenerationType.AUTO)) {
          // auto increment
          continue;
        }*/
      } else if (column.getValue().insertable()) {
        insertColumns.add(column.getKey());
      }
    }
    return insertColumns;
  }

  public List<String> getUpdateColumns() {
    List<String> updateColumns = new ArrayList<>();
    for (Map.Entry<String, Column> column : columns.entrySet()) {
      // if primary key
      if (!primaryKeys.containsKey(column.getKey()) && column.getValue().updatable()) {
        updateColumns.add(column.getKey());
      }
    }
    return updateColumns;
  }

  public TableMaping(Class<T> entityClass, DbType dbType) {
    this.entityClass = entityClass;
    this.table = entityClass.getAnnotation(Table.class);
    Assert.notNull(table, "entityClass must be annotated with @Table");
    this.tableName = table.name();
    this.dbType = dbType;
    // init
    this.columns = new LinkedHashMap<>();
    this.primaryKeys = new LinkedHashMap<>();
    // ref fields
    Field[] fields = entityClass.getDeclaredFields();
    for (Field field : fields) {
      Column column = field.getAnnotation(Column.class);
      if (column != null) {
        columns.put(field.getName(), column);
      }
      Id id = field.getAnnotation(Id.class);
      if (id != null) {
        GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
        primaryKeys.put(field.getName(), generatedValue);
      }
    }
    this.sqlBuilder = dbType.equals(DbType.MYSQL) ? new MysqlSqlBuilder(this) : null;
  }

  public String insertSql() {
    return sqlBuilder.insert();
  }

  public String updateByPrimary() {
    return sqlBuilder.updateByPrimary();
  }

  public String deleteByPrimary() {
    return sqlBuilder.deleteByPrimary();
  }

  public String selectByPrimary() {
    return sqlBuilder.selectByPrimary();
  }

  /**
   * Generate upsert statements based on object attributes and annotations
   *
   * @return
   */
  public String upsertSql() {
    return sqlBuilder.upsert();
  }
}
