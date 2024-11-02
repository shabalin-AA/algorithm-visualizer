package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Headers;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;
import java.io.File;


public class FileHandler extends MyHandler {
  protected Path root;

  public FileHandler(Path rootDirectory) {
    this.root = rootDirectory;
  }

  void doGet(HttpExchange t) {
    String fullPathStr = root.toString() + uri;
    File f = new File(fullPathStr);
    if (f.isDirectory())
      fullPathStr += "index.html";
    Path fullPath = Path.of(fullPathStr);
    try {
      String fcontent = new String(Files.readAllBytes(fullPath));
      stringResponse(t, fcontent);
    }
    catch (IOException e) {
      System.out.println(e.toString());
    }
  }

  @Override
  public void handle(HttpExchange t) {
    super.handle(t);
    if (method.equals("GET")) doGet(t);
  }
}
