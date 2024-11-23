package executor;

import org.sqlite.JDBC;

import java.sql.*;
import java.util.*;
import org.json.*;

import executor.interpreter.Node;
import executor.interpreter.Edge;


public class SQLiteHandler {
    private static final String CON_STR = "jdbc:sqlite:../dev.db";
    private Connection connection;

    public SQLiteHandler() {
        try {
            DriverManager.registerDriver(new JDBC());
            this.connection = DriverManager.getConnection(CON_STR);
        } catch (SQLException e) {
            System.out.println("unable connect to database");
        }
    }

    public void insertFlowchart(JSONObject flowchart) {
        String sql = String.format("insert into flowchart (json) values (\'%s\');", flowchart.toString());
        System.out.println(sql);
        try {
            Statement stmt = connection.createStatement();
            stmt.execute(sql);
            stmt.close();
        } catch(Exception e) {
            System.out.println(e.toString());
        }
    }

    public String getFlowchart(long id) {
        String json = "";
        String sql = String.format("select json from flowchart where id=%d;", id);
        System.out.println(sql);
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            json = rs.getString("json");
            stmt.close();
        } catch(Exception e) {
            System.out.println(e.toString());
        }
        return json;
    }
}
