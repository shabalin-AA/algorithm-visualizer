package executor.parser.expr;

import executor.interpreter.result.*;
import java.util.HashMap;

public class AssignExpr extends BinaryExpr {

    @Override
    public Result eval(HashMap<String, Object> scope) {
        String id = ((IdExpr) l).id;
        Result valueRes = r.eval(scope);
        if (valueRes instanceof Err) return valueRes;
        Object value = valueRes.unwrap();
        scope.put(id, value);
        return new Ok(value);
    }

    public void add(Expr child) {
        if (l == null) l = (IdExpr) child;
        else if (r == null) r = child;
    }
}
