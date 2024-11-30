package executor.interpreter.result;

import org.json.JSONException;
import org.json.JSONObject;

public class Err extends Result {

    public Exception value;

    public Err(Exception value) {
        this.value = value;
    }

    public JSONObject json() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("isErr", true);
        jo.put("err", this.value.toString());
        return jo;
    }
}
