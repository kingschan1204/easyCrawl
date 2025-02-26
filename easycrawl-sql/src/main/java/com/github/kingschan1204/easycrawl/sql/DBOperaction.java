package com.github.kingschan1204.easycrawl.sql;

import java.util.List;
import java.util.Map;

public abstract class DBOperaction {
  List<String> basePackages;

  // class -> Mapper
  Map<Class<?>, TableMaping<?>> classMapping;

  // tableName -> Mapper
  Map<String, TableMaping<?>> tableMapping;

  public abstract <T> long insert(T bean);

  public abstract <T> long insert(List<T> beans);

  public abstract <T> long update(T bean);

  public abstract <T> long update(List<T> beans);

  public abstract <T> long upsert(T bean);

  public abstract <T> long upsert(List<T> beans);

  public abstract <T> T get(T bean, Object... args);

  public abstract <T> List<T> Query(T bean, Object... args);
}
