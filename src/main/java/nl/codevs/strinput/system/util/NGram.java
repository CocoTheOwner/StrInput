package nl.codevs.strinput.system.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Utility class for n-gram-based string comparison.
 * {@link #nGramMatch(String, String)} for single string comparisons.
 * {@link #ngramMatching(String, String[])} for input-to-options mapping.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class NGram {

    /**
     * Match input with options using n-gram search.<br>
     * Favours input that has overlapping characters with an option.
     * Gives extra score if that overlap lies at the start of the option.
     * @param input the input string
     * @param options the options to match with
     * @return the [0, 1] match degree
     */
    public static double[] ngramMatching(String input, List<String> options) {
        int max = nGramMatch(input, input);
        return options.stream().mapToDouble(o -> (double) nGramMatch(input, o) / max).toArray();
    }

    /**
     * N-gram match two strings.<br>
     *
     * N-gram matching is modified to award sources that form (a part of) the beginning of the target.<br>
     *
     * @param source string 1
     * @param target string 2
     * @return match score between 0 and 1
     */
    public static int nGramMatch(String source, String target) {

        source = source.toLowerCase(Locale.ROOT);
        target = target.toLowerCase(Locale.ROOT);

        int nGramScore = 0;
        for (int i = 1; i < source.length() + 1; i++) {

            if (target.startsWith(source.substring(0, i))) {
                nGramScore += source.length();
            }

            List<String> set = new ArrayList<>();

            for (int j = 0; j < source.length() - i + 1; j++) {
                set.add(source.substring(j, j + i));
            }

            for (int j = 0; j < target.length() - i + 1; j++) {
                String sub = target.substring(j, j + i);
                if (set.contains(sub)) {
                    nGramScore += i;
                    set.remove(sub);
                    break;
                }
            }
        }

        return nGramScore;
    }
}
