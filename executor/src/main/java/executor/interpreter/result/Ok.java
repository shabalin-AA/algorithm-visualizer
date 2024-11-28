package executor.interpreter.result;

import org.json.JSONObject;


public class Ok extends Result {
    Object value;

    public Ok(Object value) {
        this.value = value;
    }

    public JSONObject json() {
        JSONObject jo = new JSONObject();
        jo.put("result", this.value);
        return jo;
    }
}
