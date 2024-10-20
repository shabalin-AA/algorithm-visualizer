package interpreter;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class Interpreter {
  Node[] nds;
  Edge[] eds;
  HashMap<String, Object> scope;
  HashMap<Integer, String> results;

  void printScope() {
    for (String k : scope.keySet()) {
      Object v = scope.get(k);
      if (v != null) 
        System.out.printf("%s\t%s\n", k, scope.get(k).toString());
      else
        System.out.printf("%s\tnull\n", k);
    }
  }

  public Interpreter(Node[] nds, Edge[] eds) {
    this.nds = nds;
    this.eds = eds;
    this.scope = new HashMap<String, Object>();
    this.results = new HashMap<Integer, String>();
  }

  Node firstNode() {
    for (Node n : nds) {
      boolean found = false;
      for (Edge e : eds) {
        if (e.target == n.id) {
          found = true;
          break;
        }
      }
      if (!found) return n;
    }
    return null;
  }

  enum TokenType {
    ID, NUM, OP,
    UNDEFINED,
    SPACE
  }

  TokenType getTokenType(char c) {
    if (c >= '0' && c <= '9') 
      return TokenType.NUM;
    else if (c == '=' ||
             c == '+' ||
             c == '>' ||
             c == '<' ||
             c == '-' ||
             c == '*' ||
             c == '/' ||
             c == '!' ||
             c == ';')
      return TokenType.OP;
    else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))
      return TokenType.ID;
    else if (c == ' '  ||
             c == '\t' ||
             c == '\n')
      return TokenType.SPACE;
    return TokenType.UNDEFINED;
  }

  class Token {
    public String str;
    public TokenType type;

    public Token(TokenType type, String str) {
      this.type = type;
      this.str = str;
    }
  }

  Token[] tokenize(String codeStr) {
    ArrayList<Token> res = new ArrayList<Token>();
    char[] code = codeStr.toCharArray();
    int b = 0, e = 0;
    TokenType bt, et;
    while (b < code.length) {
      bt = getTokenType(code[b]);
      e = b;
      et = getTokenType(code[e]);
      while (et == bt) {
        e++;
        if (e == code.length) break;
        et = getTokenType(code[e]);
      }
      if (bt != TokenType.SPACE)
        res.add(new Token(bt, codeStr.substring(b,e)));
      b = e;
    }
    return res.toArray(new Token[res.size()]);
  }

  enum ExprType {
    NEXT_EXPR,
    ASSIGN,
    EQ, NOT_EQ,
    GT, LS,
    ADD, SUB,
    MUL, DIV,
    NUM,
    ID,
    UNDEFINED
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

    public Object eval() {
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
          Object value = valueExpr.eval();
          scope.put(id, value);
          //printScope();
          return value;
        case GT:
          Object a = this.args.get(0).eval();
          assert a instanceof Double;
          Object b = this.args.get(1).eval();
          assert b instanceof Double;
          return ((double)a) > ((double)b);
        case ADD:
          a = this.args.get(0).eval();
          assert a instanceof Double;
          b = this.args.get(1).eval();
          assert b instanceof Double;
          return ((double)a + (double)b);
        case SUB:
          a = this.args.get(0).eval();
          assert a instanceof Double;
          b = this.args.get(1).eval();
          assert b instanceof Double;
          return ((double)a - (double)b);
        case MUL:
          a = this.args.get(0).eval();
          assert a instanceof Double;
          b = this.args.get(1).eval();
          assert b instanceof Double;
          return ((double)a * (double)b);
        case DIV:
          a = this.args.get(0).eval();
          assert a instanceof Double;
          b = this.args.get(1).eval();
          assert b instanceof Double;
          return ((double)a / (double)b);
        case EQ:
          a = this.args.get(0).eval();
          assert a instanceof Double;
          b = this.args.get(1).eval();
          assert b instanceof Double;
          return ((double)a == (double)b);
        case NOT_EQ:
          a = this.args.get(0).eval();
          assert a instanceof Double;
          b = this.args.get(1).eval();
          assert b instanceof Double;
          return ((double)a != (double)b);
        case NEXT_EXPR:
          a = null;
          for (Expr arg : this.args) 
            a = arg.eval();
          return a;
        default: break;
      }
      return null;
    }
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

  int minExprTypeIdx(Token[] tokens) {
    ExprType minEt = ExprType.UNDEFINED;
    int res = -1;
    for (int i = 0; i < tokens.length; i++) {
      ExprType et = getExprType(tokens[i]);
      if (et.ordinal() < minEt.ordinal()) {
        minEt = et;
        res = i;
      }
    }
    return res;
  }

  Expr parse(Token[] tokens) {
    if (tokens.length == 0) return null;
    Expr res = new Expr();
    if (tokens.length == 1) {
      res.type = getExprType(tokens[0]);
      switch (res.type) {
        case NUM:
          res.value = Double.parseDouble(tokens[0].str);
          break;
        case ID:
          res.value = tokens[0].str;
          break;
        default: break;
      }
      return res;
    }
    int minETidx = minExprTypeIdx(tokens);
    if (minETidx < 0) return null;
    res.type = getExprType(tokens[minETidx]);
    Token[] tokL = Arrays.copyOfRange(tokens, 0, minETidx);
    Token[] tokR = Arrays.copyOfRange(tokens, minETidx+1, tokens.length);
    Expr left = parse(tokL);
    Expr right = parse(tokR);
    if (left != null) res.args.add(left);
    if (right != null) res.args.add(right);
    return res;
  } 

  Object evalNode(Node n) {
    Token[] tokens = tokenize(n.code);
    Expr ast = parse(tokens);
    Object res = ast.eval();
    if (true) { /* debug */
      System.out.println(n.code);
      for (Token t : tokens) {
        System.out.printf("%s\t%s\n", t.str, t.type.name());
      }
      System.out.println(); 
      System.out.println(ast);
      System.out.println("result:\t" + res);
      System.out.println('\n');
    }
    switch (n.type) {
      case COND:
        scope.put("_if_value", (boolean)res);
        break;
      default: break;
    }
    return res;
  }

  Node nextNode(Node crnt) {
    for (Edge e : eds) {
      boolean edge = (e.source == crnt.id);
      if (crnt.type == NodeType.COND) {
        edge = edge && (e.branch == (boolean)scope.get("_if_value"));
      }
      if (edge) {
        for (Node n : nds) {
          if (n.id == e.target) return n;
        }
      }
    }
    return null;
  }

  public HashMap<Integer, String> eval() {
    Node crnt = firstNode();
    while (crnt != null) {
      results.put(crnt.id, evalNode(crnt).toString());
      crnt = nextNode(crnt);
    }
    return results;
  }
}
