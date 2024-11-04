package executor.interpreter.expr;

import java.util.HashMap;


public class NextExpr extends BinaryExpr {
  @Override
  public Object eval(HashMap<String, Object> scope) {
    l.eval(scope);
    return r.eval(scope);
  }

  public int precedence() { return 0; }

  public void add(Expr child) {
    if (l == null) l = child;
    else if (r == null) r = child;
  }
}
