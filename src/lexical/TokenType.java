package lexical;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

public enum TokenType {
    // Single-character tokens.
    LEFT_PARENTHESIS, RIGHT_PARENTHESIS,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR, COLON,

    NEW_LINE,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    ASSIGNMENT, EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL,

    // Literals.
    IDENTIFIER, STRING, NUMBER_INT, NUMBER_DOUBLE,

    // Keywords.
//    AND,
    CLASS, ELSE, FALSE, FUN, FOR, IN, IF, NIL,
//    OR,
    RANGE,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // comment
    COMMENT_START, COMMENT,
    MATH_EXPRESSION, MATH_RESULT,
    EOF;

    public static boolean isMathOp(TokenType type){
        return type == PLUS || type == MINUS || type == STAR || type == SLASH;
    }
}