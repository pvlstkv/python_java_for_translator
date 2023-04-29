package ast;

import lexical.Token;
import lexical.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Tree {

    //    private static int[] loopKW = {KW_LOOPVAR_POSITION,}
    private static final int KW_LOOPVAR_POSITION = 1;
    private static final int KW_IN_POSITION = 2;
    private static final int KW_RANGE_POSITION = 3;
    private static final int KW_LEFT_RANGE_BRACE = 4;
    private static final int KW_RIGHT_RANGE_BRACE = 5;
    private static final int KW_COLON_POSITION = 6;
    private static final int RIGHT_PART_POSITION = 2;


    List<Node> nodes;

    public Tree(List<List<Token>> allTokens) {
        makeAST(allTokens);
    }

    private void makeAST(List<List<Token>> allTokens) {

        List<ASTError> errors = new ArrayList<>();
        Node wholeCode = new Node();
        for (List<Token> onelineTokens : allTokens) {

            Token t = onelineTokens.get(0);
            if (t.getType() == TokenType.IDENTIFIER) {
                Node root = null;
                try {
                    root = getAssignmentNode(onelineTokens, 1); //new Node(ASTNodeType.ASSIGNMENT, onelineTokens.get(1))
                } catch (NoAssignmentTokenException e) {
                    errors.add(new ASTError(onelineTokens.get(1), "Нет оператора присаивания", onelineTokens.get(1).getLine()));
                    continue;
                }

                wholeCode.children = new ArrayList<>(List.of(root));
                Node id = new Node(ASTNodeType.IDENTIFIER, t);
                Node right = getRightSubTree(onelineTokens, RIGHT_PART_POSITION);
                root.children = new ArrayList<>(Arrays.asList(id, right));
            } else if (t.getType() == TokenType.FOR) {
                Node root = new Node(ASTNodeType.LOOP);
                Token curToken = onelineTokens.get(KW_LOOPVAR_POSITION);
                Token loopVarToken = curToken;
                if (curToken.getType() == TokenType.IDENTIFIER) {
                    Node initionLoop = new Node(ASTNodeType.LOOP_INIT);

                    Node initAssign = new Node(ASTNodeType.ASSIGNMENT);
                    initAssign.children.add(new Node(ASTNodeType.IDENTIFIER,
                            curToken));
                    initAssign.children.add(new Node(ASTNodeType.IDENTIFIER,
                            new Token(TokenType.NUMBER_INT, "0", "0", curToken.getLine(), curToken.getLevelNesting())));

                    initionLoop.children.add(initAssign);

                    curToken = onelineTokens.get(KW_IN_POSITION);
                    if (curToken.getType() == TokenType.IN) {
                        curToken = onelineTokens.get(KW_RANGE_POSITION);
                        if (curToken.getType() == TokenType.RANGE) {
                            curToken = onelineTokens.get(KW_LEFT_RANGE_BRACE);
                            if (curToken.getType() == TokenType.LEFT_BRACE) {
                                Node conditionLoop = new Node(ASTNodeType.LOOP_CONDITION);
                                conditionLoop.children.add(new Node(ASTNodeType.IDENTIFIER, loopVarToken));
                                conditionLoop.children.add(new Node(ASTNodeType.IDENTIFIER, curToken));
                                initionLoop.children.add(conditionLoop);
                                curToken = onelineTokens.get(KW_RIGHT_RANGE_BRACE);
                                if (curToken.getType() == TokenType.RIGHT_BRACE) {
                                    curToken = onelineTokens.get(KW_COLON_POSITION);
                                    if (curToken.getType() == TokenType.COLON) {
                                        Node stepLoopNode = new Node(ASTNodeType.LOOP_STEP);
                                        Node assignStepNode = new Node(ASTNodeType.ASSIGNMENT);
                                        Node plusOneNode = new Node(ASTNodeType.BINOP);
                                        plusOneNode.children.add(new Node(ASTNodeType.IDENTIFIER, loopVarToken));
                                        plusOneNode.children.add(new Node(ASTNodeType.IDENTIFIER,
                                                new Token(TokenType.IDENTIFIER, "1", "1", loopVarToken.getLine(), loopVarToken.getLevelNesting())));
                                        assignStepNode.children.add(new Node(ASTNodeType.IDENTIFIER, loopVarToken));
                                        assignStepNode.children.add(plusOneNode);
                                        stepLoopNode.children.add(assignStepNode);
                                    } else {
                                        errors.add(new ASTError(curToken, "нет правой скобки у оператора range в цикле", curToken.getLine()));
                                    }
                                } else {
                                    errors.add(new ASTError(curToken, "нет правой скобки у оператора range в цикле", curToken.getLine()));
                                }
                            } else {
                                errors.add(new ASTError(curToken, "нет левой скобки у оператора range в цикле", curToken.getLine()));
                            }
                        } else {
                            errors.add(new ASTError(curToken, "нет оператора range в цикле", curToken.getLine()));
                        }
                    } else {
                        errors.add(new ASTError(curToken, "нет оператора in в цикле", curToken.getLine()));
                    }
                } else {
                    errors.add(new ASTError(onelineTokens.get(1), "после ключевого слова for должен быть идентификатор", onelineTokens.get(1).getLine()));
                }
            }

        }
        System.out.println(wholeCode);
    }

    private Node getAssignmentNode(List<Token> onelineTokens, int i) throws NoAssignmentTokenException {
        Token t = onelineTokens.get(i);
        if (t.getType().equals(TokenType.ASSIGNMENT))
            return new Node(ASTNodeType.ASSIGNMENT, onelineTokens.get(1));
        else throw new NoAssignmentTokenException();
    }

    private Node getRightSubTree(List<Token> onelineTokens, int i) {
        if (i >= onelineTokens.size()) {
            return null;
        }
        Node left = null;
        if (onelineTokens.size() > i + 1) {
            left = new Node(ASTNodeType.IDENTIFIER, onelineTokens.get(i++));
        }
        Token arithmetic = getNextArithmetic(onelineTokens, i++);
        if (arithmetic == null)
            return left;
        Node root = new Node(ASTNodeType.BINOP, arithmetic);
        Node right = getRightSubTree(onelineTokens, i);
        root.children = new ArrayList<>(Arrays.asList(left, right));
        return root;
    }

    private Token getNextArithmetic(List<Token> onelineTokens, int i) {
        for (; i < onelineTokens.size(); ++i) {
            Token token = onelineTokens.get(i);
            var arithmetic = new HashSet<>(Arrays.asList(TokenType.PLUS, TokenType.MINUS, TokenType.STAR, TokenType.SLASH));
            if (arithmetic.contains(token.getType())) {
                return token;
            }
        }
        return null;
    }
}
