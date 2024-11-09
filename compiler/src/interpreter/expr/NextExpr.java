package interpreter.expr;

import java.util.HashMap;


public class NextExpr implements Expr {
  protected Expr first;
  protected Expr second;
  @Override
  public Object eval(HashMap<String, Object> scope) {
    first.eval(scope);
    return second.eval(scope);
  }

  public int precedence() { return 0; }

  public void add(Expr child) {
    if (first == null) first = child;
    else if (second == null) second = child;
  }

  public String toString() {
    return "" + first + second;
  }
}
