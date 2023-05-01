package semantic;

import lexical.Token;

public class SemanticError {
    public int lineNumber;
    public Token token;
    public String msgError;

    public SemanticError(Token token, String msgError) {
        this.lineNumber = token.getLine();
        this.token = token;
        this.msgError = msgError;
    }

    @Override
    public String toString() {
        return "SemanticError{" +
                "lineNumber=" + lineNumber +
                ", token=" + token +
                ", msgError='" + msgError + '\'' +
                '}';
    }
}
