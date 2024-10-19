import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;


public class ExecuteHandler implements HttpHandler {
  void request(HttpExchange t) throws IOException {
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
    System.out.println(body);
  }

  void response(HttpExchange t) throws IOException {
    String response = "This is the response";
    t.sendResponseHeaders(200, response.length());
    OutputStream os = t.getResponseBody();
    os.write(response.getBytes());
    os.close();
  }

  @Override
  public void handle(HttpExchange t) throws IOException {
    request(t);
    response(t);
  }
}
