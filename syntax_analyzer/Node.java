package syntax_analyzer;

import lexical_analuzer.LexicalType;
import lexical_analuzer.Value;

public class Node {

    NodeType type;
    Environment env;

    public Node() {
    }

    public Node(NodeType my_type) {
        type = my_type;
    }

    public Node(Environment my_env) {
        env = my_env;
    }

    public NodeType getType() {
        return type;
    }

    public boolean parse() throws Exception {
        return true;
    }

    public Value getValue() throws Exception {
        return null;
    }

    public String toString() {
        if (type == NodeType.END) {
            return "END";
        } else {
            return "Node";
        }
    }

    protected void skipNL() {
        if (env != null) {
            while (true) {
                try {
                    if (env.getInput().expect(LexicalType.NL)) {
                        env.getInput().get();
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }
}
