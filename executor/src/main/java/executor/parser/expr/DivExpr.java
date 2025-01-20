package executor.parser.expr;

import executor.interpreter.result.*;
import java.util.HashMap;

public class DivExpr extends BinaryExpr {

    @Override
    public Result eval(HashMap<String, Object> scope) {
        try {
            Result aRes = this.l.eval(scope);
            Result bRes = this.r.eval(scope);
            if (aRes instanceof Err) return aRes;
            if (bRes instanceof Err) return bRes;
            double a = (double) aRes.unwrap();
            double b = (double) bRes.unwrap();
            return new Ok(a / b);
        } catch (Exception e) {
            return new Err(e);
        }
    }

    public void add(Expr child) {
        if (l == null) l = child;
        else if (r == null) r = child;
    }
}
