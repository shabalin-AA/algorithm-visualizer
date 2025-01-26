package executor.codegen;

import executor.flowchart.Flowchart;
import executor.lexer.Lexer;
import executor.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CodeGenerator {

    Logger logger = LoggerFactory.getLogger(executor.codegen.CodeGenerator.class);

    Flowchart flow;
    Lexer lexer;
    Parser parser;

    public CodeGenerator(Flowchart flow) {
        this.flow = flow;
        this.lexer = new Lexer();
        this.parser = new Parser();
    }
}
