package executor.interpreter;

import executor.ExecuteHandler;
import executor.SQLiteHandler;
import executor.flowchart.*;
import executor.flowchart.node.CondNode;
import executor.flowchart.node.Node;
import executor.flowchart.node.SubflowNode;
import executor.interpreter.result.*;
import executor.lexer.Lexer;
import executor.lexer.Token;
import executor.parser.Parser;
import executor.parser.expr.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Interpreter {

    Logger logger = LoggerFactory.getLogger(executor.interpreter.Interpreter.class);

    SQLiteHandler sqliteHandler = new SQLiteHandler();
    ExecuteHandler executeHandler = new ExecuteHandler();

    Lexer lexer;
    Parser parser;

    Flowchart flow;
    public boolean halt;

    public Interpreter(Flowchart flow) {
        this.lexer = new Lexer();
        this.parser = new Parser();
        this.flow = flow;
        this.halt = false;
    }

    public HashMap<Integer, Result> eval() {
        Node crnt = firstNode();
        HashMap<Integer, Result> results = new HashMap<Integer, Result>();
        Result result = null;
        while (crnt != null && !halt) {
            result = evalNode(crnt);
            results.put(crnt.id, result);
            crnt = nextNode(crnt);
        }
        results.put(0, result);
        return results;
    }

    Node firstNode() {
        for (Node n : flow.nds) {
            boolean found = false;
            for (Edge e : flow.eds) {
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
        logger.info("[node {} code]\n{}", n.id, n.code);
        if (n instanceof SubflowNode) {
            return evalSubflowNode(n);
        } else {
            return evalCodeNode(n);
        }
    }

    Result evalSubflowNode(Node n) {
        String flowJson = sqliteHandler.getFlowchart(n.code);
        Flowchart subflow = null;
        HashMap<Integer, Result> results = null;
        try {
            subflow = new Flowchart(new JSONObject(flowJson), flow.scope, flow.modules);
            Interpreter subflowInt = new Interpreter(subflow);
            results = subflowInt.eval();
            Result res = results.get(0);
            logger.info("[flowchart {} results]\n{}", n.code, results.toString());
            logger.info("[node {} result]\n{}", n.id, res.toString());
            return res;
        } catch (JSONException e) {
            logger.error("[json] Wrong flowchart results json\n{}", e.toString());
            return new Err(e);
        }
    }

    Result evalCodeNode(Node n) {
        Result res = new Ok(null);
        try {
            if (n.code != null) {
                Token[] tokens = lexer.tokenize(n.code);
                logger.info("[node {} tokens]\n{}", n.id, Arrays.toString(tokens));
                Expr ast = parser.parse(tokens);
                logger.info("[node {} ast]   \n{}", n.id, exprToString(ast, 0));
                res = ast.eval(flow.scope);
                logger.info("[node {} result]\n{}", n.id, res.toString());
            }
        } catch (Exception e) {
            return new Err(e);
        }
        if (n instanceof CondNode) {
            flow.scope.put("_if_value", res);
        }
        return res;
    }

    Node nextNode(Node crnt) {
        for (Edge e : flow.eds) {
            boolean edge = (e.source == crnt.id);
            if (crnt instanceof CondNode) {
                Result ifResult = (Result) flow.scope.get("_if_value");
                if (ifResult instanceof Ok) {
                    edge = edge && (e.branch == (boolean) ifResult.unwrap());
                } else {
                    return null;
                }
            }
            if (edge) {
                for (Node n : flow.nds) {
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
