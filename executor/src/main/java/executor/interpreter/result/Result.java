package executor.interpreter.result;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Result {

    public abstract JSONObject json() throws JSONException;
}
