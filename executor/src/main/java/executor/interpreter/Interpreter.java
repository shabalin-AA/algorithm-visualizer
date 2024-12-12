package executor.interpreter;

import executor.interpreter.result.*;
import executor.lexer.Lexer;
import executor.lexer.Token;
import executor.parser.Parser;
import executor.parser.expr.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO arrays
// TODO calling other flowcharts

public class Interpreter {

    Logger logger = LoggerFactory.getLogger(executor.interpreter.Interpreter.class);

    Lexer lexer;
    Parser parser;

    Node[] nds;
    Edge[] eds;
    HashMap<String, Object> scope;

    public boolean halt;

    public Interpreter(Node[] nds, Edge[] eds, Class<?>[] modules) {
        this.nds = nds;
        this.eds = eds;
        this.scope = new HashMap<String, Object>();
        for (Class<?> cls : modules) {
            for (Field f : cls.getDeclaredFields()) scope.put(f.getName(), f);
            for (Method m : cls.getDeclaredMethods()) scope.put(m.getName(), m);
        }
        this.lexer = new Lexer();
        this.parser = new Parser();
        this.halt = false;
    }

    public List<Pair<Integer, Result>> eval() {
        Node crnt = firstNode();
        List<Pair<Integer, Result>> results = new ArrayList<Pair<Integer, Result>>();
        while (crnt != null && !halt) {
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
            Token[] tokens = lexer.tokenize(n.code);
            Expr ast = parser.parse(tokens);
            res = ast.eval(scope);
            logger.info("[node {} code]\n{}", n.id, n.code);
            logger.info("[node {} tokens]\n{}", n.id, Arrays.toString(tokens));
            logger.info("[node {} ast]\n{}", n.id, exprToString(ast, 0));
            logger.info("[node {} result]\n{}", n.id, res.toString());
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
}
