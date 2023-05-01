package ast;

import lexical.Token;
import lexical.TokenType;

import java.util.*;

import static ast.ForKWPosition.*;

public class Tree {

    private static final int RIGHT_PART_POSITION = 2;
    List<Node> nodes;

    private static int handlingLineNumber = 0;
    private static int curLvlNesting = 0;
    private List<List<Token>> allTokens;

    List<ASTError> errors = new ArrayList<>();


    public Tree(List<List<Token>> allTokens) {
        this.allTokens = allTokens;
    }

    public Node buildAST() throws Exception {
        try {
            return makeAST(allTokens);
        } catch (Exception e) {
            System.out.println(e);
            throw new Exception("Cannot create AST");
        }
    }

    private Node makeAST(List<List<Token>> allTokens) throws Exception {
        Node wholeCode = new Node();
        while (handlingLineNumber < allTokens.size()) {
            List<Token> onelineTokens = allTokens.get(handlingLineNumber);
            Token t = onelineTokens.get(0);
            if (t.getLevelNesting() < curLvlNesting) {
                curLvlNesting--;
                return wholeCode;
            }
            if (t.getType() == TokenType.IDENTIFIER) {
                wholeCode.children.add(handleAssignment(errors, wholeCode, onelineTokens, t));
                handlingLineNumber++;
            } else if (t.getType() == TokenType.FOR) {
                Node forNode = handleForLoop(errors, onelineTokens);
                wholeCode.children.add(forNode);
                if (handlingLineNumber + 1 < allTokens.size()) {
                    curLvlNesting = allTokens.get(handlingLineNumber).get(0).getLevelNesting();
                    handlingLineNumber++;
                    List<List<Token>> nextLine = Collections.singletonList(allTokens.get(handlingLineNumber));
                    int nextLineLvlNesting = nextLine.get(0).get(0).getLevelNesting();
                    if (nextLineLvlNesting == curLvlNesting + 1) {
                        curLvlNesting++;
                        Node forBodyNode = makeAST((allTokens));
                        forBodyNode.nodeType = ASTNodeType.LOOP_BODY;
                        forNode.children.add(forBodyNode);
                    } else throw new Exception("incorrect nesting level at " + nextLine.get(0).get(0).getLine());
                }

            } else if (t.getType() == TokenType.COMMENT_START) {
                Token commentToken = onelineTokens.get(1);
                handlingLineNumber++;
                wholeCode.children.add(new Node(ASTNodeType.COMMENT, commentToken));
            }

        }
        return wholeCode;
    }

    private Node handleForLoop(List<ASTError> errors, List<Token> onelineTokens) {
        Token curToken = onelineTokens.get(KW_LOOPVAR_POSITION.getValue());
        Token loopVarToken = curToken;
        Node rootNode = new Node(ASTNodeType.LOOP, curToken.getLine());
        if (curToken.getType() == TokenType.IDENTIFIER) {
            Node initionLoopNode = new Node(ASTNodeType.LOOP_INIT);

            Node initAssign = new Node(ASTNodeType.ASSIGNMENT);
            initAssign.children.add(new Node(ASTNodeType.IDENTIFIER,
                    curToken));
            initAssign.children.add(new Node(ASTNodeType.IDENTIFIER,
                    new Token(TokenType.NUMBER_INT, "0", "0", curToken.getLine(), curToken.getLevelNesting())));

            initionLoopNode.children.add(initAssign);
            rootNode.children.add(initionLoopNode);

            curToken = onelineTokens.get(KW_IN_POSITION.getValue());
            if (curToken.getType() == TokenType.IN) {
                curToken = onelineTokens.get(KW_RANGE_POSITION.getValue());
                if (curToken.getType() == TokenType.RANGE) {
                    curToken = onelineTokens.get(KW_LEFT_RANGE_BRACE.getValue());
                    if (curToken.getType() == TokenType.LEFT_PARENTHESIS) {
                        Node conditionLoopNode = new Node(ASTNodeType.LOOP_CONDITION);
                        conditionLoopNode.children.add(new Node(ASTNodeType.IDENTIFIER, loopVarToken));
                        Token borederLoopToken = onelineTokens.get(KW_RANGE_VAL_POSITION.getValue());
                        conditionLoopNode.children.add(new Node(ASTNodeType.IDENTIFIER, borederLoopToken));
                        rootNode.children.add(conditionLoopNode);
                        curToken = onelineTokens.get(KW_RIGHT_RANGE_BRACE.getValue());
                        if (curToken.getType() == TokenType.RIGHT_PARENTHESIS) {
                            curToken = onelineTokens.get(KW_COLON_POSITION.getValue());
                            if (curToken.getType() == TokenType.COLON) {
                                Node stepLoopNode = new Node(ASTNodeType.LOOP_STEP);
                                Node assignStepNode = new Node(ASTNodeType.ASSIGNMENT);
                                Node plusOneNode = new Node(ASTNodeType.BINOP, new Token(TokenType.PLUS, "+", "+", curToken.getLine(), curToken.getLevelNesting()));
                                plusOneNode.children.add(new Node(ASTNodeType.IDENTIFIER, loopVarToken));
                                plusOneNode.children.add(new Node(ASTNodeType.IDENTIFIER,
                                        new Token(TokenType.IDENTIFIER, "1", "1", loopVarToken.getLine(), loopVarToken.getLevelNesting())));
                                assignStepNode.children.add(new Node(ASTNodeType.IDENTIFIER, loopVarToken));
                                assignStepNode.children.add(plusOneNode);
                                stepLoopNode.children.add(assignStepNode);
                                rootNode.children.add(stepLoopNode);
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
        return rootNode;
    }

    private Node handleAssignment(List<ASTError> errors, Node wholeCode, List<Token> onelineTokens, Token t) {
        Node root = null;
        try {
            root = getAssignmentNode(onelineTokens, 1); //new Node(ASTNodeType.ASSIGNMENT, onelineTokens.get(1))
        } catch (NoAssignmentTokenException e) {
            errors.add(new ASTError(onelineTokens.get(1), "Нет оператора присаивания", onelineTokens.get(1).getLine()));
            return null;
        }

        Node id = new Node(ASTNodeType.IDENTIFIER, t);
        Node right = getRightSubTree(onelineTokens, RIGHT_PART_POSITION);
        root.children = new ArrayList<>(Arrays.asList(id, right));
        return root;
    }
public void printASTErrors(){
        if (errors.size() > 0){
            System.out.println("There are errors of syntax analysis at building AST");
            errors.forEach(System.out::println);
        }else{
            System.out.println("There aren't any errors of syntax analysis at building AST");
        }
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
