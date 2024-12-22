package executor;

import executor.interpreter.*;
import executor.interpreter.result.*;
import java.util.HashMap;
import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecuteHandler {

    Logger logger = LoggerFactory.getLogger(ExecuteHandler.class);
    Interpreter currentInterpreter;

    public ExecuteHandler() {}

    public String executeFlowchart(JSONObject flowchart) {
        HashMap<Integer, Result> results = null;
        JSONObject response = null;
        //TODO: make modules not hardcoded
        Class<?>[] modules = new Class<?>[] { Math.class };
        currentInterpreter = new Interpreter(flowchart, modules, new HashMap<>());
        results = currentInterpreter.eval();
        try {
            response = resultsJson(results);
        } catch (JSONException e) {
            logger.error("[json] worng response\n{}", response);
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
