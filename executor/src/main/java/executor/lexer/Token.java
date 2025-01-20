package executor.lexer;

public class Token {

    public String str;
    public TokenType type;

    public Token(TokenType type, String str) {
        this.type = type;
        this.str = str;
    }

    public String toString() {
        return String.format("\n%s\t%s", this.str, this.type.name());
    }
}
