package executor.interpreter.expr;

import java.util.HashMap;


public class AssignExpr extends BinaryExpr {
  @Override
  public Object eval(HashMap<String, Object> scope) {
    String id = ((IdExpr)l).id;
    Object value = r.eval(scope);
    scope.put(id, value);
    return value;
  }

  public int precedence() { return 2; }

  public void add(Expr child) {
    if (l == null) l = (IdExpr)child;
    else if (r == null) r = child;
  }
}
