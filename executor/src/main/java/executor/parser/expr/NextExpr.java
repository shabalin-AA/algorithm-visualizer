package executor.parser.expr;

import java.util.HashMap;

public class NextExpr extends BinaryExpr {

    @Override
    public Object eval(HashMap<String, Object> scope) {
        Object res = null;
        if (l != null) res = l.eval(scope);
        if (r != null) res = r.eval(scope);
        return res;
    }

    public void add(Expr child) {
        if (l == null) l = child;
        else if (r == null) r = child;
    }
}
