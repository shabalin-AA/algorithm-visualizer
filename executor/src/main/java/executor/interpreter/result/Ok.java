package executor.interpreter.result;

import org.json.JSONException;
import org.json.JSONObject;

public class Ok extends Result {

    public Ok(Object value) {
        this.value = value;
    }

    public JSONObject json() throws JSONException {
        JSONObject jo = new JSONObject();
        while (value instanceof Result) {
            this.value = ((Result) value).unwrap();
        }
        jo.put("ok", this.value);
        return jo;
    }

    public String toString() {
        return String.format("Ok : %s\n", value.toString());
    }
}
