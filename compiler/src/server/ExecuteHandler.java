package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.util.HashMap;

import org.json.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

import interpreter.Edge;
import interpreter.Node;
import interpreter.Interpreter;


public class ExecuteHandler implements HttpHandler {
  String requestBody(HttpExchange t, int maxBytes) {
    String body = "";
    try {
      InputStream is = t.getRequestBody();
      byte[] requestBodyBytes = new byte[maxBytes];
      is.read(requestBodyBytes);
      is.close();
      body = new String(requestBodyBytes);
    }
    catch (IOException e) {
      System.out.println("cannot read request body\n" + e.toString());
    }
    return body;
  }

  void responseBody(HttpExchange t, String responseStr) {
    try {
      t.sendResponseHeaders(200, responseStr.length());
      OutputStream os = t.getResponseBody();
      os.write(responseStr.getBytes());
      os.close();
    }
    catch (IOException e) {
      System.out.println("cannot write response body\n" + e.toString());
    }
  }
  
  void executeFlowchart(HttpExchange t) {
    String body = requestBody(t, 1024);
    System.out.println(body);
    JSONObject jo = new JSONObject(body);
    JSONArray nds = jo.getJSONArray("nds");
    Node[] nodes = new Node[nds.length()];
    for (int i = 0; i < nds.length(); i++) {
      nodes[i] = new Node(nds.getJSONObject(i));
    }
    JSONArray eds = jo.getJSONArray("eds");
    Edge[] edges = new Edge[eds.length()];
    for (int i = 0; i < eds.length(); i++) {
      edges[i] = new Edge(eds.getJSONObject(i));
    }
    HashMap<Integer, String> results = (new Interpreter(nodes, edges)).eval();
    JSONObject response = new JSONObject(results);
    Headers headers = t.getResponseHeaders();
    headers.set("Content-Type", "application/json");
    String responseStr = response.toString();
    responseBody(t, responseStr);
  }

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
    executeFlowchart(t);
    saveFlowchart(t);
  }

  @Override
  public void handle(HttpExchange t) throws IOException {
    String method = t.getRequestMethod();
    String uri = t.getRequestURI().toString();
    String protocol = t.getProtocol();
    System.out.printf("%s %s %s\n", method, uri, protocol);
    if (method.equals("POST")) doPost(t);
  }
}
