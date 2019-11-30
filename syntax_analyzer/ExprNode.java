package syntax_analyzer;

import lexical_analuzer.ValueImpl;
import lexical_analuzer.LexicalType;
import lexical_analuzer.LexicalUnit;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lexical_analuzer.Value;
import lexical_analuzer.ValueType;

public class ExprNode extends Node {

    private Node left;
    private Node right;
    private LexicalType operator;
    private boolean isMono = false;

    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.NAME,
            LexicalType.LP,
            LexicalType.INTVAL,
            LexicalType.DOUBLEVAL,
            LexicalType.LITERAL
    ));

    private final static Map<LexicalType, String> OPER = new HashMap<>();

    static {
        OPER.put(LexicalType.ADD, "+");
        OPER.put(LexicalType.SUB, "-");
        OPER.put(LexicalType.MUL, "*");
        OPER.put(LexicalType.DIV, "/");
    }

    private ExprNode(Environment env) {
        super(env);
        type = NodeType.EXPR;
    }

    private ExprNode(Environment env, boolean b) {
        super(env);
        type = NodeType.EXPR;
        isMono = b;
    }

    ExprNode(Node left, Node right, LexicalType oper) {
        type = NodeType.EXPR;
        this.left = left;
        this.right = right;
        this.operator = oper;
    }

    public static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) {
        return new ExprNode(env);
    }

    @Override
    public boolean parse() throws Exception {
        Deque<Node> exprs = new ArrayDeque<>();
        Deque<LexicalUnit> opers = new ArrayDeque<>();

        while (true) {
            switch (env.getInput().peek().getType()) {
                case INTVAL:
                case DOUBLEVAL:
                case LITERAL:
                    exprs.add(ConstNode.getHandler(env.getInput().get().getValue()));
                    break;

                case LP:
                    env.getInput().get();
                    Node bracket = ExprNode.getHandler(env);
                    bracket.parse();
                    exprs.add(bracket);
                    if (env.getInput().expect(LexicalType.RP)) {
                        env.getInput().get();
                    } else {
                        return false;
                    }
                    break;

                case SUB:
                    env.getInput().get();
                    if (!ExprNode.isMatch(env.getInput().peek().getType())) {
                        return false;
                    }
                    Node subNode = new ExprNode(env, true);
                    subNode.parse();
                    exprs.add(subNode);
                    break;

                case NAME:
                    if (env.getInput().peek(2).getType() == LexicalType.LP) {
                        Node funcNode = CallSubNode.getHandler(env);
                        funcNode.parse();
                        exprs.add(funcNode);

                    } else {
                        exprs.add(new VariableNode(env.getInput().get().getValue().getSValue()));
                    }
                    break;
                default:
                    return false;
            }

            if (isMono) {
                left = ConstNode.getHandler(new ValueImpl(-1));
                operator = LexicalType.MUL;
                right = exprs.pollLast();
                return true;
            }

            if (OPER.containsKey(env.getInput().peek().getType())) {
                if (opers.size() > 0
                        && getOpePriority(env.getInput().peek().getType()) <= getOpePriority(opers.peekLast().getType())) {
                    Node right = exprs.pollLast();
                    Node left = exprs.pollLast();
                    LexicalType operand = opers.pollLast().getType();
                    exprs.add(new ExprNode(left, right, operand));
                    opers.add(env.getInput().get());

                } else {
                    opers.add(env.getInput().get());
                }
            } else {
                break;
            }
        }

        int nOper = opers.size();
        for (int i = 0; i < nOper; i++) {
            Node l = exprs.pollFirst();
            Node r = exprs.pollFirst();
            LexicalType ope = opers.pollFirst().getType();
            exprs.addFirst(new ExprNode(l, r, ope));
        }
        left = exprs.pollLast();
        return true;
    }

    public Value getValue() throws Exception {
        if (operator == null) {
            return left.getValue();
        }
        Value val1 = left.getValue();
        Value val2 = right.getValue();
        if (val1 == null || val2 == null) {
            throw new Exception("nullに対して演算");
        } else if (val1.getType() == ValueType.STRING || val2.getType() == ValueType.STRING) {
            if (operator == LexicalType.ADD) {
                return new ValueImpl(val1.getSValue() + val2.getSValue());
            } else {
                throw new Exception("文字列です");
            }
        }
        double result;
        if (operator == LexicalType.ADD) {
            result = val1.getDValue() + val2.getDValue();
        } else if (operator == LexicalType.SUB) {
            result = val1.getDValue() - val2.getDValue();
        } else if (operator == LexicalType.MUL) {
            result = val1.getDValue() * val2.getDValue();
        } else if (operator == LexicalType.DIV) {
            if (val2.getDValue() != 0.00) {
                result = val1.getDValue() / val2.getDValue();
            } else {
                throw new Exception("0で除算");
            }
        } else {
            throw new InternalError("不正な演算子");
        }
        if (val1.getType() == ValueType.DOUBLE || val2.getType() == ValueType.DOUBLE) {
            return new ValueImpl(result);
        } else {
            return new ValueImpl((int) result);
        }
    }

    public String toString() {
        if (operator == null) {
            return "" + left;
        } else if (isMono) {
            return "-" + right;
        } else {
            return String.format("%s[%s,%s]", OPER.get(operator), left, right);
        }
    }

    private int getOpePriority(LexicalType type) {
        switch (type) {
            case MUL:
            case DIV:
                return 2;
            case SUB:
            case ADD:
                return 1;
            default:
                return -1;
        }
    }
}
