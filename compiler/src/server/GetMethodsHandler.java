package server;

import com.sun.net.httpserver.HttpExchange;

import java.io.OutputStream;
import java.io.IOException;

import java.lang.reflect.Method;


public class GetMethodsHandler extends MyHandler {
  void doGet(HttpExchange t) {
    Class<?> clazz = Math.class;
    System.out.println(clazz.getName());
    Method[] methods = clazz.getDeclaredMethods();
    String res = "";
    for (Method m : methods) {
      res += m.getName();
    }
    stringResponse(t, res);
  }

  @Override
  public void handle(HttpExchange t) {
    if (method.equals("GET")) doGet(t);
  }
}
