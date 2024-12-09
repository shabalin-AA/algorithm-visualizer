package executor.parser;

import executor.lexer.Token;
import executor.lexer.TokenType;
import executor.parser.expr.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {

    Logger logger = LoggerFactory.getLogger(executor.parser.Parser.class);

    public Expr parse(Token[] tokens) throws UndefinedTokenException {
        if (tokens.length == 0) return null;
        if (tokens.length == 1) {
            return newExpr(tokens[0]);
        }
        if (
            tokens[0].type == TokenType.LEFT_PAREN &&
            tokens[tokens.length - 1].type == TokenType.RIGHT_PAREN
        ) {
            Token[] inner = Arrays.copyOfRange(tokens, 1, tokens.length - 1);
            if (checkParentheses(inner)) return parse(inner);
        }
        int minpIdx = minPrecIdx(tokens);
        if (minpIdx < 0) throw new UndefinedTokenException(tokens);
        Expr res = parse(new Token[] { tokens[minpIdx] });
        Token[] tokL = Arrays.copyOfRange(tokens, 0, minpIdx);
        Token[] tokR = Arrays.copyOfRange(tokens, minpIdx + 1, tokens.length);
        Expr left = parse(tokL);
        Expr right = parse(tokR);
        if (left != null) res.add(left);
        if (right != null) res.add(right);
        return res;
    }

    Expr newExpr(Token t) {
        char[] chars = t.str.toCharArray();
        switch (t.type) {
            case OP:
                if (t.str.equals(";")) return new NextExpr();
                if (t.str.equals("=")) return new AssignExpr();
                if (t.str.equals(">")) return new GtExpr();
                if (t.str.equals("<")) return new LsExpr();
                if (t.str.equals("+")) return new AddExpr();
                if (t.str.equals("-")) return new SubExpr();
                if (t.str.equals("*")) return new MulExpr();
                if (t.str.equals("/")) return new DivExpr();
                if (t.str.equals("==")) return new EqExpr();
                if (t.str.equals("!=")) return new NotEqExpr();
                if (t.str.equals(",")) return new ListExpr();
                break;
            case STRING:
                if (chars[0] == '\"' && chars[chars.length - 1] == '\"') return new StringExpr(
                    t.str.substring(1, t.str.length() - 1)
                );
                else return null;
            case NUM:
                for (char c : chars) {
                    if (c == '.' || (c >= '0' && c <= '9')) {} else return null;
                }
                return new NumExpr(Double.parseDouble(t.str));
            case ID:
                for (char c : chars) {
                    if (
                        c == '_' || (c >= '0' && c <= '9') || (c >= 'A' && c <= 'z')
                    ) {} else return null;
                }
                return new IdExpr(t.str);
            default:
                break;
        }
        return null;
    }

    int minPrecIdx(Token[] tokens) {
        int minPrecedence = Integer.MAX_VALUE;
        int idx = -1;
        for (int i = 0; i < tokens.length; i++) {
            TokenType tt = tokens[i].type;
            if (tt == TokenType.LEFT_PAREN) {
                int paren = 1;
                while (paren != 0) {
                    i++;
                    switch (tokens[i].type) {
                        case LEFT_PAREN:
                            paren++;
                            break;
                        case RIGHT_PAREN:
                            paren--;
                            break;
                        default:
                            break;
                    }
                }
                i++;
                if (i >= tokens.length) break;
            }
            int p = precedence(newExpr(tokens[i]));
            if (p < minPrecedence) {
                minPrecedence = p;
                idx = i;
            }
        }
        return idx;
    }

    static int precedence(Expr expr) {
        if (expr instanceof NextExpr) return 0;
        if (expr instanceof ListExpr) return 1;
        if (expr instanceof AssignExpr) return 2;
        if (expr instanceof EqExpr) return 3;
        if (expr instanceof NotEqExpr) return 3;
        if (expr instanceof LsExpr) return 4;
        if (expr instanceof GtExpr) return 4;
        if (expr instanceof AddExpr) return 5;
        if (expr instanceof SubExpr) return 5;
        if (expr instanceof MulExpr) return 6;
        if (expr instanceof DivExpr) return 7;
        if (expr instanceof StringExpr) return 8;
        if (expr instanceof NumExpr) return 8;
        if (expr instanceof IdExpr) return 9;
        return Integer.MAX_VALUE;
    }

    boolean checkParentheses(Token[] tokens) {
        int paren = 0;
        for (int i = 0; i < tokens.length; i++) {
            switch (tokens[i].type) {
                case LEFT_PAREN:
                    paren++;
                    break;
                case RIGHT_PAREN:
                    paren--;
                    break;
                default:
                    break;
            }
            if (paren < 0) return false;
        }
        return paren == 0;
    }

    String exprToString(Expr expr, int depth) {
        String res = "";
        res += expr.getClass().getName();
        List<Field> allFields = new ArrayList<Field>();
        for (Field f : expr.getClass().getDeclaredFields()) allFields.add(f);
        for (Field f : expr.getClass().getSuperclass().getDeclaredFields()) allFields.add(f);
        for (Field field : allFields) {
            res += "\n";
            for (int i = 0; i < depth + 1; i++) res += "  ";
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(expr);
            } catch (Exception e) {
                logger.warn("Cannot get field value\n{}", e.toString());
            }
            res += field.getName() + ": ";
            if (value == null) res += "null";
            else if (value instanceof Expr) res += exprToString((Expr) value, depth + 1);
            else res += value.toString();
        }
        return res;
    }

    class UnmatchedParenthesesException extends Exception {

        public UnmatchedParenthesesException(Token[] tokens) {
            super();
            logger.warn("[UnmatchedParenthesesException] [tokens]\n{}", Arrays.toString(tokens));
        }
    }

    class UndefinedTokenException extends Exception {

        public UndefinedTokenException(Token[] tokens) {
            super();
            logger.warn("[UndefinedTokenException] [tokens]\n{}", Arrays.toString(tokens));
        }
    }
}
