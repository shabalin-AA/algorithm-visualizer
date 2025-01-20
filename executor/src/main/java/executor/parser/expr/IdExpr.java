package executor.parser.expr;

import executor.interpreter.result.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;

public class IdExpr implements Expr {

    protected String id;
    protected Expr arg; // if id represents field or method

    public IdExpr(String id) {
        this.id = id;
    }

    Result evalField(Field field) {
        field.setAccessible(true);
        try {
            return new Ok(field.get(null));
        } catch (Exception e) {
            return new Err(e);
        }
    }

    Result evalMethod(Method method, HashMap<String, Object> scope) {
        method.setAccessible(true);
        try {
            Result argumentsRes = this.arg.eval(scope);
            Object arguments = argumentsRes.unwrap();
            //TODO: invoke non-static methods
            if (arguments instanceof List) {
                List<Object> argList = (List) arguments;
                return new Ok(method.invoke(null, argList.toArray()));
            } else {
                return new Ok(method.invoke(null, arguments));
            }
        } catch (Exception e) {
            return new Err(e);
        }
    }

    Result evalFlowchart(JSONObject flowchart) {
        return new Ok(null);
    }

    @Override
    public Result eval(HashMap<String, Object> scope) {
        String varName = id;
        if (scope.containsKey(varName)) {
            Object value = scope.get(varName);
            if (value instanceof Field) {
                return evalField((Field) value);
            } else if (value instanceof Method) {
                return evalMethod((Method) value, scope);
            } else if (value instanceof JSONObject) {
                return evalFlowchart((JSONObject) value);
            } else if (value == null) {
                return new Err(new NullPointerException(String.format("Variable %s is null", varName)));
            } else {
                return new Ok(value);
            }
        } else {
            scope.put(varName, null);
            return new Err(new NullPointerException(String.format("Variable %s is null", varName)));
        }
    }

    public void add(Expr child) {
        if (arg == null) arg = child;
    }
}
