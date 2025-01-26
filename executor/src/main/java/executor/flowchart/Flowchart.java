package executor.flowchart;

import executor.flowchart.node.Node;
import executor.flowchart.node.NodeFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Flowchart {

    Logger logger = LoggerFactory.getLogger(executor.flowchart.Flowchart.class);

    public Node[] nds;
    public Edge[] eds;
    public HashMap<String, Object> scope;
    public Class<?>[] modules;

    public Flowchart(JSONObject flowchart, HashMap<String, Object> scope, Class<?>[] modules) {
        this.nds = flowchartNodes(flowchart);
        this.eds = flowchartEdges(flowchart);
        this.scope = scope;
        this.modules = modules;
        for (Class<?> cls : modules) {
            for (Field f : cls.getDeclaredFields()) scope.put(f.getName(), f);
            for (Method m : cls.getDeclaredMethods()) scope.put(m.getName(), m);
        }
    }

    Node[] flowchartNodes(JSONObject flowchart) {
        try {
            JSONArray nds = flowchart.getJSONArray("Nodes");
            Node[] nodes = new Node[nds.length()];
            NodeFactory nodeFactory = new NodeFactory();
            for (int i = 0; i < nds.length(); i++) {
                nodes[i] = nodeFactory.createNode(nds.getJSONObject(i), this);
            }
            return nodes;
        } catch (JSONException e) {
            logger.error("[json] Wrong flowchart json\n{}\n{}", flowchart.toString(), e.toString());
            return null;
        }
    }

    Edge[] flowchartEdges(JSONObject flowchart) {
        try {
            JSONArray eds = flowchart.getJSONArray("Edges");
            Edge[] edges = new Edge[eds.length()];
            for (int i = 0; i < eds.length(); i++) {
                edges[i] = new Edge(eds.getJSONObject(i));
            }
            return edges;
        } catch (JSONException e) {
            logger.error("[json] Wrong flowchart json\n{}\n{}", flowchart.toString(), e.toString());
            return null;
        }
    }
}
