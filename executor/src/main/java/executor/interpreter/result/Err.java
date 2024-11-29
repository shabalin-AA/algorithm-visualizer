package executor.interpreter.result;

import org.json.JSONObject;
import org.json.JSONException;


public class Err extends Result {
    public Exception value;

    public Err(Exception value) {
        this.value = value;
    }

    public JSONObject json() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("isErr", true);
            jo.put("err", this.value.toString());
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        return jo;
    }
}
