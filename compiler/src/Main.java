import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.SimpleFileServer;
import java.nio.file.Path;
import java.io.IOException;

import server.ExecuteHandler;
import server.GetMethodsHandler;


public class Main {
  static HttpServer server;
  static String root = System.getProperty("user.dir") + "/../visualizer/build";

  public static void main(String[] args) {
    int port = 3000;
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext("/", SimpleFileServer.createFileHandler(Path.of(root)));
      server.createContext("/execute", new ExecuteHandler());
      server.createContext("/methods", new GetMethodsHandler());
      server.start();
    }
    catch (IOException e) {
      System.out.println("cannot start the server\n" + e.toString());
    }
  }
}
