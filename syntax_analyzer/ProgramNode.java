package syntax_analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;

public class ProgramNode extends Node {

    Node child = null;

    private final static Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.IF,
            LexicalType.WHILE,
            LexicalType.DO,
            LexicalType.NAME,
            LexicalType.FOR,
            LexicalType.END
    ));

    public static Node getHandler(LexicalType t, Environment env) throws Exception {
        return StmtListNode.getHandler(env);
    }

    public boolean parse() throws Exception {
        if (StmtListNode.isMatch(env.getInput().peek(1).getType())) {
            child = StmtListNode.getHandler(env);
            child.parse();
        }

        while (env.getInput().expect(LexicalType.NL)) {
            env.getInput().get();
        }

        if (!env.getInput().expect(LexicalType.EOF)) {
            return false;
        }
        return true;
    }

    public Value getValue() throws Exception {
        if (child != null) {
            return child.getValue();
        }
        return null;
    }

    public String toString() {
        return "Program";
    }
}
