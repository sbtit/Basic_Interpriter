package syntax_analyzer;

import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExprListNode extends Node {

    List<Node> child = new ArrayList<Node>();

    static final Set<LexicalType> FIRST = new HashSet<>(Arrays.asList(
            LexicalType.NAME,
            LexicalType.SUB,
            LexicalType.LP,
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL
    ));

    private ExprListNode(Environment env) {
        super(env);
        type = NodeType.EXPR_LIST;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new ExprListNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        while (true) {
            if (ExprNode.isMatch(env.getInput().peek().getType())) {
                Node expr = ExprNode.getHandler(env);
                expr.parse();
                child.add(expr);
            } else {
                return false;
            }

            if (env.getInput().expect(LexicalType.COMMA)) {
                env.getInput().get();
                continue;
            } else {
                break;
            }
        }

        return true;
    }

    public Value get(int i) throws Exception {
        if (child.size() > i) {
            return child.get(i).getValue();
        } else {
            return null;
        }
    }

    public String toString() {
        String str = "";
        for (int i = 0; i < child.size() - 1; i++) {
            str += child.get(i) + ",";
        }
        str += child.get(child.size() - 1);
        return str;
    }
}
