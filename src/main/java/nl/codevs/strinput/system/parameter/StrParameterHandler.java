/*
 * This file is part of the Strinput distribution (https://github.com/CocoTheOwner/Strinput).
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
package nl.codevs.strinput.system.parameter;

import nl.codevs.strinput.system.text.Str;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Parameter handler for a certain type.
 *
 * @author Sjoerd van de Goor
 * @param <T> the type that this handler handles
 * @since v0.1
 */
public interface StrParameterHandler<T> {

    /**
     * Randomization used.
     */
    Random RANDOM = new Random();

    /**
     * Get all possible values for this type.<br>
     * Do not specify lists of very high length (10^6)
     * @return a list of possibilities
     */
    List<T> getPossibilities();

    /**
     * Get all possible values for this type, filtered with some input string.<br>
     * @param input the input string to filter by
     * @return a list of possibilities
     */
    default List<T> getPossibilities(String input) {
        final String fin = input.trim();
        if (input.isEmpty()) {
            return getPossibilities();
        }

        return getPossibilities()
                .stream()
                .filter(o -> {
                    String s = toString(o);
                    return s.startsWith(fin)
                        || s.endsWith(fin);
                })
                .collect(Collectors.toList());
    }

    /**
     * Whether this handler supports the type or not.
     * @param type a type
     * @return true if it supports the type
     */
    boolean supports(@NotNull Class<?> type);

    /**
     * Parse a string to this type.<br>
     * You can throw:
     * <ul>
     *     <li>{@link StrWhichException} to indicate multiple options (ambiguity)</li>
     *     <li>{@link StrParseException} to indicate parsing problems</li>
     * </ul>
     * @param text the string to parse
     * @return an instance of this type parsed from the string
     * @throws Throwable when something else fails. (Exceptions don't have to be caught in the parser)
     */
    @NotNull T parse(@NotNull String text) throws Throwable;

    /**
     * Safely parse, catching exceptions that are not {@link StrWhichException} or {@link StrParseException}.
     * @param text the string to parse
     * @return an instance of this type parsed from the string
     * @throws StrWhichException when there are multiple options in a parser, based on the string
     * @throws StrParseException when a parser fails
     */
    @NotNull default T parseSafe(@NotNull String text) throws StrWhichException, StrParseException {
        try {
            return parse(text);
        } catch (StrWhichException | StrParseException e) {
            throw e;
        } catch (Throwable e) {
            throw new StrParseException(getClass(), text, e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * Parse an instance of this type to a string.
     * @param input the input string
     * @return the string representation of an instance of this type
     */
    @NotNull default String toString(@NotNull T input) {
        return input.toString();
    }

    /**
     * Force convert input to a string using this handler.
     * @param input the input string
     * @return the string representation of this type
     */
    @SuppressWarnings("unchecked")
    @NotNull default String toStringForce(@NotNull Object input) {
        return toString((T) input);
    }

    /**
     * Get a random default value.
     * @return the random default
     */
    @NotNull String getRandomDefault();

    /**
     * Get the multiplier for a string.
     * Sets the value to the original value, except for the multiplier characters at the end.
     * Returns the total multiplier.
     * @param value the value string to parse
     * @return the value, parsed.
     */
    default int getMultiplier(@NotNull AtomicReference<String> value) {
        int total = 1;
        int multiplier;
        int i;
        char[] chars = value.get().toCharArray();
        StringBuilder res = new StringBuilder();
        for (i = chars.length - 1; i >= 0; i--) {
            if ((multiplier = multiplierFor(chars[i])) != 1) {
                total *= multiplier;
                continue;
            }
            for (; i >= 0; i--) {
                res.append(chars[i]);
            }
            break;
        }
        value.set(res.toString());
        return total;
    }

    /**
     * Get the multiplier for some character (so that 10k can become 10000).
     * Returns 1 for unlisted characters.
     * <ol>
     *     <li>c -> 16</li>
     *     <li>h -> 100</li>
     *     <li>r -> 512</li>
     *     <li>k -> 1000</li>
     *     <li>m -> 1000000</li>
     *     <li>default -> 1</li>
     * </ol>
     * @param character the character to get the multiplier of
     * @return the multiplier
     */
    default int multiplierFor(char character) throws IllegalStateException {
        return switch (character) {
            case 'c' | 'C' -> 16;
            case 'h' | 'H' -> 100;
            case 'r' | 'R' -> 512;
            case 'k' | 'K' -> 1000;
            case 'm' | 'M' -> 1000000;
            default -> 1;
        };
    }

    /**
     * Thrown when a decree parameter is parsed, but parsing fails.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    class StrParseException extends Exception {
        private final Class<?> type;
        private final Str input;
        private final Str reason;

        /**
         * Get the underlying exception's class.
         * @return the underlying exception's class
         */
        public Class<?> getType() {
            return type;
        }

        /**
         * Get the input that caused the exception.
         * @return the input
         */
        public Str getInput() {
            return input;
        }

        /**
         * Get the specified reason the parser failed.
         * @return the reason the parser failed
         */
        public Str getReason() {
            return reason;
        }

        /**
         * Create a new exception.
         * @param type the underlying parser's class
         * @param input the input string that caused the exception
         * @param reason the reason specified for the exception
         */
        public StrParseException(final Class<?> type, final String input, final String reason) {
            super();
            this.type = type;
            this.input = new Str(input);
            this.reason = new Str(reason);
        }
    }

    /**
     * Thrown when more than one option is available for a singular mapping<br>
     * Like having a hashmap where one input maps to two outputs.
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    class StrWhichException extends Exception {

        /**
         * List of options that caused the exception to be thrown.
         */
        private final List<?> options;

        /**
         * Get the options that caused this exception.
         * @return the list of options
         */
        public List<?> getOptions() {
            return options;
        }

        /**
         * Create a new exception.
         * @param type the underlying parser's class
         * @param input the input string that caused the exception
         * @param options the list of options to pick from
         */
        public StrWhichException(final Class<?> type, final String input, final List<?> options) {
            super("Cannot parse \"" + input + "\" into type " + type.getSimpleName() + " because of multiple options");
            this.options = options;
        }
    }
}
