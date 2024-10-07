public class Main {
  public static void main(String[] args) {
    Flowchart fc = new Flowchart();
    fc.root = new Node(NodeType.CALCULATION_NODE, "a = 10; b = a * 5; c = (a + 6) / b;");
    fc.root.appendl(new Node(NodeType.TERMINATION_NODE, null));
    byte[] ir = Compiler.compile(fc);
    for (int i = 0; i < ir.length; i++) {
      System.out.printf("%d \t %d \n", i, ir[i]);
    }
  }
}
