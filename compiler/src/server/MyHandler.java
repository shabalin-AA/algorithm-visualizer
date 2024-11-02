package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


public class MyHandler implements HttpHandler {
  protected String method;
  protected String uri;
  protected String protocol;
  protected String body;

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
    method = t.getRequestMethod();
    uri = t.getRequestURI().toString();
    protocol = t.getProtocol();
    body = requestBody(t, 1024);
    System.out.printf("%s %s %s\n%s\n", method, uri, protocol, body);
  }
}
