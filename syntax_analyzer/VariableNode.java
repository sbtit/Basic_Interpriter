package syntax_analyzer;

import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class VariableNode extends Node {

    String var_name;
    static Value v;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.NAME
    ));

    public VariableNode(String name) {
        type = NodeType.VARIABLE;
        var_name = name;
    }

    public VariableNode(String name, Value v) {
        type = NodeType.VARIABLE;
        var_name = name;
        this.v = v;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public boolean parse() {
        return false;
    }

    public String toString() {
        return "" + var_name;
    }

    public void setValue(Value val) {
        this.v = val;
    }

    public Value getValue() {
        return v;
    }
}
