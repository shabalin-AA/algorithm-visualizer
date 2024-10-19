import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.SimpleFileServer;

import java.nio.file.Path;

import java.io.IOException;


public class Main {
  static Node[] nds;
  static Edge[] eds;
  static Interpreter interpreter;

  static HttpServer server;
  static String root = System.getProperty("user.dir") + "/../../visualizer/build";

  public static void main(String[] args) throws IOException {
    int port = 3000;
    server = HttpServer.create(new InetSocketAddress(port), 0);
    server.createContext("/", SimpleFileServer.createFileHandler(Path.of(root)));
    server.createContext("/execute", new ExecuteHandler());
    server.start();
  }
}
