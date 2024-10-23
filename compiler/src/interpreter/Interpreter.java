package interpreter;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Interpreter {
  boolean DEBUG = true;
  
  Node[] nds;
  Edge[] eds;
  HashMap<String, Object> scope;
  Class<?>[] modules;

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
    this.modules = new Class<?>[] {
      Math.class
    };
    this.scope = new HashMap<String, Object>();
    for (Class<?> cls : modules) {
      for (Field f : cls.getDeclaredFields()) {
        scope.put(f.getName(), f);
      }
      for (Method m : cls.getDeclaredMethods()) {
        scope.put(m.getName(), m);
      }
    }
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
             c == ';' ||
             c == ',')
      return TokenType.OP;
    else if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_'))
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
      int p = Expr.getType(tokens[i]).ordinal();
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
      res.type = Expr.getType(tokens[0]);
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
    res = parse(new Token[] {tokens[minpIdx]});
    Token[] tokL = Arrays.copyOfRange(tokens, 0, minpIdx);
    Token[] tokR = Arrays.copyOfRange(tokens, minpIdx+1, tokens.length);
    Expr left = parse(tokL);
    Expr right = parse(tokR);
    if (left != null) res.args.add(left);
    if (right != null) res.args.add(right);
    return res;
  } 

  Object evalNode(Node n) {
    Token[] tokens = tokenize(n.code);
    Expr ast = parse(tokens);
    Object res = ast.eval(scope);
    if (DEBUG) { 
      System.out.println(n.code);
      for (Token t : tokens)
        System.out.printf("%s\t%s\n", t.str, t.type.name());
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
    HashMap<Integer, String> results = new HashMap<Integer, String>();
    while (crnt != null) {
      Object result = evalNode(crnt);
      String resStr = "null";
      if (result != null) resStr = result.toString();
      results.put(crnt.id, resStr);
      crnt = nextNode(crnt);
    }
    return results;
  }
}
