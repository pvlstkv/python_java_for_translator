package lexical;

import java.util.List;

public class LexicalAnalysisResult {
    private final List<List<Token>> tokens;
    private final List<Error> errors;

    public LexicalAnalysisResult(List<List<Token>> tokens, List<Error> errors) {
        this.tokens = tokens;
        this.errors = errors;
    }

  public void printAllTokens(){
        tokens.forEach(System.out::println);
  }

  public void printErrors(){
        if (errors.size() > 0){
            System.out.println("There are errors of lexical analyse");
            errors.forEach(System.out::println);
        }else{
            System.out.println("There aren't any errors of lexical analyse");
        }
  }

    public List<List<Token>> getTokens() {
        return tokens;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
