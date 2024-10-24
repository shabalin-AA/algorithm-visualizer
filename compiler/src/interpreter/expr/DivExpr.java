package interpreter.expr;

import java.util.HashMap;


public class DivExpr extends BinaryExpr {
  @Override
  public Object eval(HashMap<String, Object> scope) {
    try {
      Object a = this.l.eval(scope);
      Object b = this.r.eval(scope);
      return ((double)a / (double)b);
    }
    catch (Exception e) {}
    return null;
  }

  public int precedence() { return 7; }

  public void add(Expr child) {
    if (l == null) l = child;
    else if (r == null) r = child;
  }
}
