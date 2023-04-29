package ast;

import lexical.Token;
import lexical.TokenType;

public class ASTError {
    Token token;
    String msg;
    int lineNumber;

    public ASTError(Token token, String msg, int lineNumber) {
        this.token = token;
        this.msg = msg;
        this.lineNumber = lineNumber;
    }
}
