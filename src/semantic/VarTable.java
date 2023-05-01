package semantic;

import ast.ASTNodeType;
import ast.Node;
import lexical.Token;
import lexical.TokenType;

import java.util.ArrayList;
import java.util.List;

public class VarTable {
    public  List<Row> rows = new ArrayList<>();

    public  List<SemanticError> errors = new ArrayList<>();

    public VarTable(Node ast) {
        traverse(ast);
    }

    public void printVarTable() {
        System.out.println("Variable tables:");
        rows.forEach(System.out::println);
    }

    public void printErrors(){
        if (errors.size() > 0){
            System.out.println("There are some errors");
            errors.forEach(System.out::println);
        }else {
            System.out.println("There aren't any semantic errors");
        }
    }



    private void traverse(Node node) {
        if (node == null) {
            return;
        }
        for (Node child : node.children) {
            if (child.nodeType == ASTNodeType.LOOP_STEP) {
                continue;
            } else if (child.nodeType == ASTNodeType.ASSIGNMENT) {
                String varName = child.children.get(0).token.getLexeme();
                TokenType type = child.children.get(1).token.getType();
                String varValue = child.children.get(1).token.getLexeme();
                if (TokenType.isMathOp(type)){
                    type = TokenType.MATH_RESULT;
                    varValue = TokenType.MATH_EXPRESSION.toString();
                }
                rows.add(new Row(varName, type, varValue));
            } else if (child.nodeType == ASTNodeType.LOOP) {
                addLoopVar(child);
            }
            traverse(child);
        }
    }

    private void addLoopVar(Node child) {
//        Node loopInitNode = child.children.stream()
//                .filter(it -> it.nodeType == ASTNodeType.LOOP_INIT).findFirst().orElseThrow();
//        Node assignLoopVarNode = loopInitNode.children.get(0);
//        String varName = assignLoopVarNode.children.get(0).token.getLexeme();
//        TokenType type = assignLoopVarNode.children.get(1).token.getType();
//        String varValue = assignLoopVarNode.children.get(1).token.getLexeme();
//        rows.add(new Row(varName, type, varValue));


        Node loopConditionNode = child.children.stream().filter(it -> it.nodeType == ASTNodeType.LOOP_CONDITION)
                .findFirst().orElseThrow();
        Token borderToken = loopConditionNode.children.get(1).token;
        String borderLoop = borderToken.getLexeme();
        if (isNumber(borderLoop)) {
            long border = Long.parseLong(borderLoop);
            if (border < 0) {
                errors.add(new SemanticError(borderToken, "отрицательная граница цикла"));
            }
        } else {
            if (borderLoop.contains("\"") || borderLoop.contains("'")) {
                errors.add(new SemanticError(borderToken, "границей цикла не может быть строка"));
            } else {
                if (checkIsThereVar(borderLoop)) {
                    if (!checkIsIntVar(borderLoop)) {
                        errors.add(new SemanticError(borderToken, "граница цикла не является целым числом"));
                    }
                } else {
                    errors.add(new SemanticError(borderToken, "переменная границы цикла не объявлена"));
                }
            }
        }
    }

    private boolean checkIsIntVar(String borderLoop) {
        return rows.stream().filter(it -> it.varName.equals(borderLoop))
                .reduce((f, s) -> s).get().type == TokenType.NUMBER_INT;
    }

    private boolean checkIsThereVar(String borderLoop) {
        return rows.stream().anyMatch(it -> it.varName.equals(borderLoop));
    }

    public static boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (str.length() == 1) {
                return false;
            }
            i = 1;
        }
        boolean isDecimal = false;
        for (; i < str.length(); i++) {
            if (str.charAt(i) == '.') {
                if (isDecimal) {
                    return false;
                }
                isDecimal = true;
            } else if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                return false;
            }
        }
        return true;
    }
}
