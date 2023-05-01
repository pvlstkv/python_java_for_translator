package code_generation;

import ast.ASTNodeType;
import ast.Node;
import lexical.Token;
import lexical.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Generator {
    private List<String> javaCode = new ArrayList<>();

    private int curLine = 0;

    private List<List<Token>> allTokens;
    String className = "Translated";
    public Generator(List<List<Token>> allTokens) {
        this.allTokens = allTokens;
    }

    public Generator() {
    }

    public List<String> translate(Node root) {
        this.javaCode.add("public class " + this.className + " {");
        String prefix = " ".repeat(4);
        javaCode.add(prefix + "public static void main(String[] args) {");
        this.generate(prefix + prefix, root);
        javaCode.add(prefix + "}");
        javaCode.add("}");
        return javaCode;
    }

    private void generate(String prefix, Node root) {
        if (root == null) {
            return;
        }
        for (Node node : root.children) {
            curLine = node.getLineNumber();
            if (node.nodeType == ASTNodeType.ASSIGNMENT) {
                String leftPart = getLeftPartOfAssignment(node);
                String rightPart = getRightPartOfAssignment(allTokens.get(curLine - 1));
                javaCode.add(prefix + leftPart + rightPart);
            } else if (node.nodeType == ASTNodeType.LOOP) {
                Node loopVarAssign = node.children.get(0).children.get(0);
                StringBuilder forLine = new StringBuilder(prefix).append("for (");
                String initPart = getLoopAssignmentPart(loopVarAssign);
                forLine.append(initPart).append(" ");

                Node conditionNode = node.children.get(1);
                String conditionPart = getConditionPart(conditionNode);
                forLine.append(conditionPart).append(" ");

                Node stepNode = node.children.get(2);
                String stepPart = getStepPart(stepNode);
                forLine.append(stepPart).append("){");
                javaCode.add(forLine.toString());
                generate(prefix + "    ", node.children.get(3));
                javaCode.add(prefix + "}");
            } else if (node.nodeType == ASTNodeType.COMMENT) {
                javaCode.add(prefix + "// " + node.token.getLexeme().trim());
            }
        }
    }

    public void printJavaCode() {
        this.javaCode.forEach(System.out::println);
    }


    private String getLoopAssignmentPart(Node loopVarAssign) {
        String leftPart = getLeftPartOfAssignment(loopVarAssign);
        String rightPart = loopVarAssign.children.get(1).token.getLexeme();
        return leftPart + rightPart + ";";
    }

    private String getRightPartOfAssignment(List<Token> tokens) {
        int assignIndex = tokens.stream().filter(t -> t.getLexeme()
                .equals("=")).findFirst().map(tokens::indexOf).orElseThrow();
        return tokens.subList(assignIndex + 1, tokens.size() - 1).stream()
                .map(Token::getLexeme).collect(Collectors.joining(" ")) + ";";
    }

    private String getStepPart(Node stepNode) {
        return stepNode.children.get(0).children.get(0).token.getLexeme() + "++";
    }


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
        return new StringBuilder(type).append(" ").append(varName.token.getLexeme()).append(" = ").toString();

    }

    private String getVarType(TokenType type) {
        if (type == TokenType.NUMBER_INT) {
            return "int";
        } else if (type == TokenType.NUMBER_DOUBLE || TokenType.isMathOp(type)) {
            return "double";
        } else if (type == TokenType.STRING) {
            return "String";
        }
        return "Object";
    }
}
