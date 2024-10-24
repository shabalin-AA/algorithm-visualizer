package interpreter.expr;

import java.util.HashMap;


public class AssignExpr extends BinaryExpr {
  protected IdExpr idExpr;
  protected Expr valueExpr;

  @Override
  public Object eval(HashMap<String, Object> scope) {
    String id = idExpr.id;
    Object value = valueExpr.eval(scope);
    scope.put(id, value);
    return value;
  }

  public int precedence() { return 2; }

  public void add(Expr child) {
    System.out.println(child);
    if (child instanceof IdExpr) {
      if (idExpr == null) idExpr = (IdExpr)child;
    }
    else if (valueExpr == null) valueExpr = child;
  }
}
