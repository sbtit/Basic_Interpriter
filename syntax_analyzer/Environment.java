package syntax_analyzer;

import main.Function;
import main.PrintFunction;
import lexical_analuzer.LexicalAnalyzer;
import java.util.Hashtable;

public class Environment {

    LexicalAnalyzer input;
    Hashtable lib;
    Hashtable<String, VariableNode> var_table;

    public Environment(LexicalAnalyzer my_input) {
        input = my_input;
        lib = new Hashtable<>();
        lib.put("PRINT", new PrintFunction());
        var_table = new Hashtable();
    }

    public LexicalAnalyzer getInput() {
        return input;
    }

    public Function getFunction(String fname) {
        return (Function) lib.get(fname);
    }

    public VariableNode getVariable(String vname) {
        VariableNode v;
        v = (VariableNode) var_table.get(vname);
        if (v == null) {
            v = new VariableNode(vname);
            var_table.put(vname, v);
        }
        return v;
    }
}
