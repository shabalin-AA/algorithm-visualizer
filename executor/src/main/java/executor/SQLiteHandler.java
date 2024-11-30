package executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

public class SQLiteHandler {

    Logger logger = LoggerFactory.getLogger(executor.SQLiteHandler.class);

    private static final String CON_STR = "jdbc:sqlite:dev.db";
    private Connection connection;

    public SQLiteHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
        } catch (SQLException e) {
            logger.error(
                "Unable connect to database {}\n{}",
                CON_STR,
                e.toString()
            );
        }
    }

    public void insertFlowchart(JSONObject flowchart) {
        String sql = String.format(
            "insert into flowchart (json) values (\'%s\');",
            flowchart.toString()
        );
        logger.info("[sql] {}", sql);
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch (Exception e) {
            logger.warn("[sql]\t{}", e.toString());
        }
    }

    public String getFlowchart(long id) {
        String json = "";
        String sql = String.format(
            "select json from flowchart where id=%d;",
            id
        );
        logger.info("[sql] {}", sql);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            json = rs.getString("json");
            stmt.close();
        } catch (Exception e) {
            logger.warn("[sql]\t{}", e.toString());
        }
        return json;
    }
}
