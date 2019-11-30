package syntax_analyzer;

import lexical_analuzer.Value;
import lexical_analuzer.LexicalUnit;
import lexical_analuzer.ValueType;
import lexical_analuzer.ValueImpl;
import lexical_analuzer.LexicalType;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class CondNode extends Node {

    private Node left;
    private Node right;
    private LexicalUnit operator;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.NAME,
            LexicalType.LP,
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL
    ));

    private final static Set<LexicalType> ALLOW_OPER = new HashSet<>();

    static {
        ALLOW_OPER.add(LexicalType.EQ);
        ALLOW_OPER.add(LexicalType.GT);
        ALLOW_OPER.add(LexicalType.LT);
        ALLOW_OPER.add(LexicalType.GE);
        ALLOW_OPER.add(LexicalType.LE);
        ALLOW_OPER.add(LexicalType.NE);
    }

    private CondNode(Environment env) {
        super(env);
        type = NodeType.COND;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new CondNode(env);
    }

    public boolean parse() throws Exception {
        if (ExprNode.isMatch(env.getInput().peek().getType())) {
            left = ExprNode.getHandler(env);
            try {
                left.parse();
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            return false;
        }

        if (ALLOW_OPER.contains(env.getInput().peek().getType())) {
            operator = env.getInput().get();
        } else {
            return false;
        }

        if (ExprNode.isMatch(env.getInput().peek().getType())) {
            right = ExprNode.getHandler(env);
            try {
                right.parse();
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else {
            return false;
        }
        return true;
    }

    public Value getValue() throws Exception {
        Value val1 = left.getValue();
        Value val2 = right.getValue();
        if (val1 == null && val2 == null) {
            throw new Exception("nullに対して演算");
        }

        if (val1.getType() == ValueType.STRING || val2.getType() == ValueType.STRING) {
            if (operator.getType() == LexicalType.EQ) {
                return new ValueImpl(val1.getSValue().equals(val2.getSValue()));
            } else if (operator.getType() == LexicalType.NE) {
                return new ValueImpl(!val1.getSValue().equals(val2.getSValue()));
            } else {
                throw new Exception("無効な演算子");
            }
        }

        if (operator.getType() == LexicalType.LT) {
            return new ValueImpl(val1.getDValue() < val2.getDValue());
        } else if (operator.getType() == LexicalType.LE) {
            return new ValueImpl(val1.getDValue() <= val2.getDValue());
        } else if (operator.getType() == LexicalType.GT) {
            return new ValueImpl(val1.getDValue() > val2.getDValue());
        } else if (operator.getType() == LexicalType.GE) {
            return new ValueImpl(val1.getDValue() >= val2.getDValue());
        } else if (operator.getType() == LexicalType.EQ) {
            return new ValueImpl(val1.getDValue() == val2.getDValue());
        } else if (operator.getType() == LexicalType.NE) {
            return new ValueImpl(val1.getDValue() != val2.getDValue());
        } else {
            throw new InternalError("不正な演算子");
        }
    }

    public String toString() {
        return String.format("%s[%s:%s]", operator, right, left);
    }
}
