package entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "stock")
@Data
public class StockCode {

  @Column(name = "pct")
  private Double pct;

  @Column(name = "volume")
  private Long volume;

  @Id
  @Column(name = "symbol")
  private String symbol;

  @Column(name = "current")
  private Double current;

  @Column(name = "mc")
  private Long mc;

  @Column(name = "name")
  private String name;

  @Column(name = "exchange")
  private String exchange;

  @Column(name = "type")
  private Integer type;

  @Column(name = "areacode")
  private String areacode;

  @Column(name = "tick_size")
  private Double tickSize;

  @Column(name = "has_follow")
  private Boolean hasFollow;

  @Column(name = "indcode")
  private String indcode;
}
