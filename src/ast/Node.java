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
//                ", children=" + children +
                '}';
    }

    public void printTree() {
        printTree("", this);
    }

    private void printTree(String prefix, Node node) {
        System.out.println(prefix + "└── " + node.toString());
        for (int i = 0; i < node.children.size() - 1; i++) {
            Node child = node.children.get(i);
            printTree(prefix + "    ├── ", child);
        }
        if (node.children.size() > 0) {
            Node child = node.children.get(node.children.size() - 1);
            printTree(prefix + "    └── ", child);
        }
    }
}
