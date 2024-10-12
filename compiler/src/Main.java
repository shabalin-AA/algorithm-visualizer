import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.SimpleFileServer;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;


public class Main {
  static Node[] nds;
  static Edge[] eds;
  static Interpreter interpreter;

  static HttpServer server;
  static String root = System.getProperty("user.dir") + "/../../visualizer/build";

  public static void main(String[] args) throws IOException {
    int port = 3000;
    server = SimpleFileServer.createFileServer(
      new InetSocketAddress("localhost", port), 
      Path.of(root), 
      SimpleFileServer.OutputLevel.INFO
    );
    System.out.println("\n\nStarting server at localhost:" + port);
    server.start();
  }

}
