package executor;

import executor.interpreter.*;
import executor.interpreter.result.*;
import java.util.List;
import javafx.util.Pair;
import org.json.*;

public class ExecuteHandler {

    public ExecuteHandler() {}

    public String executeFlowchart(JSONObject jo) throws JSONException {
        JSONArray nds = jo.getJSONArray("Nodes");
        Node[] nodes = new Node[nds.length()];
        for (int i = 0; i < nds.length(); i++) {
            nodes[i] = new Node(nds.getJSONObject(i));
        }
        JSONArray eds = jo.getJSONArray("Edges");
        Edge[] edges = new Edge[eds.length()];
        for (int i = 0; i < eds.length(); i++) {
            edges[i] = new Edge(eds.getJSONObject(i));
        }
        //TODO: make modules not hardcoded
        Class<?>[] modules = new Class<?>[] { Math.class };
        List<Pair<Integer, Result>> results =
            (new Interpreter(nodes, edges, modules)).eval();
        JSONObject response = resultsJson(results);
        return response.toString();
    }

    JSONObject resultsJson(List<Pair<Integer, Result>> results)
        throws JSONException {
        JSONObject resultsJo = new JSONObject();
        for (Pair<Integer, Result> pair : results) {
            Integer k = pair.getKey();
            Result v = pair.getValue();
            resultsJo.put(k.toString(), v.json());
        }
        return resultsJo;
    }
}
