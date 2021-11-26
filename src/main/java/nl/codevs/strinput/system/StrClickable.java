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

package nl.codevs.strinput.system;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adds the option for a {@link StrMessage} to be clicked on.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrClickable extends StrMessage {

    /**
     * RNG for password generation;
     */
    private static final Random random = new Random();

    /**
     * Registered contracts (integer password -> {@link CompletableFuture})
     */
    public static final ConcurrentHashMap<Long, CompletableFuture<String>> FUTURES = new ConcurrentHashMap<>();

    /**
     * Complete a future.
     * @param value the value with which to complete the future <br>
     * (formatted as {@code PasswordLong Some text here})
     * @throws InvalidParameterException if the input value is invalid or has no attached {@link CompletableFuture} in {@link #FUTURES}
     */
    public static void complete(String value) throws InvalidParameterException {
        List<String> in = Arrays.asList(value.split(" "));

        if (in.size() < 2) {
            throw new InvalidParameterException("Input value (" + value + ") invalid due to illegal format, must be 'PasswordLong Some text here'");
        }

        long password;
        try {
            password = Long.parseLong(in.get(0));
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Input value (" + value + ")'s first string (" + in.get(0) + ") cannot be converted to a Long (" + e.getMessage() + ")");
        }

        if (!FUTURES.containsKey(password)) {
            throw new InvalidParameterException("Input value (" + value + ") password (" + password + ") has no attached future");
        }

        CompletableFuture<String> future = FUTURES.get(password);
        future.complete(String.join(" ", in.subList(1, in.size() - 1)));
    }

    /**
     * Create a clickable string and post contract for future completion.
     * @param options list of options that should be picked from
     * @param toComplete the {@link CompletableFuture} to complete
     * @param messageText vararg message components forming the text.
     */
    public StrClickable(List<String> options, CompletableFuture<String> toComplete, Component... messageText) {
        super(messageText);
        long password = random.nextLong(Long.MAX_VALUE / 2, Long.MAX_VALUE);
        options.forEach(o -> optionsList.add(new Option(o, password)));
        FUTURES.put(password, toComplete);
    }

    /**
     * Create a clickable string and post contract for future completion.
     * @param options list of options that should be picked from
     * @param toComplete the {@link CompletableFuture} to complete
     * @param messageText list of message components forming the text.
     */
    public StrClickable(List<String> options, CompletableFuture<String> toComplete, List<Component> messageText) {
        super(messageText);
        long password = random.nextLong(Long.MAX_VALUE / 2, Long.MAX_VALUE);
        options.forEach(o -> optionsList.add(new Option(o, password)));
        FUTURES.put(password, toComplete);
    }

    /**
     * Create a clickable string and post contract for future completion.
     * @param options list of options that should be picked from
     * @param toComplete the {@link CompletableFuture} to complete
     * @param messageText list of strings forming the text.
     */
    public StrClickable(List<String> options, CompletableFuture<String> toComplete, String... messageText) {
        super(messageText);
        long password = random.nextLong(Long.MAX_VALUE / 2, Long.MAX_VALUE);
        options.forEach(o -> optionsList.add(new Option(o, password)));
        FUTURES.put(password, toComplete);
    }

    /**
     * List of options.
     */
    private final List<Option> optionsList = new ArrayList<>();

    /**
     * Get the list of options.
     * @return the list of options
     */
    public List<Option> getOptions() {
        return optionsList;
    }

    /**
     * Option with password.
     */
    public record Option(String option, long password) {

    }
}
