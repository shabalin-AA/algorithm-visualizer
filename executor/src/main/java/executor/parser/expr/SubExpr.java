package executor.parser.expr;

import java.util.HashMap;

public class SubExpr extends BinaryExpr {

    @Override
    public Object eval(HashMap<String, Object> scope) {
        try {
            if (r != null) {
                Object a = this.l.eval(scope);
                Object b = this.r.eval(scope);
                return ((double) a - (double) b);
            } else {
                return -(double) (this.l.eval(scope));
            }
        } catch (Exception e) {}
        return null;
    }

    public void add(Expr child) {
        if (l == null) l = child;
        else if (r == null) r = child;
    }
}
