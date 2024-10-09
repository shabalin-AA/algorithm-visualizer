public class Main {
  public static void main(String[] args) {
    Node[] nds = new Node[] {
      new Node(0, NodeType.CALC, "a = 10; d = 50"),
      new Node(1, NodeType.COND, "a != 0"),
      new Node(2, NodeType.CALC, "b = a + 10"),
      new Node(3, NodeType.CALC, "b = a + 100"),
      new Node(4, NodeType.CALC, "d")
    };
    Edge[] eds = new Edge[] {
      new Edge(0, 0, 1),
      new Edge(1, 1, 2, true),
      new Edge(2, 1, 3, false),
      new Edge(3, 2, 4),
      new Edge(4, 3, 4)
    };
    Interpreter i = new Interpreter(nds, eds);
    i.eval();
  }
}
