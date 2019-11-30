package syntax_analyzer;

import lexical_analuzer.Value;
import lexical_analuzer.LexicalType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlockNode extends Node {
    static final Set<LexicalType> FIRST = new HashSet<LexicalType>(Arrays.asList(
            LexicalType.IF,
            LexicalType.DO,
            LexicalType.WHILE
    ));


    static boolean isMatch(LexicalType type) {
        return FIRST.contains(type);
    }

    public static Node getHandler(Environment env) throws Exception {
        if (IfBlockNode.isMatch(env.getInput().peek().getType())) {
            return IfBlockNode.getHandler(env);
        } else if (LoopNode.isMatch(env.getInput().peek().getType())) {
            return LoopNode.getHandler(env);
        } else {
            throw new Exception("不適切な型です");
        }
    }

    public boolean parse() throws Exception {
        throw new Exception("parseは実行できません。");
    }

    public String toString() {
        return "BlockNode";
    }
    
    public Value getValue(String vname){
        return null;
    }
}
