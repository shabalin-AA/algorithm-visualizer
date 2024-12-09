package executor.parser.expr;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class IdExpr implements Expr {

    protected String id;
    protected Expr arg; // if id represents field or method

    public IdExpr(String id) {
        this.id = id;
    }

    Object evalField(Field field) {
        field.setAccessible(true);
        try {
            return field.get(null);
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    Object evalMethod(Method method, HashMap<String, Object> scope) {
        method.setAccessible(true);
        try {
            Object arguments = this.arg.eval(scope);
            //TODO: invoke non-static methods
            if (arguments instanceof List) {
                List<Object> argList = (List) arguments;
                return method.invoke(null, argList.toArray());
            } else {
                return method.invoke(null, arguments);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    @Override
    public Object eval(HashMap<String, Object> scope) {
        String varName = id;
        if (scope.containsKey(varName)) {
            Object value = scope.get(varName);
            if (value instanceof Field) {
                return evalField((Field) value);
            } else if (value instanceof Method) {
                return evalMethod((Method) value, scope);
            } else {
                return value;
            }
        } else {
            scope.put(varName, null);
            return null;
        }
    }

    public void add(Expr child) {
        if (arg == null) arg = child;
    }
}
