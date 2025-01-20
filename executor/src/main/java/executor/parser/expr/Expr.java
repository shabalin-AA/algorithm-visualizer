package executor.parser.expr;

import executor.interpreter.result.*;
import java.util.HashMap;

public interface Expr {
    public Result eval(HashMap<String, Object> scope);

    public void add(Expr child);
}
