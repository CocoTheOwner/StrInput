/*
 * This file is part of the StrInput distribution.
 * (https://github.com/CocoTheOwner/StrInput)
 * Copyright (c) 2021 Sjoerd van de Goor.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package nl.codevs.strinput.system.util;

import nl.codevs.strinput.system.virtual.StrVirtual;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Utility class for n-gram-based string comparison.
 * {@link #nGramMatch(String, String)} for single string comparisons.
 * {@link #ngramMatching(String, List)} for input-to-options mapping.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class NGram {

    private NGram() {
        // Never used
    }

    /**
     * Match input with options using n-gram search.<br>
     * Favours input that has overlapping characters with an option.
     * Gives extra score if that overlap lies at the start of the option.
     * @param input the input string
     * @param options the options to match with
     * @return the [0, 1] match degree
     */
    public static double[] ngramMatching(
            @NotNull final String input,
            @NotNull final List<String> options
    ) {
        int max = nGramMatch(input, input);
        return options.stream().mapToDouble(
                o -> (double) nGramMatch(input, o) / max
        ).toArray();
    }

    /**
     * N-gram match two strings.<br>
     *
     * Standard N-gram matching is modified to award sources
     * that form (a part of) the beginning of the target.
     *
     * @see <a href="https://en.wikipedia.org/wiki/N-gram">N-Gram Wikipedia</a>
     * @param source string 1
     * @param target string 2
     * @return match score between 0 and 1
     */
    public static int nGramMatch(
            @NotNull final String source,
            @NotNull final String target
    ) {

        String sourceLower = source.toLowerCase(Locale.ROOT);
        String targetLower = target.toLowerCase(Locale.ROOT);

        int nGramScore = 0;
        for (int i = 1; i < sourceLower.length() + 1; i++) {

            if (targetLower.startsWith(sourceLower.substring(0, i))) {
                nGramScore += sourceLower.length();
            }

            List<String> set = new ArrayList<>();

            for (int j = 0; j < sourceLower.length() - i + 1; j++) {
                set.add(sourceLower.substring(j, j + i));
            }

            for (int j = 0; j < targetLower.length() - i + 1; j++) {
                String sub = targetLower.substring(j, j + i);
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
     * {@code strVirtualList} is sorted and returned.<br>
     * The best match (the element with the highest n-gram score) is first, and the lowest last.
     * @param input the input string for matching (source)
     * @param strVirtualList the list of virtual nodes to sort.
     *                       <em>Not modified.</em>
     * @param threshold the minimal matching score
     * @return a list with the elements of
     * {@code strVirtualList}, in sorted order.
     */
    @Contract(mutates = "param2")
    public static @NotNull List<StrVirtual> sortByNGram(
            @NotNull final String input,
            @NotNull final List<StrVirtual> strVirtualList,
            final double threshold
    ) {

        // Get names and virtuals
        int amount = strVirtualList.stream()
                .mapToInt(v -> v.getNames().size()).sum();

        StrVirtual[] virtuals = new StrVirtual[amount];
        String[] names = new String[amount];

        int index = 0;
        for (StrVirtual strVirtual : strVirtualList) {
            for (String name : strVirtual.getNames()) {
                virtuals[index] = strVirtual;
                names[index] = name;
                index++;
            }
        }

        if (amount != index) {
            throw new IllegalStateException(
                    "Amount " + amount + " not equal to index " + input
            );
        }

        // Results array, 1:1 with names
        double[] results = NGram.ngramMatching(input, List.of(names));

        // Ordered list of virtual nodes
        ConcurrentHashMap<StrVirtual, Double> scores
                = new ConcurrentHashMap<>();
        for (int i = 0; i < amount; i++) {
            if (!scores.containsKey(virtuals[i])
                    || scores.get(virtuals[i]) < results[i]) {
                scores.put(virtuals[i], results[i]);
            }
        }

        // Get & sort
        return strVirtualList
                .stream()
                .filter(v -> scores.get(v) >= threshold)
                .sorted(Comparator.comparingInt(v -> v.getName().length()))
                .sorted(Comparator.comparingDouble(v -> -scores.get(v)))
                .collect(Collectors.toList());
    }
}
