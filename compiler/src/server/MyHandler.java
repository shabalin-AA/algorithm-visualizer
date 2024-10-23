package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


public class MyHandler implements HttpHandler {
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

  void stringResponse(HttpExchange t, String responseStr) {
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

  @Override
  public void handle(HttpExchange t) {
    String method = t.getRequestMethod();
    String uri = t.getRequestURI().toString();
    String protocol = t.getProtocol();
    System.out.printf("%s %s %s\n", method, uri, protocol);
    System.out.println(requestBody(t, 1024));
    stringResponse(t, "response");
  }
}
