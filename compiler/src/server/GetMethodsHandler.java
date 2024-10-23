package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.io.IOException;

import java.lang.reflect.Method;


public class GetMethodsHandler extends MyHandler {
  void doGet(HttpExchange t) {
    try {
      t.getRequestBody().close();
    }
    catch (IOException e) {}
    Class<?> clazz = Math.class;
    System.out.println(clazz.getName());
    Method[] methods = clazz.getDeclaredMethods();
    for (Method m : methods) {
      System.out.println(m.getName());
    }
    stringResponse(t, "methods");
  }

  @Override
  public void handle(HttpExchange t) {
    String method = t.getRequestMethod();
    String uri = t.getRequestURI().toString();
    String protocol = t.getProtocol();
    System.out.printf("%s %s %s\n", method, uri, protocol);
    if (method.equals("GET")) doGet(t);
  }
}
