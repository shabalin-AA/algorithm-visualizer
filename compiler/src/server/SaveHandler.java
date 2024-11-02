package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


public class SaveHandler extends MyHandler {
  void saveFlowchart(HttpExchange t) {
    Connection conn = null;
    try {
      String url = "jdbc:sqlite:flowchart.db";
      conn = DriverManager.getConnection(url);
      Statement stmt = conn.createStatement();
      /*
      ResultSet res = stmt.executeQuery("select * from Node;");
      while (res.next()) {
        System.out.printf("%d\t%s\n", res.getInt(1), res.getString(2));
      }
      */
      stmt.close();
      conn.close();
    }
    catch (SQLException e) {
      System.out.println("cannot save to database\n" + e.toString());
    }
  }

  void doPost(HttpExchange t) {
    saveFlowchart(t);
  }

  @Override
  public void handle(HttpExchange t) {
    super.handle(t);
    if (method.equals("POST")) doPost(t);
  }
}
