package main;

import syntax_analyzer.ProgramNode;
import syntax_analyzer.Node;
import syntax_analyzer.Environment;
import lexical_analuzer.LexicalUnit;
import lexical_analuzer.LexicalAnalyzerImpl;
import lexical_analuzer.LexicalAnalyzer;
import java.io.FileInputStream;

public class Main {
    public static void main(String[] args) throws Exception {
        FileInputStream file = new FileInputStream("test1.bas");
        LexicalAnalyzer lex = new LexicalAnalyzerImpl(file);
        Environment env = new Environment(lex);
        LexicalUnit lu = lex.get();
        lex.unget(lu);

        Node program = ProgramNode.getHandler(lu.getType(), env);
        if (program != null && program.parse()) {
            System.out.println(program);
            System.out.println(program.getValue());
        } else {
            System.out.println("error");
        }
        file.close();
    }
}