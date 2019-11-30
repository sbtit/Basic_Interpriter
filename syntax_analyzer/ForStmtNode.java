package syntax_analyzer;

import lexical_analuzer.LexicalType;
import lexical_analuzer.LexicalUnit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lexical_analuzer.Value;
import lexical_analuzer.ValueImpl;

public class ForStmtNode extends Node {

    private Node subst;
    private LexicalUnit max;
    private Node operation;
    private LexicalUnit name;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.FOR
    ));

    private ForStmtNode(Environment env) {
        super(env);
        type = NodeType.FOR_STMT;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new ForStmtNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        if (!env.getInput().expect(LexicalType.FOR)) {
            return false;
        }
        env.getInput().get();

        if (SubstNode.isMatch(env.getInput().peek().getType())) {
            this.subst = SubstNode.getHandler(env);
            subst.parse();
        } else {
            return false;
        }

        if (!env.getInput().expect(LexicalType.TO)) {
            return false;
        }
        env.getInput().get();

        if (env.getInput().expect(LexicalType.INTVAL)) {
            max = env.getInput().get();
        } else {
            return false;
        }

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
        skipNL();

        if (!env.getInput().expect(LexicalType.NEXT)) {
            return false;
        }
        env.getInput().get();
        skipNL();

        if (env.getInput().peek().getType() == LexicalType.NAME) {
            name = env.getInput().get();
        } else {
            return false;
        }
        return true;
    }

    public Value getValue() throws Exception {
        subst.getValue();
        while (true) {
            if (env.getVariable(name.getValue().getSValue()).getValue().getIValue() > max.getValue().getIValue()) {
                return null;
            }
            operation.getValue();
            env.getVariable(name.getValue().getSValue()).setValue(new ExprNode(env.getVariable(name.getValue().getSValue()), ConstNode.getHandler(new ValueImpl(1)), (LexicalType.ADD)).getValue());
        }
    }

    public String toString() {
        return String.format("FOR(%s TO %s ){%s}:%s", subst, max, operation, name);
    }
}
