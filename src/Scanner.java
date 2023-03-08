import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public
class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private char currentChar;
    private int line = 1;

    private static final Map<String, TokenType> keywords = Map.of(
            "and", TokenType.AND,
            "or", TokenType.OR,
            "for", TokenType.FOR,
            "print", TokenType.PRINT,
            "range", TokenType.RANGE
    );

    Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {;
        while (!isEOF()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isEOF() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = getCurrentChar();
        current++;
        currentChar = c;
        switch (c) {
            case '(': addToken(TokenType.LEFT_PARENTHESIS); break;
            case ')': addToken(TokenType.RIGHT_PARENTHESIS); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case ':':addToken(TokenType.COLON);break;
//            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
            case '=': addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL); break;
//            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
//            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
//            case '/':
//                if (match('/')) {
//                    // A comment goes until the end of the line.
//                    while (getCurrentChar() != '\n' && !isEOF()) current++;
//                } else {
//                    addToken(TokenType.SLASH);
//                }
//                break;
            case ' ':
            case '\r':
            case '\t':                // Ignore whitespace.                break;
            case '\n':  line++; break;
            case '"': string(); break;
            default:
                if (isDigit(c)) {
                    processNumber();
                } else if (isLetter(c)){
                    processAlphabetic();
                } else {
                    System.err.println(line + "Unexpected character.");
                }
        }
    }
    private void processAlphabetic() {
        while (isLetterNumeric(getCurrentChar()))
            current++;
        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) {
            type = TokenType.IDENTIFIER;
        }
        addToken(type);

    }
    private void processNumber() {
        currentChar = getCurrentChar();
        while (isDigit(currentChar)){
            current++;
            currentChar = getCurrentChar();
        }

        // Look for a fractional part.
        if (getCurrentChar() == '.' && isDigit(getCurrentCharNext())) {
            // Consume the "."
            current++;

            while (isDigit(getCurrentChar()))
                current++;
        }

        addToken(TokenType.IDENTIFIER,
                Double.parseDouble(source.substring(start, current)));
    }
    private char getCurrentCharNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
    private void string() {
        while (getCurrentChar() != '"' && !isEOF()) {
            if (getCurrentChar() == '\n') line++;
            current++;
        }

        if (isEOF()) {
            System.out.println(line + "Unterminated string.");
            return;
        }

        // The closing ".
        current++;

        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }
    private char getCurrentChar() {
        if (isEOF()) return '\0';
        return source.charAt(current);
    }

    private boolean match(char expected) {
        if (isEOF()) return false;
//        if (source.charAt(current) != expected) return false;
        if (getCurrentChar() != expected) return false;

        current++;
        return true;
    }
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isLetterNumeric(char c) {
        return isLetter(c) || isDigit(c);
    }
}