package syntax_analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;

public class IfBlockNode extends Node {

    Node cond;
    Node trueOper;
    Node elseOper;
    boolean isENDIF = false;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.IF
    ));

    private IfBlockNode(Environment env) {
        super(env);
        type = NodeType.IF_BLOCK;
    }

    static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new IfBlockNode(env);
    }

    public boolean parse() throws Exception {
        switch (env.getInput().peek().getType()) {
            case IF:
                isENDIF = true;

            case ELSEIF:
                env.getInput().get();
                if (CondNode.isMatch(env.getInput().peek().getType())) {
                    cond = CondNode.getHandler(env);
                    cond.parse();
                } else {
                    return false;
                }
                if (!env.getInput().expect(LexicalType.THEN)) {
                    return false;
                }
                env.getInput().get();
                break;
            default:
                return false;
        }

        if (StmtNode.isMatch(env.getInput().peek().getType())) {
            trueOper = StmtNode.getHandler(env);
            trueOper.parse();

            if (env.getInput().expect(LexicalType.ELSE)) {
                env.getInput().get();

                if (StmtNode.isMatch(env.getInput().peek().getType())) {
                    elseOper = StmtNode.getHandler(env);
                    elseOper.parse();
                } else {
                    return false;
                }
            }
            if (!env.getInput().expect(LexicalType.NL)) {
                return false;
            }
            env.getInput().get();
            return true;
        } else if (env.getInput().expect(LexicalType.NL)) {
            env.getInput().get();

            if (StmtListNode.isMatch(env.getInput().peek().getType())) {
                trueOper = StmtListNode.getHandler(env);
                trueOper.parse();
            } else {
                return false;
            }
            while (env.getInput().expect(LexicalType.NL)) {
                env.getInput().get();
            }

            if (env.getInput().expect(LexicalType.ELSEIF)) {
                elseOper = IfBlockNode.getHandler(env);
                elseOper.parse();
            } else if (env.getInput().expect(LexicalType.ELSE)) {
                env.getInput().get();
                if (!env.getInput().expect(LexicalType.NL)) {
                    return false;
                }
                skipNL();
                if (StmtListNode.isMatch(env.getInput().peek().getType())) {
                    elseOper = StmtListNode.getHandler(env);
                    elseOper.parse();
                } else {
                    return false;
                }

                while (env.getInput().expect(LexicalType.NL)) {
                    env.getInput().get();
                }
            }
            if (isENDIF) {
                if (env.getInput().expect(LexicalType.ENDIF)) {
                    env.getInput().get();
                } else {
                    return false;
                }
                if (env.getInput().expect(LexicalType.NL)) {
                    env.getInput().get();
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
        return true;
    }

    public Value getValue() throws Exception {
        if (cond.getValue().getBValue() == true) {
            trueOper.getValue();
        } else if (trueOper != null) {
            trueOper.getValue();
        }
        return null;
    }

    public String toString() {
        String str = "";
        str += String.format("IF(%s)  THEN%s", cond, trueOper);
        if (elseOper != null) {
            str += String.format("ELSE%s", elseOper);
        }
        if (isENDIF) {
            str += "ENDIF";
        }
        return str;
    }
}
