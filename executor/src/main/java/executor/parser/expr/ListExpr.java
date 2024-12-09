package executor.parser.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListExpr implements Expr {

    protected Expr value;
    protected ListExpr next = null;

    @Override
    public Object eval(HashMap<String, Object> scope) {
        List<Object> acc = null;
        if (next == null) {
            acc = new ArrayList<Object>();
            acc.add(value.eval(scope));
        } else {
            acc = (List) next.eval(scope);
            acc.add(value.eval(scope));
        }
        return acc;
    }

    public void add(Expr child) {
        if (child instanceof ListExpr) if (next == null) next = (ListExpr) child;
        else if (value == null) value = child;
    }
}
