package lexical;

public class Token {
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;

    private final int levelNesting;




    public Token(TokenType type, String lexeme, Object literal, int line, int levelNesting) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
        this.levelNesting = levelNesting;
    }

    public String toString() {
        return type + " '" + lexeme + "' level nesting: " + levelNesting ;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLine() {
        return line;
    }

    public int getLevelNesting() {
        return levelNesting;
    }
}