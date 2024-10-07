public class Node {
  NodeType type;
  String code;
  Node left;
  Node right;
  public Node(NodeType type, String code) {
    this.type = type;
    this.code = code;
  }
  public Node appendl(Node n) {
    this.left = n;
    return n;
  }
  public Node appendr(Node n) {
    this.right = n;
    return n;
  }
}
