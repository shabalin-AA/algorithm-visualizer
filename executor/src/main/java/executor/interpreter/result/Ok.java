package executor.interpreter.result;

import org.json.JSONException;
import org.json.JSONObject;

public class Ok extends Result {

    public Object value;

    public Ok(Object value) {
        this.value = value;
    }

    public JSONObject json() throws JSONException {
        JSONObject jo = new JSONObject();
        jo.put("result", this.value);
        return jo;
    }
}
