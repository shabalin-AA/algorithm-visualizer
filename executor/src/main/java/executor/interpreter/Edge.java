package executor.interpreter;

import org.apache.logging.log4j.*;
import org.json.JSONException;
import org.json.JSONObject;

public class Edge {

    private static final Logger logger = LogManager.getLogger(
        executor.Application.class
    );

    public int target;
    public int source;
    public int id;
    public boolean branch;

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
        } catch (JSONException e) {
            logger.debug("Wrong Edge json\n" + e.toString());
        }
    }
}
