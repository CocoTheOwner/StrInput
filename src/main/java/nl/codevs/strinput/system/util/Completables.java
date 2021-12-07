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
package nl.codevs.strinput.system.util;

import nl.codevs.strinput.system.parameter.StrParameterHandler;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handle {@link java.util.concurrent.CompletableFuture}s such as when a {@link StrParameterHandler.StrWhichException} happens, and there are hence multiple options to pick from.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class Completables {

    private static final ConcurrentHashMap<Long, CompletableFuture<String>> FUTURES = new ConcurrentHashMap<>();

    /**
     * RNG for password generation;
     */
    private static final Random random = new Random();

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
     * Post and generate clickables for a selection of options and a completable future.
     *
     * @param options the list of string options to pick from.
     * @param toComplete the to-complete future.
     * @return a list of StrClickables with the text of options and on-click events to complete the right future.
     */
    public static List<Str> postAndClickable(List<String> options, CompletableFuture<String> toComplete) {

        // Generate password
        long password;
        do {
            password = (Long.MAX_VALUE - 1) / 2 + random.nextInt(Integer.MAX_VALUE);
        }
        while (FUTURES.containsKey(password));

        // Generate Strs
        List<Str> optionsList = new ArrayList<>();
        options.forEach(o -> optionsList.add(new Str(C.X, C.X, () -> Completables.complete(o))));

        // Post future
        FUTURES.put(password, toComplete);

        return optionsList;
    }
}
