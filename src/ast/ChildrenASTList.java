package ast;

import java.util.ArrayList;

public class ChildrenASTList<T> extends ArrayList<T> {

    public String toString(int spaceCount) {
        return "".repeat(spaceCount) + this.toString();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
