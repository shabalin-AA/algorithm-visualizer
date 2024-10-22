package interpreter;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

public class Interpreter {
  Node[] nds;
  Edge[] eds;
  HashMap<String, Object> scope;

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
    else if (c == '(')
      return TokenType.LEFT_PAREN;
    else if (c == ')')
      return TokenType.RIGHT_PAREN;
    return TokenType.UNDEFINED;
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
      if (bt == TokenType.LEFT_PAREN ||
          bt == TokenType.RIGHT_PAREN) {
        e++;
      }
      else {
        while (et == bt) {
          e++;
          if (e == code.length) break;
          et = getTokenType(code[e]);
        }
      }
      if (bt != TokenType.SPACE)
        res.add(new Token(bt, codeStr.substring(b,e)));
      b = e;
    }
    return res.toArray(new Token[res.size()]);
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
  
  int minPrecIdx(Token[] tokens) {
    int minPrecedence = (int)1e6;
    int idx = -1;
    for (int i = 0; i < tokens.length; i++) {
      TokenType tt = tokens[i].type;
      if (tt == TokenType.LEFT_PAREN) {
        int paren = 1;
        while (paren != 0) {
          i++;
          switch (tokens[i].type) {
            case LEFT_PAREN:  paren++; break;
            case RIGHT_PAREN: paren--; break;
          }
        }
        i++;
        if (i >= tokens.length) break;
      }
      int p = getExprType(tokens[i]).ordinal();
      if (p < minPrecedence) {
        minPrecedence = p;
        idx = i;
      }
    }
    return idx;
  }

  boolean checkParentheses(Token[] tokens) {
    int paren = 0;
    for (int i = 0; i < tokens.length; i++) {
      switch (tokens[i].type) {
        case LEFT_PAREN: paren++; break;
        case RIGHT_PAREN: paren--; break;
      }
      if (paren < 0) return false;
    }
    return paren == 0;
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
    if (tokens[0].type == TokenType.LEFT_PAREN &&
        tokens[tokens.length-1].type == TokenType.RIGHT_PAREN) {
      Token[] inner = Arrays.copyOfRange(tokens, 1, tokens.length-1);
      if (checkParentheses(inner))
        return parse(inner);
    }
    int minpIdx = minPrecIdx(tokens);
    if (minpIdx < 0) return null;
    res.type = getExprType(tokens[minpIdx]);
    Token[] tokL = Arrays.copyOfRange(tokens, 0, minpIdx);
    Token[] tokR = Arrays.copyOfRange(tokens, minpIdx+1, tokens.length);
    Expr left = parse(tokL);
    Expr right = parse(tokR);
    if (left != null) res.args.add(left);
    if (right != null) res.args.add(right);
    return res;
  } 

  Object evalNode(Node n) {
      System.out.println(n.code);
    Token[] tokens = tokenize(n.code);
      for (Token t : tokens)
        System.out.printf("%s\t%s\n", t.str, t.type.name());
      System.out.println(); 
    Expr ast = parse(tokens);
      System.out.println(ast);
    Object res = ast.eval(scope);
      System.out.println("result:\t" + res);
      System.out.println('\n');
    if (true) { /* debug */
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
    HashMap<Integer, String> results = new HashMap<Integer, String>();
    while (crnt != null) {
      Object result = evalNode(crnt);
      results.put(crnt.id, result.toString());
      crnt = nextNode(crnt);
    }
    return results;
  }
}
