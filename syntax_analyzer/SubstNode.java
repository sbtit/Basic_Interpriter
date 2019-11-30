package syntax_analyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;

public class SubstNode extends Node {
    String left;
    Node expr;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.NAME
    ));

    private SubstNode(Environment env) {
        super(env);
        type = NodeType.ASSIGN_STMT;
    }

    static boolean isMatch(LexicalType t) {
        return FIRST.contains(t);
    }

    public static SubstNode getHandler(Environment env) {
        return new SubstNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        if (env.getInput().expect(LexicalType.NAME)) {
            left = env.getInput().get().getValue().getSValue();
        } else {
            return false;
        }

        if (env.getInput().get().getType() != LexicalType.EQ) {
            return false;
        }

        if (ExprNode.isMatch(env.getInput().peek().getType())) {
            expr = ExprNode.getHandler(env);
            expr.parse();
        } else {
            return false;
        }
        return true;
    }
    
    public Value getValue() throws Exception{
        env.getVariable(left).setValue(expr.getValue()); 
	return null;
    }

    public String toString() {
        return String.format("%s[%s]", left, expr);
    }
}
