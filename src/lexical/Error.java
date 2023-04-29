package lexical;

public class Error {
    private final int lineNumber;
    private final String line;
    private final String errorMsg;

    public Error(int lineNumber, String line, String errorMsg) {
        this.lineNumber = lineNumber;
        this.line = line.stripTrailing();
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "Error: " +
                "lineNumber = " + lineNumber +
                ", errorMsg = '" + errorMsg +
                ", line = '" + line + '\'';
    }
}
