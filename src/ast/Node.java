package ast;

import lexical.Token;

import java.util.ArrayList;
import java.util.List;

public class Node {
    ASTNodeType nodeType;
//    String value;
    Token token;
    List<Node> children;


    public Node(ASTNodeType nodeType) {
        this.nodeType = nodeType;
        this.children = new ArrayList<>();
    }

    public Node(ASTNodeType nodeType, Token token) {
        this.nodeType = nodeType;
        this.token = token;
        this.children = new ArrayList<>();
    }

    public Node() {
        this.children = new ArrayList<>();
    }

    public Node(ASTNodeType nodeType, String value, Token token, List<Node> children) {
        this.nodeType = nodeType;
//        this.value = value;
        this.token = token;
        this.children = children;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeType=" + nodeType +
//                ", value='" + value + '\'' +
                ", token=" + token +
                ", children=" + children +
                '}';
    }
}