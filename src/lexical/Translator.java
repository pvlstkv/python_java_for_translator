package lexical;

import ast.Tree;

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
        LexicalAnalysisResult LAResults = scanner.scanTokens();

        // print all tokens
        for (List<Token> tokenLine : LAResults.getTokens()) {
//            for (Token token : tokenLine) {
                System.out.println(tokenLine);
//            }
        }

        // print all errors
        if (LAResults.getErrors().size() != 0) {
            System.out.println("There are errors:");
            for (Error e : LAResults.getErrors()) {
                String s = e.toString();
                System.out.println(s);
            }
        } else {
            System.out.println("No errors");
        }

        new Tree(LAResults.getTokens());
    }
}
