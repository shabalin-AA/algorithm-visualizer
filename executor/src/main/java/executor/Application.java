package executor;

import org.json.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    Logger logger = LoggerFactory.getLogger(executor.Application.class);

    SQLiteHandler sqliteHandler = new SQLiteHandler();
    ExecuteHandler executeHandler = new ExecuteHandler();

    @PostMapping(
        value = "/execute",
        consumes = "application/json",
        produces = "application/json"
    )
    String execute(@RequestBody String body) {
        logger.info("[request_body]\t" + body);
        try {
            return executeHandler.executeFlowchart(new JSONObject(body));
        } catch (JSONException e) {
            logger.warn("Wrong flowchart json\n" + e.toString());
        }
        return "";
    }

    @PostMapping(value = "/save", consumes = "application/json")
    void save(@RequestBody String body) {
        try {
            sqliteHandler.insertFlowchart(new JSONObject(body), "userFlow");
        } catch (JSONException e) {
            logger.warn("Wrong flowchart json\n" + e.toString());
        }
    }

    @GetMapping(value = "/flowchart-list", produces = "application/json")
    String flowchartList() {
        return sqliteHandler.getFlowchartList();
    }

    @GetMapping("/flowchart/{id}")
    String load(@PathVariable long id) {
        return sqliteHandler.getFlowchart(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
