package sql;

import com.github.kingschan1204.easycrawl.sql.DbType;
import com.github.kingschan1204.easycrawl.sql.TableMaping;
import com.test.entity.StockCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("TableHelper")
public class TableHelperTest {

  @Test
  @DisplayName("Sql test ")
  public void upsertSql() {
    TableMaping tableMaping = new TableMaping(StockCode.class, DbType.PGSQL);
    System.out.println(tableMaping.getSqlBuilder().insert());
    System.out.println(tableMaping.getSqlBuilder().upsertByPrimary());
    System.out.println(tableMaping.getSqlBuilder().updateByPrimary());
    System.out.println(tableMaping.getSqlBuilder().deleteByPrimary());
    System.out.println(tableMaping.getSqlBuilder().selectByPrimary());
  }
}
