package executor.parser.expr;

import executor.interpreter.result.*;
import java.util.HashMap;

public class NextExpr extends BinaryExpr {

    @Override
    public Result eval(HashMap<String, Object> scope) {
        Object res = null;
        if (l != null) res = l.eval(scope);
        if (r != null) res = r.eval(scope);
        return new Ok(res);
    }

    public void add(Expr child) {
        if (l == null) l = child;
        else if (r == null) r = child;
    }
}
