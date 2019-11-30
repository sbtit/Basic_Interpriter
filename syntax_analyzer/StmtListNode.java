package syntax_analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;

public class StmtListNode extends Node {

    List<Node> child = new ArrayList<Node>();

    static final Set<LexicalType> FIRST = new HashSet<>(Arrays.asList(
            LexicalType.IF,
            LexicalType.DO,
            LexicalType.WHILE,
            LexicalType.NAME,
            LexicalType.FOR,
            LexicalType.END,
            LexicalType.NL
    ));

    private StmtListNode(Environment env) {
        super(env);
        type = NodeType.STMT_LIST;
    }

    static boolean isMatch(LexicalType t) {
        return FIRST.contains(t);
    }

    public static Node getHandler(Environment env) {
        return new StmtListNode(env);
    }

    public boolean parse() throws Exception {
        while (true) {
            skipNL();
            Node handler;
            if (StmtNode.isMatch(env.getInput().peek().getType())) {
                handler = StmtNode.getHandler(env);
            } else if (BlockNode.isMatch(env.getInput().peek().getType())) {
                handler = BlockNode.getHandler(env);
            } else {
                break;
            }
            handler.parse();
            child.add(handler);
        }
        return true;
    }

    public Value getValue() throws Exception {
        for (int i = 0; i < child.size(); i++) {
            child.get(i).getValue();
        }
        return null;
    }

    public String toString() {
        String str = "";
        for (Node n : child) {
            str += n.toString();
        }
        return str;
    }
}
