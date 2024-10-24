package interpreter.expr;

import java.util.HashMap;


public interface Expr {
  public Object eval(HashMap<String, Object> scope);
  public int precedence();
  public void add(Expr child);
}

