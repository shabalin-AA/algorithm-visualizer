import java.util.HashMap;
import java.util.ArrayList;

public class Interpreter {
  Node[] nds;
  Edge[] eds;
  HashMap<String, Object> scope;

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
             c == '-' ||
             c == '>' ||
             c == '<' ||
             c == '*' ||
             c == '/')
      return TokenType.OP;
    else if (c >= 'A' && c <= 'z')
      return TokenType.ID;
    else if (c == ' ' ||
             c == '\t' ||
             c == '\n')
      return TokenType.SPACE;
    return TokenType.UNDEFINED;
  }

  class Token {
    public String str;
    public TokenType type;
  }

  ArrayList<Token> tokenize(String codeStr) {
    ArrayList<Token> res = new ArrayList<Token>();
    int b = 0, e = 0;
    char[] code = codeStr.toCharArray();
    Token crntTok = new Token();
    while (b < code.length) {
      crntTok.type = getTokenType(code[b]);
      e = b + 1;
      if (e < code.length) {
        TokenType et = getTokenType(code[e]);
        while (et == crntTok.type) {
          e++;
          if (e == code.length) break;
          et = getTokenType(code[e]);
        }
      }
      crntTok.str = codeStr.substring(b, e);
      res.add(crntTok);
      crntTok = new Token();
      b = e + 1;
    }
    return res;
  }

  void evalNode(Node n) {
    System.out.println(n.code);
    ArrayList<Token> tokens = tokenize(n.code);
    for (Token t : tokens) 
      System.out.printf("%s\t%s\n", t.str, t.type.name());
    switch (n.type) {
      case COND:
        scope.put("_if_value", true);
        break;
      default: break;
    }
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

  public void eval() {
    Node crnt = firstNode();
    while (crnt != null) {
      evalNode(crnt);
      crnt = nextNode(crnt);
    }
  }
}
