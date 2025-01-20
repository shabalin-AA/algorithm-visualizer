package executor.parser.expr;

import executor.interpreter.result.*;
import java.util.HashMap;

public class StringExpr implements Expr {

    String value;

    public StringExpr(String value) {
        this.value = value;
    }

    @Override
    public Result eval(HashMap<String, Object> scope) {
        return new Ok(value);
    }

    public void add(Expr child) {}
}
