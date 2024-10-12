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
  static String root = "/Users/aleksejsabalin/Study/as/algorithm-visualizer/visualizer/build";

  public static void main(String[] args) throws IOException {
    /*
    nds = new Node[] {
      new Node(0, NodeType.CALC, "a = 10; d = 50"),
      new Node(1, NodeType.COND, "a != 0"),
      new Node(2, NodeType.CALC, "b = a + 10"),
      new Node(3, NodeType.CALC, "b = a + 100"),
      new Node(4, NodeType.CALC, "d")
    };
    eds = new Edge[] {
      new Edge(0, 0, 1),
      new Edge(1, 1, 2, true),
      new Edge(2, 1, 3, false),
      new Edge(3, 2, 4),
      new Edge(4, 3, 4)
    };
    */
    interpreter = new Interpreter(nds, eds);
    //i.eval();
    server = SimpleFileServer.createFileServer(
      new InetSocketAddress("localhost", 3000), 
      Path.of(root), 
      SimpleFileServer.OutputLevel.INFO
    );
    server.start();
  }

}
