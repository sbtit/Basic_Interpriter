package main;

import lexical_analuzer.Value;
import syntax_analyzer.ExprListNode;

public class PrintFunction extends Function {

    public PrintFunction() {
    }

    public Value invoke(ExprListNode arg) throws Exception {
        System.out.println(arg.get(0).getSValue());
        return null;
    }
}
