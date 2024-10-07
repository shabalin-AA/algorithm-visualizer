import java.util.ArrayList;

public class Compiler {
  Node[] nds;
  Edge[] eds;
  ArrayList<Byte> bytecode;

  public Compiler(Node[] nds, Edge[] eds) {
    this.nds = nds;
    this.eds = eds;
    this.bytecode = new ArrayList<Byte>();
  }

  Node firstNode() {
    for (Node n : nds) {
      boolean found = false;
      for (Edge e : eds) {
        if (e.target == n.id) {
          found = true;
          break;
        }
      }
      if (!found) return n;
    }
    return null;
  }

  Node nextNode(Node crnt) {
    switch (crnt.type) {
      case COND:
        break;
      default:
        int idx = -1;
        for (Edge e : eds) {
          if (e.source == crnt.id) {
            idx = e.target;
            break;
          }
        }
        for (Node n : nds) {
          if (n.id == idx) return n;
        }
        return null;
    }
    return null;
  }

  void compileNode(Node n) {
    switch (n.type) {
      case CALC: 
        System.out.printf("%d \t %s\n", n.id, n.type.name());
        break;
      case COND:
        System.out.printf("%d \t %s\n", n.id, n.type.name());
        break;
      default: break;
    }
  }

  public void compile() {
    Node crnt = firstNode();
    while (crnt != null) {
      compileNode(crnt);
      crnt = nextNode(crnt);
    }
  }
}
