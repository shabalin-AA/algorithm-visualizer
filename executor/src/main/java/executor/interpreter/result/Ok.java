package executor.interpreter.result;

import org.json.JSONObject;
import org.json.JSONException;


public class Ok extends Result {
    public Object value;

    public Ok(Object value) {
        this.value = value;
    }

    public JSONObject json() {
        JSONObject jo = new JSONObject();
        try {
            jo.put("result", this.value);
        } catch (JSONException e) {
            System.out.println(e.toString());
        }
        return jo;
    }
}
