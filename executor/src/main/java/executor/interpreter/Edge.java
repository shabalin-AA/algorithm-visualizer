package executor.interpreter;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Edge {

    Logger logger = LoggerFactory.getLogger(executor.interpreter.Edge.class);

    public int target;
    public int source;
    public int id;
    public boolean branch;
    String fullJson;

    public Edge(int id, int source, int target) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.branch = true;
    }

    public Edge(int id, int source, int target, boolean branch) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.branch = branch;
    }

    public Edge(JSONObject jo) {
        try {
            this.id = Integer.parseInt(jo.getString("id"));
            this.source = Integer.parseInt(jo.getString("source"));
            this.target = Integer.parseInt(jo.getString("target"));
            String branch = jo.getString("branch");
            if (branch.equals("false")) this.branch = false;
            else this.branch = true;
            this.fullJson = jo.getString("fullJson");
        } catch (JSONException e) {
            logger.warn("Wrong Edge json\n" + e.toString());
        }
    }
}
