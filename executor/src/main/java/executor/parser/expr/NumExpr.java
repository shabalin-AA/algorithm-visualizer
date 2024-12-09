package executor.parser.expr;

import java.util.HashMap;

public class NumExpr implements Expr {

    double value;

    public NumExpr(double value) {
        this.value = value;
    }

    @Override
    public Object eval(HashMap<String, Object> scope) {
        return value;
    }

    public void add(Expr child) {}
}
