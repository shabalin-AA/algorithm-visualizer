package executor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlite.JDBC;

public class SQLiteHandler {

    Logger logger = LoggerFactory.getLogger(SQLiteHandler.class);

    private static final String CON_STR = "jdbc:sqlite:dev.db";
    private Connection connection;

    public SQLiteHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
        } catch (SQLException e) {
            logger.error("Unable connect to database {}\n{}", CON_STR, e.toString());
        }
    }

    public String getFlowchartList() {
        String sql = "select * from flowchart;";
        logger.info("[sql] {}", sql);
        ArrayList<Long> ids = new ArrayList<Long>();
        ArrayList<String> names = new ArrayList<String>();
        ArrayList<String> jsons = new ArrayList<String>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                long id = rs.getInt("id");
                String name = rs.getString("name");
                String json = rs.getString("json");
                ids.add(id);
                names.add(name);
                jsons.add(json);
            }
            stmt.close();
        } catch (Exception e) {
            logger.warn("[sql]\t{}", e.toString());
        }
        try {
            JSONArray list = new JSONArray();
            for (int i = 0; i < ids.size(); i++) {
                JSONObject item = new JSONObject();
                item.put("id", ids.get(i));
                item.put("name", names.get(i));
                item.put("json", jsons.get(i));
                list.put(item);
            }
            return list.toString();
        } catch (JSONException e) {
            logger.warn("[json]\t{}", e.toString());
        }
        return "{}";
    }

    public void insertFlowchart(JSONObject flowchart, String name) {
        String sql = String.format(
            "insert into flowchart (name, json) values (\'%s\', \'%s\');",
            name,
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
        String sql = String.format("select json from flowchart where id=%d;", id);
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
