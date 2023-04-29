package lexical;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Scanner {
    private final static char commentBeginner = '#';
    private String source;

    private final List<String> sourceLines;
    private final List<List<Token>> allTokens = new ArrayList<>();
    private List<Token> currentLineToken = new ArrayList<>();

    private final List<Error> errors = new ArrayList<>();

    private int start = 0;
    private int current = 0;
    private char currentChar;
    private int lineNumber = 1;

    private int currentLevelOfNesting = 0;

    private static final Map<String, TokenType> keywords = Map.of(
//            "and", TokenType.AND,
//            "or", TokenType.OR,
            "for", TokenType.FOR,
            "in", TokenType.IN,
            "print", TokenType.PRINT,
            "range", TokenType.RANGE
    );

    Scanner(String source) {
        this.sourceLines = Stream.of(source.split("\n")).map(str -> str + "\n").collect(Collectors.toList());
    }

    public LexicalAnalysisResult scanTokens() {
        for (String line : sourceLines) {
            currentLineToken = new ArrayList<>();
            if (!isCommentLine(line)) {
                try {
                    currentLevelOfNesting = getCurrentLevelOfNesting(line);
                } catch (LevelNestingException e) {
                    errors.add(new Error(lineNumber, line, "Level nesting error of line "));
                }
            }
            this.source = line;
            scanToken();
            current = 0;
            allTokens.add(currentLineToken);
        }
        currentLineToken.add(new Token(TokenType.EOF, "", null, lineNumber, currentLevelOfNesting));
        return new LexicalAnalysisResult(allTokens, errors);
    }

    private boolean isCommentLine(String line) {
        return line.trim().charAt(0) == commentBeginner;
    }

    private int getCurrentLevelOfNesting(String lineNumber) throws LevelNestingException {
        int count = 0;
        for (int i = 0; i < lineNumber.length(); i++) {
            if (lineNumber.charAt(i) != ' ') {
                break;
            }
            count++;
        }
        if (count % 4 == 0)
            return count / 4;
        else throw new LevelNestingException();
    }

    private boolean isEOF() {
        return current >= source.length();
    }

    private void scanToken() {
        while (!isEOF()) {
            start = current;
            char c = getCurrentChar();
            current++;
            currentChar = c;
            switch (c) {
                case '(':
                    addToken(TokenType.LEFT_PARENTHESIS);
                    break;
                case ')':
                    addToken(TokenType.RIGHT_PARENTHESIS);
                    break;
//                case '{':
//                    addToken(TokenType.LEFT_BRACE);
//                    break;
//                case '}':
//                    addToken(TokenType.RIGHT_BRACE);
//                    break;
                case ',':
                    addToken(TokenType.COMMA);
                    break;
                case '.':
                    addToken(TokenType.DOT);
                    break;
                case '-':
                    addToken(TokenType.MINUS);
                    break;
                case '+':
                    addToken(TokenType.PLUS);
                    break;
                case ';':
                    addToken(TokenType.SEMICOLON);
                    break;
                case '*':
                    addToken(TokenType.STAR);
                    break;
                case ':':
                    addToken(TokenType.COLON);
                    break;
//            case '!': addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG); break;
                case '=':
                    addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.ASSIGNMENT);
                    break;
                case commentBeginner:
                    addToken(TokenType.COMMENT_START);
                    processComment();
                    break;
//            case '<': addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS); break;
//            case '>': addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER); break;
//            case '/':
//                if (match('/')) {
//                    // A comment goes until the end of the lineNumber.
//                    while (getCurrentChar() != '\n' && !isEOF()) current++;
//                } else {
//                    addToken(TokenType.SLASH);
//                }
//                break;
                case ' ':
                case '\r':
                case '\t':
                    break;              // Ignore whitespace.
                case '\n':
                    addToken(TokenType.NEW_LINE);
                    lineNumber++;
                    break;
                case '"':
                    string();
                    break;
                default:
                    if (isDigit(c)) {
                        processNumber();
                    } else if (isLetter(c)) {
                        processAlphabetic();
                    } else {
                        errors.add(new Error(lineNumber, source, "Unexpected character."));
                    }
            }
        }
    }

    private void processComment() {
        start = current;
        current = source.length();
        addToken(TokenType.COMMENT, source.substring(start));

    }

    private String getCurrentLine() {
        return source.substring(current, source.indexOf('\n', current));
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
        boolean isInteger = true;
        currentChar = getCurrentChar();
        while (isDigit(currentChar)) {
            current++;
            currentChar = getCurrentChar();
        }

        // Look for a fractional part.
        if (getCurrentChar() == '.' && isDigit(getCurrentCharNext())) {
            isInteger = false;
            current++;

            while (isDigit(getCurrentChar()))
                current++;
        }

        String digitText = source.substring(start, current);
        addToken(isInteger ? TokenType.NUMBER_INT : TokenType.NUMBER_DOUBLE,
                isInteger ? Integer.parseInt(digitText) : Double.parseDouble(digitText));
    }

    private char getCurrentCharNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void string() {
        while (getCurrentChar() != '"' && !isEOF()) {
            if (getCurrentChar() == '\n') lineNumber++;
            current++;
        }

        if (isEOF()) {
            System.out.println(lineNumber + "Unterminated string.");
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
        if (TokenType.NEW_LINE == type) {
            text = "\\n";
        }
        currentLineToken.add(new Token(type, text, literal, lineNumber, currentLevelOfNesting));
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

