package com.github.kingschan1204.easycrawl.sql;

import java.util.List;

public interface IJdbcOperaction {
  <T> long insert(T bean);

  <T> long insert(List<T> beans);

  <T> long update(T bean);

  <T> long update(List<T> beans);

  <T> long upsert(T bean);

  <T> long upsert(List<T> beans);

  <T> T get(T bean, Object... args);

  <T> List<T> Query(T bean, Object... args);
}
