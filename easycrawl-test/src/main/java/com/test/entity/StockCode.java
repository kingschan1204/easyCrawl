package com.test.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "xq_stock_code")
public class StockCode {

  //  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(
      name = "symbol",
      length = 10,
      nullable = false,
      columnDefinition = "varchar(10) COMMENT '股票代码'")
  private String symbol;

  @Column(name = "pct", precision = 7, scale = 2, columnDefinition = "decimal(7,2) COMMENT '涨幅百分比'")
  private BigDecimal pct;

  @Column(name = "volume", columnDefinition = "bigint COMMENT '本日股票成交量'")
  private Long volume;

  @Column(
      name = "current",
      precision = 10,
      scale = 2,
      columnDefinition = "decimal(10,2) COMMENT '当前股价'")
  private BigDecimal current;

  @Column(name = "mc", columnDefinition = "bigint COMMENT '总市值'")
  private Long mc;

  @Column(name = "name", length = 50, columnDefinition = "varchar(50) COMMENT '公司名称'")
  private String name;

  @Column(name = "exchange", length = 10, columnDefinition = "varchar(10) COMMENT '股票所属交易所'")
  private String exchange;

  @Column(name = "type", columnDefinition = "int COMMENT '股票类型'")
  private Integer type;

  @Column(name = "areacode", length = 10, columnDefinition = "varchar(10) COMMENT '地区代码'")
  private String areacode;

  @Column(
      name = "tick_size",
      precision = 3,
      scale = 2,
      columnDefinition = "decimal(3,2) COMMENT '最小价格变动单位，股价变动最小刻度'")
  private BigDecimal tickSize;

  @Column(name = "has_follow", columnDefinition = "tinyint(1) COMMENT '是否被关注的标识'")
  private Boolean hasFollow;

  @Column(name = "indcode", length = 20, columnDefinition = "varchar(20) COMMENT '行业代码'")
  private String indcode;
}
