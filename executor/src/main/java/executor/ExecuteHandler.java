package executor;

import executor.interpreter.*;
import executor.interpreter.result.*;
import java.util.List;
import javafx.util.Pair;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteHandler {

    Logger logger = LoggerFactory.getLogger(ExecuteHandler.class);
    Interpreter currentInterpreter;

    public ExecuteHandler() {}

    public String executeFlowchart(JSONObject jo) {
        List<Pair<Integer, Result>> results = null;
        JSONObject response = null;
        try {
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
            currentInterpreter = new Interpreter(nodes, edges, modules);
            results = currentInterpreter.eval();
        } catch (JSONException e) {
            logger.error("[json] Wrong flowchart json\n{}\n{}", jo.toString(), e.toString());
            return "";
        }
        try {
            response = resultsJson(results);
            return response.toString();
        } catch (JSONException e) {
            logger.error("[json] Wrong result json\n{}", e.toString());
        }
        return "";
    }

    void haltExecution() {
        currentInterpreter.halt = true;
    }

    JSONObject resultsJson(List<Pair<Integer, Result>> results) throws JSONException {
        JSONObject resultsJo = new JSONObject();
        for (Pair<Integer, Result> pair : results) {
            Integer k = pair.getKey();
            Result v = pair.getValue();
            resultsJo.put(k.toString(), v.json());
        }
        return resultsJo;
    }
}
