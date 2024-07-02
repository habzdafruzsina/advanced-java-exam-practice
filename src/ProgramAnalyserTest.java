import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ProgramAnalyserTest {

    @Test
    public void checkConditionsTest() {
        ProgramAnalyser programAnalyser = new ProgramAnalyser();
        assertDoubleCondition(programAnalyser);
        assertNameLowCondition(programAnalyser);
        assertDebugIsHigherCondition(programAnalyser);
    }

    private static void assertDoubleCondition(ProgramAnalyser programAnalyser) {
        List<String> lines = programAnalyser.getCheckConditions()
                .apply(Main.GEN_TXT, List.of(programAnalyser.getCondNameLowStart(), programAnalyser.getCondDebugIsHigher()))
                .toList();

        assertNameLowStart(lines);
        assertDebugIsHigher(lines);
    }

    private static void assertNameLowCondition(ProgramAnalyser programAnalyser) {
        List<String> lines = programAnalyser.getCheckConditions()
                .apply(Main.GEN_TXT, List.of(programAnalyser.getCondNameLowStart()))
                .toList();

        assertNameLowStart(lines);
    }

    private static void assertDebugIsHigherCondition(ProgramAnalyser programAnalyser) {
        List<String> lines = programAnalyser.getCheckConditions()
                .apply(Main.GEN_TXT, List.of(programAnalyser.getCondDebugIsHigher()))
                .toList();

        assertDebugIsHigher(lines);
    }

    private static void assertNameLowStart(List<String> lines) {
        lines.forEach(line -> {
            char firstChar = ProgramAnalyser.getNameFromLine(line).toCharArray()[0];
            assertTrue(firstChar >= 'A' && firstChar <= 'K');
        });
    }

    private static void assertDebugIsHigher(List<String> lines) {
        lines.forEach(line -> {
            List<Score> scores = ProgramAnalyser.convertLineToScores(line);
            int debugSum = 0;
            int releaseSum = 0;
            for (Score score : scores) {
                debugSum += score.getDebug();
                releaseSum += score.getRelease();
            }
            assertTrue(debugSum > releaseSum);
        });
    }
}
