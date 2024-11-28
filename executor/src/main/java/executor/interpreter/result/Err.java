package executor.interpreter.result;

import org.json.JSONObject;


public class Err extends Result {
    Exception value;

    public Err(Exception value) {
        this.value = value;
    }

    public JSONObject json() {
        JSONObject jo = new JSONObject();
        jo.put("isErr", true);
        jo.put("err", this.value.toString());
        return jo;
    }
}
