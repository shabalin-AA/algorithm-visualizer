package executor.interpreter.result;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Result {

    public Object value;

    public abstract JSONObject json() throws JSONException;
}
