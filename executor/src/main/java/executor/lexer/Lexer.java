package executor.lexer;

import java.util.ArrayList;

public class Lexer {

    public Token[] tokenize(String codeStr) {
        ArrayList<Token> res = new ArrayList<Token>();
        char[] code = codeStr.toCharArray();
        int b = 0, e = 0;
        TokenType bt, et;
        while (b < code.length) {
            bt = tokenType(code[b]);
            e = b;
            et = tokenType(code[e]);
            switch (bt) {
                case LEFT_PAREN:
                case RIGHT_PAREN:
                    e++;
                    break;
                case STRING:
                    et = TokenType.UNDEFINED;
                    while (et != TokenType.STRING) {
                        e++;
                        if (e == code.length) break;
                        et = tokenType(code[e]);
                    }
                    e++;
                    break;
                default:
                    while (et == bt) {
                        e++;
                        if (e == code.length) break;
                        et = tokenType(code[e]);
                    }
            }
            if (bt != TokenType.SPACE) res.add(new Token(bt, codeStr.substring(b, e)));
            b = e;
        }
        return res.toArray(new Token[res.size()]);
    }

    TokenType tokenType(char c) {
        if ((c >= '0' && c <= '9') || c == '.') return TokenType.NUM;
        else if (
            c == '=' ||
            c == '+' ||
            c == '>' ||
            c == '<' ||
            c == '-' ||
            c == '*' ||
            c == '/' ||
            c == '!' ||
            c == ';' ||
            c == ','
        ) return TokenType.OP;
        else if (
            (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c == '_')
        ) return TokenType.ID;
        else if (c == ' ' || c == '\t' || c == '\n') return TokenType.SPACE;
        else if (c == '(') return TokenType.LEFT_PAREN;
        else if (c == ')') return TokenType.RIGHT_PAREN;
        else if (c == '\"') return TokenType.STRING;
        return TokenType.UNDEFINED;
    }
}
