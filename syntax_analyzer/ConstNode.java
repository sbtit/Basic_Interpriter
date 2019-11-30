package syntax_analyzer;

import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConstNode extends Node {

    private Value value;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL,
            LexicalType.SUB
    ));

    private ConstNode(Value v) {
        value = v;
        switch (v.getType()) {
            case INTEGER:
                type = NodeType.INT_CONSTANT;
                break;
            case DOUBLE:
                type = NodeType.DOUBLE_CONSTANT;
                break;
            case STRING:
                type = NodeType.STRING_CONSTANT;
                break;
            default:
                break;
        }
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Value v) {
        return new ConstNode(v);
    }

    @Override
    public boolean parse() throws Exception {
        return false;
    }

    public Value getValue() {
        return value;
    }

    public String toString() {
        return "" + value;
    }
}
