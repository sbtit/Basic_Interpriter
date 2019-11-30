package syntax_analyzer;

import lexical_analuzer.LexicalUnit;
import lexical_analuzer.LexicalAnalyzerImpl;
import lexical_analuzer.LexicalAnalyzer;
import java.io.FileInputStream;

public class Syntax_Main {
    public static void main(String[] args) throws Exception {
        FileInputStream file = new FileInputStream("C:\\Users\\C0116133\\Desktop\\ap1-2017\\soft3\\src\\test1.bas");
        LexicalAnalyzer lex = new LexicalAnalyzerImpl(file);
        Environment env = new Environment(lex);
        LexicalUnit lu = lex.get();
        lex.unget(lu);

        Node program = ProgramNode.getHandler(lu.getType(), env);
        if (program != null && program.parse()) {
            System.out.println(program);
        } else {
            System.out.println("error");
        }
        file.close();
    }
}