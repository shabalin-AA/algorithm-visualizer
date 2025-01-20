package executor.interpreter.result;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Result {

    Object value;

    public Object unwrap() {
        return this.value;
    }

    public abstract JSONObject json() throws JSONException;
}
