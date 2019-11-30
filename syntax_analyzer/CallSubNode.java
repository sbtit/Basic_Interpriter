package syntax_analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;

public class CallSubNode extends Node {

    String funcName;
    Node arguments;
    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.NAME
    ));

    private CallSubNode(Environment env) {
        super(env);
        type = NodeType.FUNCTION_CALL;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new CallSubNode(env);
    }

    public boolean parse() throws Exception {
        boolean isLP = false;
        funcName = env.getInput().get().getValue().getSValue();

        if (env.getInput().expect(LexicalType.LP)) {
            isLP = true;
            env.getInput().get();
        }
        if (ExprListNode.isMatch(env.getInput().peek().getType())) {
            arguments = ExprListNode.getHandler(env);
            arguments.parse();
        }
        if (isLP && !env.getInput().expect(LexicalType.RP)) {
            return false;
        }
        if (isLP && env.getInput().expect(LexicalType.RP)) {
            env.getInput().get();
        }
        return true;
    }

    public Value getValue() throws Exception {
        return env.getFunction(funcName).invoke((ExprListNode) arguments);
    }

    public String toString() {
        return "" + funcName + "[" + arguments + "]";
    }
}
