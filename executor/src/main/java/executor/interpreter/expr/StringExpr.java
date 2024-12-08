package executor.interpreter.expr;

import java.util.HashMap;


public class StringExpr implements Expr {
  String value;

  public StringExpr(String value) {
    this.value = value;
  }

  @Override
  public Object eval(HashMap<String, Object> scope) {
    return value;
  }

  public void add(Expr child) {}
}
