package nl.codevs.strinput.system.util;

import nl.codevs.strinput.system.virtual.StrVirtual;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Utility class for n-gram-based string comparison.
 * {@link #nGramMatch(String, String)} for single string comparisons.
 * {@link #ngramMatching(String, List)} for input-to-options mapping.
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

    /**
     * Sort a list of virtual nodes by n-gram match to a string input.<br>
     * {@code strVirtualList} is sorted & returned.<br>
     * The best match (highest n-gram score) is first, and the lowest last.
     * @param input the input string for matching (source)
     * @param strVirtualList the list of virtual nodes to sort (targets). <em>Not modified.</em>
     * @param threshold the minimal matching score
     * @return an array with the elements of {@code strVirtualList}, in sorted order.
     */
    @Contract(mutates = "param2")
    public static @NotNull List<StrVirtual> sortByNGram(String input, List<StrVirtual> strVirtualList, double threshold) {

        // Get names and virtuals
        List<String> names = new ArrayList<>();
        List<StrVirtual> virtuals = new ArrayList<>();
        ConcurrentHashMap<String, Double> scores = new ConcurrentHashMap<>();
        for (StrVirtual strVirtual : strVirtualList) {
            for (String name : strVirtual.getNames()) {
                names.add(name);
                virtuals.add(strVirtual);
                scores.put(strVirtual.getName(), 0d);
            }
        }

        // Results array, 1:1 with names
        double[] results = NGram.ngramMatching(input, names);

        // Ordered list of virtual nodes
        for (int i = 0; i < results.length; i++) {
            StrVirtual virtual = virtuals.get(i);
            if (scores.get(virtual.getName()) < results[i]) {
                scores.put(virtual.getName(), results[i]);
            }
        }

        // Get & sort
        strVirtualList
                .stream()
                .filter(v -> scores.get(v.getName()) > threshold)
                .collect(Collectors.toList())
                .sort(Comparator.comparingDouble(v -> -scores.get(v.getName())));
        return strVirtualList;
    }
}
