public class Main {

    public static final String GEN_TXT = "gen.txt";

    public static void main(String[] args) {
        ProgramAnalyser programAnalyser = new ProgramAnalyser();
        programAnalyser.getMakeScoresFile().accept(9, GEN_TXT);
    }
}
