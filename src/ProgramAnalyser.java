import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramAnalyser {

    private static final int NAME_LENGTH = 6;
    private static final int PAIR_COUNT = 3;
    private static final String SEPARATOR = ";";
    private static final String PAIR_SEPARATOR = "-";
    private static final int FILE_LINE_COUNT = 20;

    public static String getNameFromLine(String line) {
        return line.split(SEPARATOR)[0];
    }

    public static List<Score> convertLineToScores(String line) {
        return Arrays.stream(line.split(SEPARATOR)[1].split(" "))
                .map(pairString -> {
                    String[] pair = pairString.split(PAIR_SEPARATOR);
                    return new Score(Integer.parseInt(pair[0]), Integer.parseInt(pair[1]));
                }).toList();
    }

    private final Supplier<String> makeName =
            () -> new Random()
                    .ints('A', 'Z')
                    .limit(NAME_LENGTH)
                    .mapToObj(num -> String.valueOf((char) num))
                    .collect(Collectors.joining());

    private final Function<Integer, String> makeScores = (max) -> {
        int[] numbers = new Random()
                .ints(0, max)
                .limit(PAIR_COUNT*2)
                .toArray();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < numbers.length; i += 2) {
            result.append(numbers[i]).append(PAIR_SEPARATOR).append(numbers[i+1]).append(' ');
        }
        return result.toString();
    };

    private final BiConsumer<Integer, String> makeScoresFile = (max, fileName) -> {
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < FILE_LINE_COUNT; i++) {
            content.append(makeName.get()).append(SEPARATOR).append(makeScores.apply(max)).append('\n');
        }

        try {
            Files.writeString(Path.of(fileName), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    };

    private final BiFunction<String, List<BiPredicate<String, List<Score>>>, Stream<String>> checkConditions = (filename, predicates) -> {
        List<String> lines;
        try {
            lines = Files.readAllLines(Path.of(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return lines.stream().filter(line -> {
            String name = getNameFromLine(line);
            List<Score> scores = convertLineToScores(line);
             boolean result = true;
             for (BiPredicate<String, List<Score>> predicate : predicates) {
                 result = result && predicate.test(name, scores);
             }
             return result;
        });
    };

    private final BiPredicate<String, List<Score>> condNameLowStart
            = (name, score) -> name.toCharArray()[0] >= 'A' && name.toCharArray()[0] <= 'K';

    private final BiPredicate<String, List<Score>> condDebugIsHigher
            = (name, scores) -> {
        int debugSum = 0;
        int releaseSum = 0;
        for (Score score : scores) {
            debugSum += score.getDebug();
            releaseSum += score.getRelease();
        }
        return debugSum > releaseSum;
    };

    public BiConsumer<Integer, String> getMakeScoresFile() {
        return makeScoresFile;
    }

    public BiFunction<String, List<BiPredicate<String, List<Score>>>, Stream<String>> getCheckConditions() {
        return checkConditions;
    }

    public BiPredicate<String, List<Score>> getCondNameLowStart() {
        return condNameLowStart;
    }

    public BiPredicate<String, List<Score>> getCondDebugIsHigher() {
        return condDebugIsHigher;
    }
    
    public static Function<Class<?>, List<String>> findAnnots = (clazz) -> {
    	return Arrays.stream(clazz.getDeclaredMethods())
    		.filter(m -> m.isAnnotationPresent(Debug.class) && m.isAnnotationPresent(Release.class))
    		.map(m -> {
    			StringBuilder sb = new StringBuilder();
    			sb.append(m.getName()).append(";");
    			var debugValues = m.getAnnotation(Debug.class).value();
    			var releaseValues = m.getAnnotation(Release.class).value();
    			for(int i =0; i<debugValues.length; i++) {
    				sb.append(debugValues[i]).append("-").append(releaseValues[i]).append(" ");
    			}
    			return sb.toString();
    		})
    		.toList();
    };
}
