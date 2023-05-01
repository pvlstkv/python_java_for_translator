package lexical;

import ast.Node;
import ast.Tree;
import code_generation.Generator;
import semantic.VarTable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Translator {
    public static void main(String[] args) throws IOException {
        runFile("src/python_code.py");
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (; ; ) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source);
        try {
            System.out.println("lexical analyse");
            LexicalAnalysisResult LAResults = scanner.scanTokens();
            // print all tokens
            LAResults.printAllTokens();
            LAResults.printErrors();

            System.out.println("syntax analyse");
            Tree tree = new Tree(LAResults.getTokens());
            Node ast = tree.buildAST();
            tree.printASTErrors();
            ast.printTree();

            System.out.println("semantic analyse");
            VarTable varTable = new VarTable(ast);
            varTable.printVarTable();
            varTable.printErrors();

            Generator g = new Generator(LAResults.getTokens());
            g.translate(ast);
            g.printJavaCode();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
