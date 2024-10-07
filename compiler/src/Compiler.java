public class Compiler {
  final static int init_cap = 16;
  final static int grow_factor = 2;
  static byte[] res;
  static Flowchart fc;
  static Node current;

  static void resize() {
    byte[] tmp = new byte[res.length];
    for (int i = 0; i < res.length; i++) {
      tmp[i] = res[i];
    }
    res = new byte[tmp.length * grow_factor];
    for (int i = 0; i < tmp.length; i++) {
      res[i] = tmp[i];
    }
  }

  static void step() {
    current = current.left;
  }

  public static byte[] compile(Flowchart fc) {
    res = new byte[init_cap];
    fc = fc;
    current = fc.root;
    while (current.type != NodeType.TERMINATION_NODE) {
      step();
    }
    return res;
  }
}
