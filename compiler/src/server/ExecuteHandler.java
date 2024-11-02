package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.util.HashMap;

import org.json.*;

import interpreter.Edge;
import interpreter.Node;
import interpreter.Interpreter;


public class ExecuteHandler extends MyHandler {
  void executeFlowchart(HttpExchange t) {
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
    //TODO: make modules not hardcoded
    Class<?>[] modules = new Class<?>[] {
      Math.class
    };
    HashMap<Integer, String> results = (new Interpreter(nodes, edges, modules)).eval();
    JSONObject response = new JSONObject(results);
    Headers headers = t.getResponseHeaders();
    headers.set("Content-Type", "application/json");
    String responseStr = response.toString();
    stringResponse(t, responseStr);
  }

  void doPost(HttpExchange t) {
    executeFlowchart(t);
  }

  @Override
  public void handle(HttpExchange t) {
    super.handle(t);
    if (method.equals("POST")) doPost(t);
  }
}
