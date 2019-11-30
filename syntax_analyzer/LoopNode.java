package syntax_analyzer;

import lexical_analuzer.LexicalType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.Value;

public class LoopNode extends Node {

    private Node cond;
    private Node operation;
    private boolean isDo;
    private boolean isCond;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.DO,
            LexicalType.WHILE
    ));

    private LoopNode(Environment env) {
        super(env);
        type = NodeType.LOOP_BLOCK;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new LoopNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        if (env.getInput().expect(LexicalType.WHILE)) {
            isDo = false;
            condHandler();
            stmtListHandler();

            skipNL();
            if (!env.getInput().expect(LexicalType.WEND)) {
                return false;
            }
            env.getInput().get();
            if (!env.getInput().expect(LexicalType.NL)) {
                return false;
            }
            env.getInput().get();

        } else if (env.getInput().peek().getType() == LexicalType.DO) {
            env.getInput().get();
            switch (env.getInput().peek().getType()) {
                case NL:
                    isDo = true;
                    stmtListHandler();
                    if (!env.getInput().expect(LexicalType.LOOP)) {
                        return false;
                    }
                    env.getInput().get();

                    condHandler();
                    if (!env.getInput().expect(LexicalType.NL)) {
                        return false;
                    }
                    skipNL();
                    break;

                case WHILE:
                case UNTIL:
                    isDo = false;
                    condHandler();
                    stmtListHandler();

                    if (!env.getInput().expect(LexicalType.LOOP)) {
                        return false;
                    }
                    env.getInput().get();

                    if (!env.getInput().expect(LexicalType.NL)) {
                        return false;
                    }
                    skipNL();

                    break;
                default:
                    return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean condHandler() throws Exception {
        if (env.getInput().expect(LexicalType.WHILE)) {
            isCond = true;
        } else if (env.getInput().expect(LexicalType.UNTIL)) {
            isCond = true;
        } else {
            return false;
        }
        env.getInput().get();

        if (CondNode.isMatch(env.getInput().peek().getType())) {
            cond = CondNode.getHandler(env);
            cond.parse();
        } else {
            return false;
        }
        return true;
    }

    private boolean stmtListHandler() throws Exception {
        if (!env.getInput().expect(LexicalType.NL)) {
            return false;
        }
        skipNL();
        if (StmtListNode.isMatch(env.getInput().peek().getType())) {
            operation = StmtListNode.getHandler(env);
            operation.parse();
        } else {
            return false;
        }
        return true;
    }

    public Value getValue() throws Exception {
        if (isDo) {
            operation.getValue();
        }

        while (true) {
            if (!judge()) {
                return null;
            }
            operation.getValue();
        }
    }

    private boolean judge() throws Exception {
        if ((cond.getValue().getBValue() == true && isCond == false)
                || (cond.getValue().getBValue() == false && isCond == true)) {
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        if (isDo) {
            return String.format("%s[%s%s[%s]]", isDo ? "DO" : "LOOP", operation,
                    isCond ? "!" : "", cond);
        } else {
            try {
                return String.format("%s[%s%s[%s]]", isDo ? "DO" : "LOOP", isCond ? "!" : "",
                        cond, operation);
            } catch (Exception e) {
                return "Error";
            }
        }
    }
}
