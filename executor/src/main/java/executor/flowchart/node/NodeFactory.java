package executor.flowchart.node;

import executor.flowchart.Flowchart;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeFactory {

    Logger logger = LoggerFactory.getLogger(executor.flowchart.node.NodeFactory.class);

    public Node createNode(JSONObject jo, Flowchart flow) {
        Node newNode = null;
        try {
            String type = jo.getString("type");
            if (type.equals("COND")) {
                newNode = new CondNode();
            } else if (type.equals("CALC")) {
                newNode = new CodeNode();
            } else if (type.equals("SUBFLOW")) {
                newNode = new SubflowNode();
            }
            newNode.id = Integer.parseInt(jo.getString("id"));
            newNode.code = jo.getString("code");
            newNode.fullJson = jo.getString("fullJson");
            newNode.flow = flow;
        } catch (JSONException e) {
            logger.warn("Wrong Node json\n" + e.toString());
        }
        return newNode;
    }
}
