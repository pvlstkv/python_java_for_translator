package code_generation;

import ast.ASTNodeType;
import ast.Node;
import lexical.Token;
import lexical.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Generator {
    private List<String> javaCode = new ArrayList<>();

    private int curLine = 0;

    public Generator() {
    }

    private void generate(String prefix, Node root, List<List<Token>> allTokens) {
        if (root == null) {
            return;
        }
        for (Node node : root.children) {
            curLine = node.token.getLine();
            if (node.nodeType == ASTNodeType.ASSIGNMENT) {
                String leftPart = getLeftPartOfAssignment(node);
                String rightPart = getRightPartOfAssignment(allTokens.get(curLine));
                javaCode.add(leftPart + rightPart);
            } else if (node.nodeType == ASTNodeType.LOOP) {
                Node loopVarAssign = node.children.get(0).children.get(0);
                StringBuilder forLine = new StringBuilder(prefix).append("for (");
                String initPart = getAssignmentLine(loopVarAssign);
                forLine.append(initPart).append(" ");

                Node conditionNode = node.children.get(1);
                String conditionPart = getConditionPart(conditionNode);
                forLine.append(conditionPart).append(" ");

                Node stepNode = node.children.get(2);
//                String stepPart = getStepPart(stepNode);
            }
        }
    }

    private String getRightPartOfAssignment(List<Token> tokens) {
        int assignIndex = tokens.stream().filter(t -> t.getLexeme()
                .equals("=")).findFirst().map(tokens::indexOf).orElseThrow();
        return tokens.stream().skip(assignIndex + 1).map(Token::getLexeme).collect(Collectors.joining(" "))
                + ";";
    }

//    private String getStepPart(Node stepNode) {
//
//    }


    private String getConditionPart(Node condition) {
        Node loopVar = condition.children.get(0);
        Node loopBorderNode = condition.children.get(1);
        return new StringBuilder(loopVar.token.getLexeme()).append(" < ").append(loopBorderNode.token.getLexeme())
                .append(";").toString();
    }

    private String getLeftPartOfAssignment(Node assign) {
        Node varName = assign.children.get(0);
        Node varVal = assign.children.get(1);
        String type = getVarType(varVal.token.getType());
        return new StringBuilder(type).append(" ").append(varName.token.getLexeme()).append(" ")
                .append(" = ").toString();

    }

    private String getVarType(TokenType type) {
        if (type == TokenType.NUMBER_INT) {
            return "int";
        } else if (type == TokenType.NUMBER_DOUBLE) {
            return "double";
        } else if (type == TokenType.STRING) {
            return "String";
        }
        return "Object";
    }
}
