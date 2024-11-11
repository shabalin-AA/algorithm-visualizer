package executor.interpreter;

class Token {
  public String str;
  public TokenType type;

  public Token(TokenType type, String str) {
    this.type = type;
    this.str = str;
  }
}
