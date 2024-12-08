package executor.interpreter;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Node {

    Logger logger = LoggerFactory.getLogger(executor.interpreter.Node.class);

    public int id;
    public NodeType type;
    public String code;
    String fullJson;

    public Node(int id, NodeType type, String code) {
        this.id = id;
        this.type = type;
        this.code = code;
    }

    public Node(JSONObject jo) {
        try {
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
            this.fullJson = jo.getString("fullJson");
        } catch (JSONException e) {
            logger.warn("Wrong Node json\n" + e.toString());
        }
    }
}
