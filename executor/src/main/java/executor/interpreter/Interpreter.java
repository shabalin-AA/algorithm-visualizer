package executor.interpreter;

import executor.interpreter.expr.*;
import executor.interpreter.result.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.apache.logging.log4j.*;

// TODO arrays
// TODO calling other flowcharts

public class Interpreter {

    private static final Logger logger = LogManager.getLogger("main");

    Node[] nds;
    Edge[] eds;
    HashMap<String, Object> scope;

    public Interpreter(Node[] nds, Edge[] eds, Class<?>[] modules) {
        this.nds = nds;
        this.eds = eds;
        this.scope = new HashMap<String, Object>();
        for (Class<?> cls : modules) {
            for (Field f : cls.getDeclaredFields()) scope.put(f.getName(), f);
            for (Method m : cls.getDeclaredMethods()) scope.put(m.getName(), m);
        }
    }

    public List<Pair<Integer, Result>> eval() {
        Node crnt = firstNode();
        List<Pair<Integer, Result>> results = new ArrayList<
            Pair<Integer, Result>
        >();
        while (crnt != null) {
            Result result = evalNode(crnt);
            results.add(new Pair<Integer, Result>(crnt.id, result));
            crnt = nextNode(crnt);
        }
        return results;
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

    Result evalNode(Node n) {
        Object res = null;
        try {
            Token[] tokens = tokenize(n.code);
            if (!checkParentheses(tokens)) {
                throw new UnmatchedParenthesesException(tokens);
            }
            Expr ast = parse(tokens);
            res = ast.eval(scope);
            logger.debug(n.code);
            String tokensStr = "";
            for (Token t : tokens) {
                tokensStr += String.format("%s\t%s\n", t.str, t.type.name());
            }
            logger.debug("[tokens]\t" + tokensStr);
            logger.debug("[ast]\t" + exprToString(ast, 0));
            logger.debug("[result]\t" + res);
        } catch (Exception e) {
            return new Err(e);
        }
        switch (n.type) {
            case COND:
                scope.put("_if_value", (boolean) res);
                break;
            default:
                break;
        }
        return new Ok(res);
    }

    Node nextNode(Node crnt) {
        for (Edge e : eds) {
            boolean edge = (e.source == crnt.id);
            if (crnt.type == NodeType.COND) {
                edge = edge && (e.branch == (boolean) scope.get("_if_value"));
            }
            if (edge) {
                for (Node n : nds) {
                    if (n.id == e.target) return n;
                }
            }
        }
        return null;
    }

    Token[] tokenize(String codeStr) {
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
            if (bt != TokenType.SPACE) res.add(
                new Token(bt, codeStr.substring(b, e))
            );
            b = e;
        }
        return res.toArray(new Token[res.size()]);
    }

    Expr parse(Token[] tokens) throws UndefinedTokenException {
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

    TokenType tokenType(char c) {
        if (c >= '0' && c <= '9') return TokenType.NUM;
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
                if (
                    chars[0] == '\"' && chars[chars.length - 1] == '\"'
                ) return new StringExpr(t.str.substring(1, t.str.length() - 1));
                else return null;
            case NUM:
                for (char c : chars) {
                    if (c == '.' || (c >= '0' && c <= '9')) {} else return null;
                }
                return new NumExpr(Double.parseDouble(t.str));
            case ID:
                for (char c : chars) {
                    if (
                        c == '_' ||
                        (c >= '0' && c <= '9') ||
                        (c >= 'A' && c <= 'z')
                    ) {} else return null;
                }
                return new IdExpr(t.str);
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
        for (Field f : expr
            .getClass()
            .getSuperclass()
            .getDeclaredFields()) allFields.add(f);
        for (Field field : allFields) {
            res += "\n";
            for (int i = 0; i < depth + 1; i++) res += "  ";
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(expr);
            } catch (Exception e) {
                logger.debug("Cannot get field valuei\n" + e.toString());
            }
            res += field.getName() + ": ";
            if (value == null) res += "null";
            else if (value instanceof Expr) res += exprToString(
                (Expr) value,
                depth + 1
            );
            else res += value.toString();
        }
        return res;
    }

    class UnmatchedParenthesesException extends Exception {

        public UnmatchedParenthesesException(Token[] tokens) {
            super();
            String tokensStr = "";
            for (Token t : tokens) {
                tokensStr += String.format("%s\t%s\n", t.str, t.type.name());
            }
            System.out.println(tokensStr);
        }
    }

    class UndefinedTokenException extends Exception {

        public UndefinedTokenException(Token[] tokens) {
            super();
        }
    }
}
