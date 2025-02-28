package com.github.kingschan1204.easycrawl.sql;

public interface SqlBuilder {
  String insert();

  String updateByPrimary();

  String upsertByPrimary();

  String deleteByPrimary();

  String selectByPrimary();
}
