package executor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.json.*;
import executor.interpreter.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;


@SpringBootApplication
@RestController
public class Application {
    SQLiteHandler sqlite = new SQLiteHandler();

    @PostMapping(
        value = "/execute",
        consumes = "application/json",
        produces = "application/json"
    )
    String execute(@RequestBody String body) {
        JSONObject jo = new JSONObject(body);
        System.out.println(jo);
        JSONArray nds = jo.getJSONArray("Nodes");
        Node[] nodes = new Node[nds.length()];
        for (int i = 0; i < nds.length(); i++) {
            nodes[i] = new Node(nds.getJSONObject(i));
        }
        JSONArray eds = jo.getJSONArray("Edges");
        Edge[] edges = new Edge[eds.length()];
        for (int i = 0; i < eds.length(); i++) {
            edges[i] = new Edge(eds.getJSONObject(i));
        }
        //TODO: make modules not hardcoded
        Class<?>[] modules = new Class<?>[] {
            Math.class
        };
        HashMap<Integer, String> results = (new Interpreter(nodes, edges, modules)).eval();
        JSONObject response = new JSONObject(results);
        return response.toString();
    }

    @PostMapping(
        value = "/save",
        consumes = "application/json"
    )
    void save(@RequestBody String body) {
        sqlite.insertFlowchart(new JSONObject(body));
    }

    @GetMapping("/flowchart/{id}")
    String load(@PathVariable long id) {
        return sqlite.getFlowchart(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
