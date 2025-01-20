package executor.interpreter;

import executor.ExecuteHandler;
import executor.SQLiteHandler;
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
import org.json.JSONArray;
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

    Node[] nds;
    Edge[] eds;
    HashMap<String, Object> scope;
    Class<?>[] modules;
    public boolean halt;

    public Interpreter(Node[] nds, Edge[] eds, Class<?>[] modules, HashMap<String, Object> scope) {
        this.nds = nds;
        this.eds = eds;
        this.modules = modules;
        for (Class<?> cls : modules) {
            for (Field f : cls.getDeclaredFields()) scope.put(f.getName(), f);
            for (Method m : cls.getDeclaredMethods()) scope.put(m.getName(), m);
        }
        this.lexer = new Lexer();
        this.parser = new Parser();
        this.halt = false;
        this.scope = scope;
    }

    public Interpreter(JSONObject flowchart, Class<?>[] modules, HashMap<String, Object> scope) {
        this.nds = flowchartNodes(flowchart);
        this.eds = flowchartEdges(flowchart);
        this.modules = modules;
        for (Class<?> cls : modules) {
            for (Field f : cls.getDeclaredFields()) scope.put(f.getName(), f);
            for (Method m : cls.getDeclaredMethods()) scope.put(m.getName(), m);
        }
        this.lexer = new Lexer();
        this.parser = new Parser();
        this.halt = false;
        this.scope = scope;
    }

    Node[] flowchartNodes(JSONObject flowchart) {
        try {
            JSONArray nds = flowchart.getJSONArray("Nodes");
            Node[] nodes = new Node[nds.length()];
            for (int i = 0; i < nds.length(); i++) {
                nodes[i] = new Node(nds.getJSONObject(i));
            }
            return nodes;
        } catch (JSONException e) {
            logger.error("[json] Wrong flowchart json\n{}\n{}", flowchart.toString(), e.toString());
            return null;
        }
    }

    Edge[] flowchartEdges(JSONObject flowchart) {
        try {
            JSONArray eds = flowchart.getJSONArray("Edges");
            Edge[] edges = new Edge[eds.length()];
            for (int i = 0; i < eds.length(); i++) {
                edges[i] = new Edge(eds.getJSONObject(i));
            }
            return edges;
        } catch (JSONException e) {
            logger.error("[json] Wrong flowchart json\n{}\n{}", flowchart.toString(), e.toString());
            return null;
        }
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
        logger.info("[node {} code]\n{}", n.id, n.code);
        if (n.type == NodeType.SUBFLOW) {
            return evalSubflowNode(n);
        } else {
            return evalCodeNode(n);
        }
    }

    Result evalSubflowNode(Node n) {
        String flowJson = sqliteHandler.getFlowchart(n.code);
        JSONObject flow = null;
        HashMap<Integer, Result> results = null;
        try {
            flow = new JSONObject(flowJson);
            Interpreter subflowInt = new Interpreter(flow, this.modules, this.scope);
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
                res = ast.eval(scope);
                logger.info("[node {} result]\n{}", n.id, res.toString());
            }
        } catch (Exception e) {
            return new Err(e);
        }
        if (n.type == NodeType.COND) {
            scope.put("_if_value", res);
        }
        return res;
    }

    Node nextNode(Node crnt) {
        for (Edge e : eds) {
            boolean edge = (e.source == crnt.id);
            if (crnt.type == NodeType.COND) {
                Result ifResult = (Result) scope.get("_if_value");
                if (ifResult instanceof Ok) {
                    edge = edge && (e.branch == (boolean) ifResult.unwrap());
                } else {
                    return null;
                }
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
