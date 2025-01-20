package executor.interpreter.result;

import org.json.JSONException;
import org.json.JSONObject;

public class Err extends Result {

    public Err(Exception value) {
        this.value = value;
    }

    public JSONObject json() throws JSONException {
        JSONObject jo = new JSONObject();
        while (value instanceof Result) {
            this.value = ((Result) value).unwrap();
        }
        jo.put("err", this.value.toString());
        return jo;
    }

    public String toString() {
        return String.format("Err : %s\n", value.toString());
    }
}
