package com.github.kingschan1204.easycrawl.sql;

import java.util.List;
import java.util.Map;

public abstract class DBOperaction {
  List<String> basePackages;

  // class -> Mapper
  Map<Class<?>, TableMaping<?>> classMapping;

  // tableName -> Mapper
  Map<String, TableMaping<?>> tableMapping;
}
