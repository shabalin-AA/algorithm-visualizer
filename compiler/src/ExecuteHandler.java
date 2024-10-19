import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.Headers;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.util.HashMap;

import org.json.*;


public class ExecuteHandler implements HttpHandler {
  JSONObject response;

  void request(HttpExchange t) throws IOException, JSONException {
    String method = t.getRequestMethod();
    String uri = t.getRequestURI().toString();
    String protocol = t.getProtocol();
    InputStream is = t.getRequestBody();
    byte[] requestBodyBytes = new byte[1024];
    is.read(requestBodyBytes);
    is.close();
    String body = new String(requestBodyBytes);
    System.out.println(method);
    System.out.println(uri);
    System.out.println(protocol);
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
    response = new JSONObject(results);
    System.out.println();
  }

  void response(HttpExchange t) throws IOException {
    Headers headers = t.getResponseHeaders();
    headers.set("Content-Type", "application/json");
    String responseStr = response.toString();
    t.sendResponseHeaders(200, responseStr.length());
    OutputStream os = t.getResponseBody();
    os.write(responseStr.getBytes());
    os.close();
  }

  @Override
  public void handle(HttpExchange t) throws IOException {
    request(t);
    response(t);
  }
}
