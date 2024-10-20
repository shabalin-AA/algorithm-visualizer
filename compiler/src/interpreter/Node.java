package interpreter;

import org.json.JSONObject;


public class Node {
  public int id;
  public NodeType type;
  public String code;

  public Node(int id, NodeType type, String code) {
    this.id = id;
    this.type = type;
    this.code = code;
  }

  public Node(JSONObject jo) {
    this.id = Integer.parseInt(jo.getString("id"));
    this.code = jo.getString("code");
    String type = jo.getString("type");
    NodeType[] types = NodeType.values();
    for (NodeType t : types) {
      if (t.name().equals(type)) {
        this.type = t;
        break;
      }
    }
  }
}
