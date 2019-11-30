package syntax_analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.LexicalType;

public class StmtNode extends Node {
    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.FOR,
            LexicalType.END, 
            LexicalType.NAME
    ));

    private StmtNode(Environment env) {
        super(env);
        type = NodeType.STMT;
    }

    static boolean isMatch(LexicalType t) {
        return FIRST.contains(t);
    }

    public static Node getHandler(Environment env) throws Exception {
        switch (env.getInput().peek().getType()) {
            case NAME:
                if (env.getInput().peek(2).getType() == LexicalType.EQ) {
                    return SubstNode.getHandler(env);
                }
                if (ExprListNode.isMatch(env.getInput().peek(2).getType())) {
                    return CallSubNode.getHandler(env);
                }
                throw new Exception("構文エラー");
            case FOR:
                return ForStmtNode.getHandler(env);
            case END:
                return EndNode.getHandler(env);
            default:
                throw new Exception("不適切な型" );
        }
    }

    public String toString() {
        return "Stmt";
    }
}
