package lexical_analuzer;

public interface LexicalAnalyzer {
    public LexicalUnit get() throws Exception;
    public boolean expect(LexicalType type) throws Exception;
    public void unget(LexicalUnit token) throws Exception;    
    public LexicalUnit peek() throws Exception;
    public LexicalUnit peek(int n) throws Exception;

}
