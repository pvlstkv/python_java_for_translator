package semantic;

import lexical.Token;
import lexical.TokenType;

public class Row {
    String varName;
    TokenType type;
    String value;

    public Row(String varName, TokenType type, String value) {
        this.varName = varName;
        this.type = type;
        this.value = value;
    }

    public Row() {
    }

    @Override
    public String toString() {
        return "Row{" +
                "varName='" + varName + '\'' +
                ", type=" + type +
                ", value='" + value + '\'' +
                '}';
    }
}
