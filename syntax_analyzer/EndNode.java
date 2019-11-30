package syntax_analyzer;

import lexical_analuzer.LexicalType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.Value;

public class EndNode extends Node {

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.END
    ));

    private EndNode(Environment env) {
        super(env);
        type = NodeType.END;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new EndNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        if (env.getInput().peek().getType() == LexicalType.END) {
            env.getInput().get();
            return true;
        } else {
            return false;
        }
    }

    public Value getValue() {
        System.exit(0);
        return null;
    }

    @Override
    public String toString() {
        return "END";
    }
}
