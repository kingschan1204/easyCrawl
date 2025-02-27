package sql;

import com.github.kingschan1204.easycrawl.sql.DbType;
import com.github.kingschan1204.easycrawl.sql.TableMaping;
import com.test.entity.StockCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UpsertTest")
public class UpsertTest {

    @Test
    @DisplayName("upsert Sql test ")
    public void upsertSql(){
        TableMaping tableMaping = new TableMaping(StockCode.class, DbType.MYSQL);
        System.out.println(tableMaping.updateSql());
    }
}
