package interpreter.expr;

import java.util.HashMap;
import java.util.List;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class IdExpr implements Expr {
  protected String id;
  protected Expr arg;

  public IdExpr(String id) {
    this.id = id;
  }

  @Override
  public Object eval(HashMap<String, Object> scope) {
    String varName = id;
    Object a,b;
    if (scope.containsKey(varName)) {
      a = scope.get(varName);
      b = a;
      if (a instanceof Field) {
        Field f = (Field)a;
        f.setAccessible(true);
        try {
          b = f.get(b);
        }
        catch (Exception e) {}
      }
      else if (a instanceof Method) {
        Method m = (Method)a;
        m.setAccessible(true);
        try {
          Object arguments = this.arg.eval(scope);
          if (arguments instanceof List) {
            List argList = (List)arguments;
            b = m.invoke(null, argList.toArray());
          }
          else {
            b = m.invoke(null, arguments);
          }
        }
        catch (Exception e) {
          System.out.println(e);
        }
      }
      return b;
    }
    else {
      scope.put(varName, null);
      return null;
    }
  }

  public int precedence() { return 9; }

  public void add(Expr child) {
    if (arg == null) arg = child;
  }

  public String toString() {
    return this.getClass().getName() + "\t" + this.id;
  }
}
