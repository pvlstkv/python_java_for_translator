package lexical;

import java.util.List;

public class LexicalAnalysisResult {
    private final List<List<Token>> tokens;
    private final List<Error> errors;

    public LexicalAnalysisResult(List<List<Token>> tokens, List<Error> errors) {
        this.tokens = tokens;
        this.errors = errors;
    }

    public List<List<Token>> getTokens() {
        return tokens;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
