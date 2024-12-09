package executor.parser.expr;

import java.util.HashMap;

public interface Expr {
    public Object eval(HashMap<String, Object> scope);

    public void add(Expr child);
}
