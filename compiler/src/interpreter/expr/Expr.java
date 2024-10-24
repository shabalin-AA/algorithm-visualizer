package interpreter.expr;

import java.util.HashMap;


public interface Expr {
  public Object eval(HashMap<String, Object> scope);
  public int precedence();
  public void add(Expr child);
}

/*
class _Expr {
  ExprType type;
  ArrayList<Expr> args;
  Object value;

  public _Expr() {
    this.type = ExprType.UNDEFINED;
    this.args = new ArrayList<Expr>();
  }

  public String toString() {
    return exprToString(this, 0);
  }

  String exprToString(Expr e, int depth) {
    String res = e.type.name() + '\n';
    for (Expr arg : e.args) {
      for (int i = 0; i < depth; i++)
        res += ' ';
      res += '-';
      res += exprToString(arg, depth + 1);
    }
    return res;
  }

  public static ExprType getType(Token t) {
    char[] chars = t.str.toCharArray();
    switch (t.type) {
      case OP:
        if (t.str.equals(";")) return ExprType.NEXT_EXPR;
        if (t.str.equals("=")) return ExprType.ASSIGN;
        if (t.str.equals(">")) return ExprType.GT;
        if (t.str.equals("<")) return ExprType.LS;
        if (t.str.equals("+")) return ExprType.ADD;
        if (t.str.equals("-")) return ExprType.SUB;
        if (t.str.equals("*")) return ExprType.MUL;
        if (t.str.equals("/")) return ExprType.DIV;
        if (t.str.equals("==")) return ExprType.EQ;
        if (t.str.equals("!=")) return ExprType.NOT_EQ;
        if (t.str.equals(",")) return ExprType.LIST;
        break;
      case NUM:
        for (char c : chars) {
          if (c == '.' || (c >= '0' && c <= '9')) {}
          else return ExprType.UNDEFINED;
        }
        return ExprType.NUM;
      case ID:
        for (char c : chars) {
          if (c == '_' || 
            (c >= '0' && c <= '9') ||
            (c >= 'A' && c <= 'z')) {}
          else return ExprType.UNDEFINED;
        }
        return ExprType.ID;
    }
    return ExprType.UNDEFINED;
  }
  
  public Object eval(HashMap<String, Object> scope) {
    Object a, b;
    switch (this.type) {
      case LIST:
        Expr e1 = this.args.get(0);
        Expr e2 = this.args.get(1);
        List acc = null;
        if (e1.type == ExprType.LIST) {
          acc = (List)e1.eval(scope);
          acc.add(e2.eval(scope));
        }
        else if (e2.type == ExprType.LIST) {
          acc = (List)e2.eval(scope);
          acc.add(e1.eval(scope));
        }
        else {
          acc = new ArrayList();
          acc.add(e1.eval(scope));
          acc.add(e2.eval(scope));
        }
        return acc;
      case NUM:
        return value;
      case ID:
        String varName = (String)value;
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
              Object arguments = this.args.get(0).eval(scope);
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
      case UNDEFINED:
        return null;
      case ASSIGN:
        Expr idExpr = (Expr)this.args.get(0);
        assert idExpr.type == ExprType.ID : "Need id expr on left";
        Expr valueExpr = (Expr)this.args.get(1);
        String id = (String)idExpr.value;
        Object value = valueExpr.eval(scope);
        scope.put(id, value);
        return value;
      case GT:
        try {
          a = this.args.get(0).eval(scope);
          b = this.args.get(1).eval(scope);
          return ((double)a > (double)b);
        }
        catch (Exception e) {}
      case ADD:
        try {
          a = this.args.get(0).eval(scope);
          b = this.args.get(1).eval(scope);
          return ((double)a + (double)b);
        }
        catch (Exception e) {}
      case SUB:
        try {
          a = this.args.get(0).eval(scope);
          b = this.args.get(1).eval(scope);
          return ((double)a - (double)b);
        }
        catch (Exception e) {}
      case MUL:
        try {
          a = this.args.get(0).eval(scope);
          b = this.args.get(1).eval(scope);
          return ((double)a * (double)b);
        }
        catch (Exception e) {}
      case DIV:
        try {
          a = this.args.get(0).eval(scope);
          b = this.args.get(1).eval(scope);
          return ((double)a / (double)b);
        }
        catch (Exception e) {}
        break;
      case EQ:
        try {
          a = this.args.get(0).eval(scope);
          b = this.args.get(1).eval(scope);
          return ((double)a == (double)b);
        }
        catch (Exception e) {}
      case NOT_EQ:
        try {
          a = this.args.get(0).eval(scope);
          b = this.args.get(1).eval(scope);
          return ((double)a != (double)b);
        }
        catch (Exception e) {}
      case NEXT_EXPR:
        a = null;
        for (Expr arg : this.args) 
          a = arg.eval(scope);
        return a;
      default: break;
    }
    return null;
  }
}
  
*/
