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

  void insertNode(Node node, int flowchart_id) {
    try {
      Statement st = connection.createStatement();
      String sql = String.format(
        "insert into node (node_id, flowchart, code, type) " + 
                    "values (%d, %d, \"%s\", %d);", 
        flowchart_id, 
        node.code, 
        node.type.ordinal()
      );
      st.execute(sql);
      st.close();
    } catch (Exception e) {
      System.out.println("cannot insert node\n" + e.toString());
    }
  }

  void insertEdge(Edge edge, int flowchart_id) {
  }

  public void insertFlowchart(JSONObject flowchart) {
    JSONArray nds = flowchart.getJSONArray("Nodes");
    for (int i = 0; i < nds.length(); i++) {
      Node node = new Node(nds.getJSONObject(i));
      insertNode(node, 1);
    }
    JSONArray eds = flowchart.getJSONArray("Edges");
    for (int i = 0; i < eds.length(); i++) {
      Edge edge = new Edge(eds.getJSONObject(i));
      insertEdge(edge, 1);
    }
  }
}
