package ast;

public enum ForKWPosition {
    KW_LOOPVAR_POSITION(1),
    KW_IN_POSITION(2),
    KW_RANGE_POSITION(3),
    KW_LEFT_RANGE_BRACE(4),
    KW_RIGHT_RANGE_BRACE(6),
    KW_COLON_POSITION(7);

    private final int value;

    ForKWPosition(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}