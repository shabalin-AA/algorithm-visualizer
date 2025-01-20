package executor.parser.expr;

import executor.interpreter.result.*;
import java.util.HashMap;

public class NumExpr implements Expr {

    double value;

    public NumExpr(double value) {
        this.value = value;
    }

    @Override
    public Result eval(HashMap<String, Object> scope) {
        return new Ok(value);
    }

    public void add(Expr child) {
        throw new IllegalArgumentException("NumExpr has no children");
    }
}
