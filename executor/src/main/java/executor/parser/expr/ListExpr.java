package executor.parser.expr;

import executor.interpreter.result.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListExpr implements Expr {

    protected Expr value = null;
    protected Expr next = null;

    @Override
    public Result eval(HashMap<String, Object> scope) {
        ArrayList<Object> acc = new ArrayList<Object>();
        acc.add(this.value.eval(scope).unwrap());
        Expr cur = next;
        while (cur != null) {
            if (cur instanceof ListExpr) {
                ListExpr curList = (ListExpr) cur;
                acc.add(curList.value.eval(scope).unwrap());
                cur = curList.next;
            } else {
                acc.add(cur.eval(scope).unwrap());
                cur = null;
            }
        }
        return new Ok(acc);
    }

    public void add(Expr child) {
        if (value == null) {
            value = child;
        } else if (next == null) {
            next = child;
        }
    }
}
