package lexical_analuzer;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;

public class LexicalAnalyzerImpl implements LexicalAnalyzer {
    private static final Map<String, LexicalUnit> SYMBOL = new HashMap<>();
    private static final Map<String, LexicalUnit> RESERVED = new HashMap<>();
    PushbackReader reader;
    private List<LexicalUnit> uList = new ArrayList<>();

    static {
        SYMBOL.put("=", new LexicalUnit(LexicalType.EQ));
        SYMBOL.put("<", new LexicalUnit(LexicalType.LT));
        SYMBOL.put(">", new LexicalUnit(LexicalType.GT));
        SYMBOL.put(".", new LexicalUnit(LexicalType.DOT));
        SYMBOL.put("+", new LexicalUnit(LexicalType.ADD));
        SYMBOL.put("-", new LexicalUnit(LexicalType.SUB));
        SYMBOL.put("*", new LexicalUnit(LexicalType.MUL));
        SYMBOL.put("/", new LexicalUnit(LexicalType.DIV));
        SYMBOL.put("(", new LexicalUnit(LexicalType.LP));
        SYMBOL.put(")", new LexicalUnit(LexicalType.RP));
        SYMBOL.put(",", new LexicalUnit(LexicalType.COMMA));
        SYMBOL.put("<=", new LexicalUnit(LexicalType.LE));
        SYMBOL.put("=<", new LexicalUnit(LexicalType.LE));
        SYMBOL.put("=>", new LexicalUnit(LexicalType.GE));
        SYMBOL.put(">=", new LexicalUnit(LexicalType.GE));
        SYMBOL.put("<>", new LexicalUnit(LexicalType.NE));
        SYMBOL.put("\r", new LexicalUnit(LexicalType.NL));
        SYMBOL.put("\n", new LexicalUnit(LexicalType.NL));
        RESERVED.put("IF", new LexicalUnit(LexicalType.IF));
        RESERVED.put("THEN", new LexicalUnit(LexicalType.THEN));
        RESERVED.put("ELSE", new LexicalUnit(LexicalType.ELSE));
        RESERVED.put("ELSEIF", new LexicalUnit(LexicalType.ELSEIF));
        RESERVED.put("ENDIF", new LexicalUnit(LexicalType.ENDIF));
        RESERVED.put("FOR", new LexicalUnit(LexicalType.FOR));
        RESERVED.put("FORALL", new LexicalUnit(LexicalType.FORALL));
        RESERVED.put("NEXT", new LexicalUnit(LexicalType.NEXT));
        RESERVED.put("FUNC", new LexicalUnit(LexicalType.FUNC));
        RESERVED.put("DIM", new LexicalUnit(LexicalType.DIM));
        RESERVED.put("AS", new LexicalUnit(LexicalType.AS));
        RESERVED.put("END", new LexicalUnit(LexicalType.END));
        RESERVED.put("WHILE", new LexicalUnit(LexicalType.WHILE));
        RESERVED.put("DO", new LexicalUnit(LexicalType.DO));
        RESERVED.put("UNTIL", new LexicalUnit(LexicalType.UNTIL));
        RESERVED.put("LOOP", new LexicalUnit(LexicalType.LOOP));
        RESERVED.put("TO", new LexicalUnit(LexicalType.TO));
        RESERVED.put("WEND", new LexicalUnit(LexicalType.WEND));
    }

    public LexicalAnalyzerImpl(InputStream in) {
        reader = new PushbackReader(new InputStreamReader(in));
    }

    @Override
    public LexicalUnit get() throws Exception {
        if (!uList.isEmpty()) {
            return uList.remove(uList.size() - 1);
        }

        int ci;
        do {
            ci = reader.read();
        } while (ci == ' ' || ci == '\t');
        
        if (ci == -1) {
            return new LexicalUnit(LexicalType.EOF);
        } else {
            reader.unread(ci);
        }

        if (ci == '\"') {
            return getLit();
        } else if ('0' <= ci && ci <= '9') {
            return getNum();
        } else if (('a' <= ci && ci <= 'z') || ('A' <= ci && ci <= 'Z')) {
            return getStr();
        } else if (SYMBOL.containsKey(String.valueOf((char) ci))) {
            return getSym();
        } else {
            throw new Exception("Error");
        }
    }

    private LexicalUnit getLit() throws Exception {
        String result = "";
        int ci = reader.read();

        while (true) {
            ci = reader.read();
            if (ci < 0) {
                throw new Exception("err");
            } else if (ci == '\n') {
                throw new Exception("err");
            } else if (ci == '\"') {
                break;
            } else {
                result += (char) ci;
            }
        }
        return new LexicalUnit(LexicalType.LITERAL, new ValueImpl(result));
    }

    private LexicalUnit getNum() throws Exception {
        String result = "";
        while (true) {
            int ci = reader.read();
            if ('0' <= ci && ci <= '9') {
                result += (char) ci;
            } else if (ci == -1) {
                break;
            } else {
                reader.unread(ci);
                break;
            }
        }

        return new LexicalUnit(LexicalType.INTVAL, new ValueImpl(Integer.parseInt(result)));
    }

    private LexicalUnit getStr() throws Exception {
        String result = "";
        while (true) {
            int ci = reader.read();
            if (ci < 0) {
                break;
            }
            if (('a' <= ci && ci <= 'z') || ('A' <= ci && ci <= 'Z') || ('0' <= ci && ci <= '9')) {
                result += (char) ci;
            } else {
                reader.unread(ci);
                break;
            }
        }
        
        if (RESERVED.containsKey(result)) {
            return RESERVED.get(result);
        } else {
            return new LexicalUnit(LexicalType.NAME, new ValueImpl(result));
        }
    }

    private LexicalUnit getSym() throws Exception {
        String result = "";
        int ci = 0;
        while (true) {
            ci = reader.read();
            if (ci < 0) {
                break;
            }
            if (SYMBOL.containsKey(result + (char) ci)) {
                result += (char) ci;
            } else {
                reader.unread(ci);
                break;
            }
        }
        return SYMBOL.get(result);
    }

    @Override
    public boolean expect(LexicalType type) throws Exception {
        return type == peek().getType();
    }

    @Override
    public void unget(LexicalUnit token) throws Exception {
        uList.add(token);
    }

    public LexicalUnit peek() throws Exception {
        LexicalUnit lu = get(); 
        unget(lu);
        return lu;
    }

    public LexicalUnit peek(int n) throws Exception {
        List<LexicalUnit> tmpList = new LinkedList<>();
        for (int i = 0; i < n - 1; i++) {
            tmpList.add(get());
        }
        LexicalUnit lu = get();
        unget(lu);

        for (int i = n - 2; i >= 0; i--) {
            unget(tmpList.get(i));
        }
        return lu;
    }
}
