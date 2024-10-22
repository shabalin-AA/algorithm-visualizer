package interpreter;

import java.util.ArrayList;
import java.util.HashMap;


class Expr {
  ExprType type;
  ArrayList<Expr> args;
  Object value;

  public Expr() {
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

  ExprType getExprType(Token t) {
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
    switch (this.type) {
      case NUM:
        return value;
      case ID:
        String varName = (String)value;
        if (scope.containsKey(varName))
          return scope.get(varName);
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
        Object a = this.args.get(0).eval(scope);
        assert a instanceof Double;
        Object b = this.args.get(1).eval(scope);
        assert b instanceof Double;
        return ((double)a) > ((double)b);
      case ADD:
        a = this.args.get(0).eval(scope);
        assert a instanceof Double;
        b = this.args.get(1).eval(scope);
        assert b instanceof Double;
        return ((double)a + (double)b);
      case SUB:
        a = this.args.get(0).eval(scope);
        assert a instanceof Double;
        b = this.args.get(1).eval(scope);
        assert b instanceof Double;
        return ((double)a - (double)b);
      case MUL:
        a = this.args.get(0).eval(scope);
        assert a instanceof Double;
        b = this.args.get(1).eval(scope);
        assert b instanceof Double;
        return ((double)a * (double)b);
      case DIV:
        a = this.args.get(0).eval(scope);
        assert a instanceof Double;
        b = this.args.get(1).eval(scope);
        assert b instanceof Double;
        return ((double)a / (double)b);
      case EQ:
        a = this.args.get(0).eval(scope);
        assert a instanceof Double;
        b = this.args.get(1).eval(scope);
        assert b instanceof Double;
        return ((double)a == (double)b);
      case NOT_EQ:
        a = this.args.get(0).eval(scope);
        assert a instanceof Double;
        b = this.args.get(1).eval(scope);
        assert b instanceof Double;
        return ((double)a != (double)b);
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
  
