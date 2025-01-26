package executor;

import executor.flowchart.Flowchart;
import executor.interpreter.Interpreter;
import executor.interpreter.result.Result;
import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteHandler {

    Logger logger = LoggerFactory.getLogger(ExecuteHandler.class);
    Interpreter currentInterpreter;

    public String executeFlowchart(JSONObject flowchart) {
        HashMap<Integer, Result> results = null;
        JSONObject response = null;
        //TODO: make modules not hardcoded
        Class<?>[] modules = new Class<?>[] { Math.class };
        Flowchart flow = new Flowchart(flowchart, new HashMap<>(), modules);
        currentInterpreter = new Interpreter(flow);
        results = currentInterpreter.eval();
        try {
            response = resultsJson(results);
        } catch (JSONException e) {
            logger.error("[json] wrong response\n{}", response);
        }
        return response.toString();
    }

    JSONObject resultsJson(HashMap<Integer, Result> results) throws JSONException {
        JSONObject resultsJo = new JSONObject();
        for (Integer k : results.keySet()) {
            Result v = results.get(k);
            resultsJo.put(k.toString(), v.json());
        }
        return resultsJo;
    }

    void haltExecution() {
        currentInterpreter.halt = true;
    }
}
