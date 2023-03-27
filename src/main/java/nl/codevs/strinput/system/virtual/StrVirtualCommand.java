/*
 * This file is part of the Strinput distribution.
 * (https://github.com/CocoTheOwner/Strinput)
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
package nl.codevs.strinput.system.virtual;

import nl.codevs.strinput.system.StrInput;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.StrSettings;
import nl.codevs.strinput.system.Param;
import nl.codevs.strinput.system.Env;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.util.NGram;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * A {@link StrInput} annotated method's virtual representation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class StrVirtualCommand implements StrVirtual {

    /**
     * Null parameter.
     */
    private static final int NULL_PARAM = Integer.MAX_VALUE - 69420;

    /**
     * Command parameters.
     */
    private final List<StrVirtualParameter> parameters;
    /**
     * The underlying method.
     */
    private final Method method;
    /**
     * Parent category.
     */
    private final StrVirtualCategory parent;
    /**
     * Command annotation.
     */
    private final StrInput annotation;

    /**
     * Create a new virtual command.<br>
     * Assumes {@code command} is annotated by @{@link StrInput}.
     * @param previous the virtual category this command is in
     * @param command the underlying method
     */
    public StrVirtualCommand(
            @Nullable final StrVirtualCategory previous,
            @NotNull final Method command) {
        this.parent = previous;
        this.method = command;
        this.annotation = method.getAnnotation(StrInput.class);
        this.parameters = setupParameters();
    }

    /**
     * Calculate the parameters in this method.<br>
     * Sorted by required & contextuality.
     * @return {@link List} of {@link StrVirtualParameter}s
     */
    private @NotNull List<StrVirtualParameter> setupParameters() {
        List<StrVirtualParameter> params = new ArrayList<>();
        Arrays.stream(method.getParameters()).filter(
                p -> p.isAnnotationPresent(Param.class)
        ).forEach(p -> params.add(new StrVirtualParameter(p)));
        return params;
    }

    /**
     * Get parameters.
     * @return the parameters
     */
    @Contract(" -> new")
    public @NotNull List<StrVirtualParameter> getParameters() {
        return new ArrayList<>(parameters);
    }

    /**
     * Get the parent virtual.
     *
     * @return the parent virtual
     */
    @Override
    public @Nullable StrVirtualCategory getParent() {
        return parent;
    }

    /**
     * Get the default virtual name
     * (when the annotation was not given a specific name).
     *
     * @return the name
     */
    @Override
    public @NotNull String getDefaultName() {
        return method.getName();
    }

    /**
     * Get the annotation on the class/method.
     *
     * @return the annotation
     */
    @Override
    public @NotNull StrInput getAnnotation() {
        return annotation;
    }

    /**
     * Run the virtual.
     *
     * @param arguments the remaining arguments
     * @return true if this virtual ran successfully
     */
    @Override
    public boolean run(@NotNull final List<String> arguments) {

        if (arguments.size() != 0) {
            debug(new Str(C.GREEN).a("Entered arguments: ")
                    .a(new Str(String.join(", ", arguments), C.BLUE)));
        }

        ConcurrentHashMap<StrVirtualParameter, Object> params
                = computeParameters(arguments);

        if (params == null) {
            debug(new Str(C.RED).a("Parameter parsing failed for ")
                    .a(new Str(getName(), C.BLUE)));
            help(user());
            return true;
        }

        Object[] finalParams = new Object[getParameters().size()];

        // Final checksum.
        // Everything should already be valid,
        // but this is just in case.
        // Also fills the finalParams array.
        int x = 0;
        for (StrVirtualParameter parameter : getParameters()) {
            if (!params.containsKey(parameter)) {
                debug(new Str("Failed to handle command"
                        + " because of missing param: ", C.RED)
                        .a(new Str(parameter.getName(), C.BLUE))
                        .a(new Str("!", C.RED)));
                debug(new Str("Params stored: ", C.RED)
                        .a(new Str(params.keySet().stream()
                                .map(StrVirtualParameter::getName)
                                .collect(Collectors.joining(", ")
                                ), C.BLUE))
                );
                debug(new Str("This is a big problem"
                        + " within the Decree system,"
                        + " as it should have been caught earlier."
                        + " Please contact the author(s).", C.RED));
                user().sendMessage(
                        new Str("A big error occurred in the command system."
                                + " Contact your admin!", C.RED));
                return false;
            }

            Object value = params.get(parameter);
            finalParams[x++] = value.equals(NULL_PARAM) ? null : value;
        }
        if (!getParameters().isEmpty()) {
            debug(new Str("Elements that will be parsed ("
                    + finalParams.length + " of "
                    + getParameters().size() + "):")
            );
            debug(new Str(Arrays.stream(finalParams)
                    .map(Object::toString)
                    .collect(Collectors.joining(", "))));
        }
        StrUser user = user();
        StrCenter center = center();
        Runnable rx = () -> {
            try {
                try {
                    Env.UserContext.touch(user);
                    Env.CenterContext.touch(center);
                    method.setAccessible(true);
                    method.invoke(getParent().getInstance(), finalParams);
                } catch (InvocationTargetException e) {
                    if (e.getCause().getMessage()
                            .endsWith("may only be triggered synchronously.")) {
                        debug(new Str(
                                "Sent asynchronously while it must be ran sync."
                                        + " Set 'sync = true' in the annotation"
                                        + " of the command or category", C.RED));
                        e.printStackTrace();
                        user().sendMessage(
                                new Str("The command you tried to run (", C.RED)
                                .a(new Str(getPath(), C.BLUE)
                                        .a(new Str(") may only be run sync!"
                                            + " Contact your admin!", C.RED))));
                    } else {
                        throw e;
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
                user().sendMessage(new Str(
                        "Uncaught Exception thrown while executing,"
                                + " contact your admin!", C.RED));
                throw new RuntimeException("Failed to execute " + getPath());
            }
        };

        if (annotation.sync()) {
            center().runSync(rx);
        } else {
            rx.run();
        }

        return true;
    }

    /**
     * Send help for this virtual to a user.
     *
     * @param user the user to send help to
     */
    @Override
    public void help(@NotNull final StrUser user) {
        user.sendMessage(new Str(C.GREEN)
                .a(getName() + " " + parameters.size()));
    }


    /**
     * Compute parameter objects from string argument inputs.
     * @param args The arguments (parameters) to parse into this command
     * @return A {@link ConcurrentHashMap}
     * from the parameter to the instantiated object for that parameter
     */
    private @Nullable ConcurrentHashMap<StrVirtualParameter, Object>
    computeParameters(@NotNull final List<String> args) {

        /*
         * Apologies for the obscene amount of loops.
         * It is the only way this can be done functionally.
         *
         * Note that despite the great amount of loops,
         * the average runtime is still O(log(n)).
         * This is because of the ever-decreasing number of
         * arguments & options that are already matched.
         *
         * If all arguments are already matched
         *  in the first (quick equals) loop,
         *  the runtime is actually O(n)
         */

        ConcurrentHashMap<StrVirtualParameter, Object> params
                = new ConcurrentHashMap<>();
        ConcurrentHashMap<
                StrVirtualParameter,
                StrParameterHandler.StrParseException
        > parseExceptionArgs = new ConcurrentHashMap<>();

        List<StrVirtualParameter> options = getParameters();
        List<String> dashArgs = new ArrayList<>();
        List<String> keylessArgs = new ArrayList<>();
        List<String> keyedArgs = new ArrayList<>();
        List<String> nullArgs = new ArrayList<>();
        List<String> badArgs = new ArrayList<>();

        fillLists(
                args,
                dashArgs,
                keyedArgs,
                keylessArgs,
                nullArgs,
                badArgs
        );

        parseKeyedArgs(
                keyedArgs,
                options,
                params,
                badArgs,
                parseExceptionArgs
        );

        parseNullArgs(
                nullArgs,
                params,
                options
        );

        parseDashArgs(
                dashArgs,
                params,
                options
        );

        parseKeylessArgs(
                keylessArgs,
                params,
                options,
                parseExceptionArgs,
                badArgs
        );

        parseRemaining(
                options,
                params,
                parseExceptionArgs,
                badArgs
        );

        debug(
                params,
                options,
                keyedArgs,
                keylessArgs,
                nullArgs,
                dashArgs,
                badArgs,
                parseExceptionArgs
        );

        return validateParameters(
                params,
                parseExceptionArgs
        ) ? params : null;
    }

    /**
     * Split up a set of arguments into lists for future parsing.
     * Args are split-up as follows ({@code ~text -> listName})
     * <ul>
     *     <li>{@code -text -> dashArgs}</li>
     *     <li>{@code text -> keylessArgs}</li>
     *     <li>{@code =value -> keylessArgs}</li>
     *     <li>{@code key=value -> keyedArgs}</li>
     *     <li>{@code key=text= -> badArgs}</li>
     *     <li>{@code key= -> badArgs}</li>
     *     <li>{@code null -> nullArgs} -
     *     only if {@link StrSettings#isAllowNullInput()}</li>
     * </ul>
     * @param args the initial arguments
     * @param dashArgs arguments prefixed with '-'
     * @param keyedArgs arguments of format 'key=value'
     * @param keylessArgs arguments of format 'value'
     * @param nullArgs arguments equal to 'null'
     * @param badArgs other arguments
     */
    private void fillLists(
            @NotNull final List<String> args,
            @NotNull final List<String> dashArgs,
            @NotNull final List<String> keyedArgs,
            @NotNull final List<String> keylessArgs,
            @NotNull final List<String> nullArgs,
            @NotNull final List<String> badArgs
    ) {
        // Split args into correct corresponding handlers
        for (String arg : args) {

            // These are handled later,
            // after other fulfilled options
            // will already have been matched
            ArrayList<String> splitArg =
                    new ArrayList<>(List.of(arg.split("=")));

            if (splitArg.size() == 1) {

                if (arg.startsWith("-")) {
                    dashArgs.add(arg.substring(1));
                } else {
                    keylessArgs.add(arg);
                }
                continue;
            }

            if (splitArg.size() > 2) {
                String oldArg = null;
                while (!arg.equals(oldArg)) {
                    oldArg = arg;
                    arg = arg.replaceAll("==", "=");
                }

                splitArg = new ArrayList<>(List.of(arg.split("=")));

                if (splitArg.size() == 2) {
                    debug(new Str(C.RED).a(
                                    "Parameter fixed by replacing"
                                            + " '==' with '=' (new arg: ")
                            .a(C.BLUE).a(arg).a(C.RED).a(")")
                    );
                    keyedArgs.add(arg);
                } else {
                    badArgs.add(arg);
                }
                continue;
            }

            if (Env.settings().isAllowNullInput()
                    && splitArg.get(1).equalsIgnoreCase("null")) {
                debug(new Str(C.GREEN).a("Null parameter added: ").a(C.BLUE).a(arg));
                nullArgs.add(splitArg.get(0));
                continue;
            }

            if (splitArg.get(0).isEmpty()) {
                debug(new Str(C.RED)
                        .a("Parameter key has empty value (full arg: ")
                        .a(C.BLUE).a(arg).a(C.RED).a(")"));
                while (!arg.startsWith("=")) {
                    arg = arg.substring(1);
                }
                keylessArgs.add(arg);
                continue;
            }

            if (splitArg.get(1).isEmpty()) {
                debug(new Str(C.RED).a("Parameter key: ")
                        .a(C.BLUE).a(splitArg.get(0))
                        .a(C.RED).a(" has empty value (full arg: ")
                        .a(C.BLUE).a(arg)
                        .a(C.RED).a(")"));
                badArgs.add(arg);
                continue;
            }

            keyedArgs.add(arg);
        }
    }

    /**
     * Send debug messages for parameters.
     * @param params parameter mapping
     * @param options remaining options
     * @param keyedArgs remaining keyed arguments
     * @param keylessArgs remaining keyless arguments
     * @param nullArgs remaining null-param arguments
     * @param dashArgs remaining -arguments
     * @param badArgs bad arguments
     * @param parseExceptionArgs arguments that ran into parse exceptions
     */
    private void debug(
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object>
                    params,
            @NotNull final List<StrVirtualParameter> options,
            @NotNull final List<String> keyedArgs,
            @NotNull final List<String> keylessArgs,
            @NotNull final List<String> nullArgs,
            @NotNull final List<String> dashArgs,
            @NotNull final List<String> badArgs,
            @NotNull final ConcurrentHashMap<
                    StrVirtualParameter,
                    StrParameterHandler.StrParseException
            > parseExceptionArgs
    ) {
        // Prevent debug if not required
        if (!Env.center().getSettings().isDebug()) {
            return;
        }

        // Convert nullArgs
        for (int i = 0; i < nullArgs.size(); i++) {
            nullArgs.set(i, nullArgs.get(i) + "=null");
        }

        // Debug
        if (Env.settings().isAllowNullInput()) {
            debug(new Str(nullArgs.isEmpty() ? C.GREEN : C.RED)
                    .a("Unmatched null argument"
                            + (nullArgs.size() == 1 ? "" : "s") + ": ")
                    .a(new Str(!nullArgs.isEmpty()
                            ? String.join(", ", nullArgs)
                            : "NONE",
                            C.BLUE)
                    ));
        }
        debug(new Str(keylessArgs.isEmpty() ? C.GREEN : C.RED)
                .a("Unmatched keyless argument"
                        + (keylessArgs.size() == 1 ? "" : "s") + ": ")
                .a(new Str(!keylessArgs.isEmpty()
                        ? String.join(", ", keylessArgs)
                        : "NONE", C.BLUE)
                ));
        debug(new Str(keyedArgs.isEmpty() ? C.GREEN : C.RED)
                .a("Unmatched keyed argument"
                        + (keyedArgs.size() == 1 ? "" : "s") + ": ")
                .a(new Str(!keyedArgs.isEmpty()
                        ? String.join(", ", keyedArgs)
                        : "NONE", C.BLUE)
                ));
        debug(new Str(badArgs.isEmpty() ? C.GREEN : C.RED)
                .a("Bad argument"
                        + (badArgs.size() == 1 ? "" : "s") + ": ")
                .a(new Str(!badArgs.isEmpty()
                        ? String.join(", ", badArgs)
                        : "NONE", C.BLUE)
                ));
        debug(new Str(parseExceptionArgs.isEmpty() ? C.GREEN : C.RED)
                .a("Failed argument"
                        + (parseExceptionArgs.size() == 1 ? "" : "s")
                        + (parseExceptionArgs.isEmpty() ? ": NONE" : ":\n")
                ));
        if (!parseExceptionArgs.isEmpty()) {
            debug(new Str(String.join(
                    ", ",
                    parseExceptionArgs
                            .values()
                            .stream()
                            .map(Throwable::getMessage)
                            .toList()
            )));
        }
        debug(new Str(options.isEmpty() ? C.GREEN : C.RED)
                .a("Unfulfilled parameter"
                        + (options.size() == 1 ? "" : "s") + ": ")
                .a(new Str(!options.isEmpty()
                                ? String.join(
                                ", ", options
                                        .stream()
                                        .map(StrVirtualParameter::getName)
                                        .toList()
                        )
                                : "NONE", C.BLUE)
                ));
        debug(new Str(dashArgs.isEmpty() ? C.GREEN : C.RED)
                .a("Unfulfilled -boolean parameter"
                        + (dashArgs.size() == 1 ? "" : "s") + ": ")
                .a(new Str(!dashArgs.isEmpty()
                        ? String.join(", ", dashArgs)
                        : "NONE", C.BLUE)
                ));

        List<Str> mappings = new ArrayList<>();
        mappings.add(new Str(C.GREEN).a("Parameter mapping:"));
        params.forEach((param, object) -> mappings.add(new Str(C.GREEN)
                        .a("\u0009 - (")
                        .a(C.BLUE)
                        .a(param.getType().getSimpleName())
                        .a(C.GREEN)
                        .a(") ")
                        .a(C.BLUE)
                        .a(param.getName())
                        .a(C.GREEN)
                        .a(" -> ")
                        .a(C.BLUE)
                        .a(object.toString()
                                .replace(String.valueOf(NULL_PARAM), "null"))
                )
        );
        options.forEach(param -> mappings.add(new Str(C.GREEN)
                .a("\u0009 - (")
                .a(C.BLUE)
                .a(param.getType().getSimpleName())
                .a(C.GREEN)
                .a(") ")
                .a(C.BLUE)
                .a(param.getName())
                .a(C.GREEN)
                .a(" -> ")
                .a(C.RED)
                .a("NONE")));

        for (Str mapping : mappings) {
            debug(mapping);
        }
    }

    /**
     * Parse remaining options by getting default & contextual values.
     * @param options remaining options
     * @param params parameter mapping
     * @param parseExceptionArgs parameters that ran into parse exceptions
     * @param badArgs bad arguments
     */
    private void parseRemaining(
            @NotNull final List<StrVirtualParameter> options,
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object>
                    params,
            @NotNull final ConcurrentHashMap<
                    StrVirtualParameter,
                    StrParameterHandler.StrParseException
            > parseExceptionArgs,
            @NotNull final List<String> badArgs
    ) {
        for (StrVirtualParameter option : new ArrayList<>(options)) {
            if (option.hasDefault()) {
                parseExceptionArgs.remove(option);
                try {
                    Object val = option.getDefaultValue();
                    params.put(option, val == null ? NULL_PARAM : val);
                    options.remove(option);
                } catch (StrParameterHandler.StrParseException e) {
                    debug(new Str(C.RED).a("Default value ")
                            .a(C.BLUE).a(option.getDefault())
                            .a(C.RED).a(" could not be parsed to ")
                            .a(option.getType().getSimpleName()));
                    debug(new Str(C.RED).a("Reason: ").a(C.BLUE).a(e.getMessage()));
                } catch (StrParameterHandler.StrWhichException e) {
                    debug(new Str(C.RED).a("Default value ")
                            .a(C.BLUE).a(option.getDefault())
                            .a(C.RED).a(" returned multiple options"));
                    options.remove(option);
                    if (Env.settings().isPickFirstOnMultiple()
                            || user().supportsClickables()) {
                        debug(new Str(C.GREEN)
                                .a("Adding the first option for parameter ")
                                .a(C.BLUE).a(option.getName()));
                        params.put(option, e.getOptions().get(0));
                    } else {
                        Object result = pickValidOption(e.getOptions(), option);
                        if (result == null) {
                            badArgs.add(option.getDefault());
                        } else {
                            params.put(option, result);
                        }
                    }
                }
            } else if (option.isContextual() && user().supportsContext()) {
                parseExceptionArgs.remove(option);
                StrContextHandler<?> handler;
                try {
                    handler = StrCenter.ContextHandling
                            .getContextHandler(option.getType());
                } catch (StrCenter.ContextHandling
                        .StrNoContextHandlerException e) {
                    debug(new Str(C.RED)
                            .a("Parameter "
                                    + option.getName()
                                    + " marked as contextual without"
                                    + " available context handler ("
                                    + option.getType().getSimpleName() + ")."));
                    user().sendMessage(new Str(C.RED)
                            .a("Parameter ")
                            .a(C.BLUE).a(option.help(user()))
                            .a(C.RED).a(" marked as contextual without"
                                    + " available context handler ("
                                    + option.getType().getSimpleName()
                                    + "). Please context your admin.")
                    );
                    e.printStackTrace();
                    continue;
                }
                Object contextValue = handler.handle(user());
                if (contextValue == null) {
                    debug(new Str(C.RED).a("Parameter: ")
                            .a(C.BLUE).a(option.getName())
                            .a(C.RED).a(" not fulfilled due to context"
                                    + " handler returning null."));
                } else {
                    debug(new Str(C.GREEN).a("Context value for ")
                            .a(C.BLUE).a(option.getName())
                            .a(C.GREEN).a(" set to: " + contextValue));
                    params.put(option, contextValue);
                    options.remove(option);
                }
            } else if (parseExceptionArgs.containsKey(option)) {
                debug(new Str(C.RED).a("Parameter: ")
                        .a(C.BLUE).a(option.getName())
                        .a(C.RED).a(" not fulfilled due to parseException: "
                                + parseExceptionArgs.get(option).getMessage()));
            }
        }
    }

    /**
     * Parse keyless arguments.
     * @param keylessArgs the keyless arguments
     * @param params parameter mapping
     * @param options parameter options
     * @param parseExceptionArgs parameters that ran into parse exceptions
     * @param badArgs bad arguments
     */
    private void parseKeylessArgs(
            @NotNull final List<String> keylessArgs,
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object>
                    params,
            @NotNull final List<StrVirtualParameter> options,
            @NotNull final ConcurrentHashMap<
                    StrVirtualParameter,
                    StrParameterHandler.StrParseException
            > parseExceptionArgs,
            @NotNull final List<String> badArgs
    ) {
        // Keyless arguments
        looping: for (StrVirtualParameter option : new ArrayList<>(options)) {

            for (String keylessArg : new ArrayList<>(keylessArgs)) {

                if (Env.settings().isAllowNullInput()
                        && keylessArg.equalsIgnoreCase("null")) {
                    debug(new Str(C.GREEN).a("Null parameter added: ")
                            .a(C.BLUE).a(keylessArg));
                    params.put(option, NULL_PARAM);
                    continue looping;
                }

                try {
                    Object result = option.getHandler().parseSafe(keylessArg);
                    parseExceptionArgs.remove(option);
                    options.remove(option);
                    keylessArgs.remove(keylessArg);
                    params.put(option, result);
                    continue looping;

                } catch (StrParameterHandler.StrParseException e) {
                    parseExceptionArgs.put(option, e);
                } catch (StrParameterHandler.StrWhichException e) {
                    parseExceptionArgs.remove(option);
                    options.remove(option);
                    keylessArgs.remove(keylessArg);

                    if (Env.settings().isPickFirstOnMultiple()
                            || user().supportsClickables()) {
                        params.put(option, e.getOptions().get(0));
                    } else {
                        Object result = pickValidOption(e.getOptions(), option);
                        if (result == null) {
                            badArgs.add(keylessArg);
                        } else {
                            params.put(option, result);
                        }
                        continue looping;
                    }
                } catch (Throwable e) {
                    // This exception is actually something that is broken
                    debug(new Str(C.RED).a("Parsing ")
                            .a(C.BLUE).a(keylessArg)
                            .a(C.RED).a(" into ")
                            .a(C.BLUE).a(option.getName())
                            .a(C.RED).a(" failed because of: ")
                            .a(C.BLUE).a(e.getMessage())
                    );
                    e.printStackTrace();
                    debug(new Str(C.RED).a("If you see a handler"
                                    + " in the stacktrace that we (")
                            .a(C.GREEN).a("StrInput")
                            .a(C.RED).a(") wrote, please report this bug to us.")
                    );
                    debug(new Str(C.RED)
                            .a("If you see a custom handler of your own,"
                                    + " there is an issue with it.")
                    );
                }
            }
        }
    }

    /**
     * Parse -arguments.
     * @param dashArgs -arguments
     * @param params parameter mapping
     * @param options parameter options
     */
    private void parseDashArgs(
            @NotNull final List<String> dashArgs,
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object>
                    params,
            @NotNull final List<StrVirtualParameter> options
    ) {
        for (StrVirtualParameter option : new ArrayList<>(options)) {
            if (option.getHandler().supports(boolean.class)) {

                // Quick equals
                for (String dashBooleanArg : new ArrayList<>(dashArgs)) {
                    if (option.getNames().contains(dashBooleanArg)) {
                        params.put(option, true);
                        dashArgs.remove(dashBooleanArg);
                        options.remove(option);
                    }
                }

                // Ignored case equals
                looping: for (String dashBooleanArg
                        : new ArrayList<>(dashArgs)) {
                    for (String name : option.getNames()) {
                        if (name.equalsIgnoreCase(dashBooleanArg)) {
                            params.put(option, true);
                            dashArgs.remove(dashBooleanArg);
                            options.remove(option);
                            continue looping;
                        }
                    }
                }

                // Name contains key (key substring of name)
                looping: for (String dashBooleanArg
                        : new ArrayList<>(dashArgs)) {
                    for (String name : option.getNames()) {
                        if (name.contains(dashBooleanArg)) {
                            params.put(option, true);
                            dashArgs.remove(dashBooleanArg);
                            options.remove(option);
                            continue looping;
                        }
                    }
                }

                // Key contains name (name substring of key)
                looping: for (String dashBooleanArg
                        : new ArrayList<>(dashArgs)) {
                    for (String name : option.getNames()) {
                        if (dashBooleanArg.contains(name)) {
                            params.put(option, true);
                            dashArgs.remove(dashBooleanArg);
                            options.remove(option);
                            continue looping;
                        }
                    }
                }
            }
        }
    }

    /**
     * Parse null arguments
     * @param nullArgs null arguments
     * @param params parameter mapping
     * @param options parameter options
     */
    private void parseNullArgs(
            @NotNull final List<String> nullArgs,
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object> params,
            @NotNull final List<StrVirtualParameter> options
    ) {


        // Quick equals null
        looping: for (String key : new ArrayList<>(nullArgs)) {
            for (StrVirtualParameter option : options) {
                if (option.getNames().contains(key)) {
                    params.put(option, NULL_PARAM);
                    options.remove(option);
                    nullArgs.remove(key);
                    continue looping;
                }
            }
        }

        // Ignored case null
        looping: for (String key : new ArrayList<>(nullArgs)) {
            for (StrVirtualParameter option : options) {
                for (String name : option.getNames()) {
                    if (name.equalsIgnoreCase(key)) {
                        params.put(option, NULL_PARAM);
                        options.remove(option);
                        nullArgs.remove(key);
                        continue looping;
                    }
                }
            }
        }

        // Name contains key (key substring of name), null
        looping: for (String key : new ArrayList<>(nullArgs)) {
            for (StrVirtualParameter option : options) {
                for (String name : option.getNames()) {
                    if (name.contains(key)) {
                        params.put(option, NULL_PARAM);
                        options.remove(option);
                        nullArgs.remove(key);
                        continue looping;
                    }
                }
            }
        }

        // Key contains name (name substring of key), null
        looping: for (String key : new ArrayList<>(nullArgs)) {
            for (StrVirtualParameter option : options) {
                for (String name : option.getNames()) {
                    if (key.contains(name)) {
                        params.put(option, NULL_PARAM);
                        options.remove(option);
                        nullArgs.remove(key);
                        continue looping;
                    }
                }
            }
        }
    }

    /**
     * Parse keyed arguments
     * @param keyedArgs keyed arguments
     * @param options parameter options
     * @param params parameter mapping
     * @param badArgs bad arguments
     * @param parseExceptionArgs arguments that ran into parse exceptions
     */
    private void parseKeyedArgs(
            @NotNull final List<String> keyedArgs,
            @NotNull final List<StrVirtualParameter> options,
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object>
                    params,
            @NotNull final List<String> badArgs,
            @NotNull final ConcurrentHashMap<
                    StrVirtualParameter,
                    StrParameterHandler.StrParseException
            > parseExceptionArgs
    ) {

        // Quick equals
        looping: for (String arg : new ArrayList<>(keyedArgs)) {
            String key = arg.split("\\Q=\\E")[0];
            String value = arg.split("\\Q=\\E")[1];
            for (StrVirtualParameter option : options) {
                if (option.getNames().contains(key)) {
                    if (parseParamInto(
                            params,
                            badArgs,
                            parseExceptionArgs,
                            option,
                            value
                    )) {
                        options.remove(option);
                        keyedArgs.remove(arg);
                    }
                    continue looping;
                }
            }
        }

        // Ignored case
        looping: for (String arg : new ArrayList<>(keyedArgs)) {
            String key = arg.split("\\Q=\\E")[0];
            String value = arg.split("\\Q=\\E")[1];
            for (StrVirtualParameter option : options) {
                for (String name : option.getNames()) {
                    if (name.equalsIgnoreCase(key)) {
                        if (parseParamInto(
                                params,
                                badArgs,
                                parseExceptionArgs,
                                option,
                                value
                        )) {
                            options.remove(option);
                            keyedArgs.remove(arg);
                        }
                        continue looping;
                    }
                }
            }
        }

        // Name contains key (key substring of name)
        looping: for (String arg : new ArrayList<>(keyedArgs)) {
            String key = arg.split("\\Q=\\E")[0];
            String value = arg.split("\\Q=\\E")[1];
            for (StrVirtualParameter option : options) {
                for (String name : option.getNames()) {
                    if (name.contains(key)) {
                        if (parseParamInto(
                                params,
                                badArgs,
                                parseExceptionArgs,
                                option,
                                value
                        )) {
                            options.remove(option);
                            keyedArgs.remove(arg);
                        }
                        continue looping;
                    }
                }
            }
        }

        // Key contains name (name substring of key)
        looping: for (String arg : new ArrayList<>(keyedArgs)) {
            String key = arg.split("\\Q=\\E")[0];
            String value = arg.split("\\Q=\\E")[1];
            for (StrVirtualParameter option : options) {
                for (String name : option.getNames()) {
                    if (key.contains(name)) {
                        if (parseParamInto(
                                params,
                                badArgs,
                                parseExceptionArgs,
                                option,
                                value
                        )) {
                            options.remove(option);
                            keyedArgs.remove(arg);
                        }
                        continue looping;
                    }
                }
            }
        }
    }

    /**
     * Instruct the user to pick a valid option.
     * @param validOptions The valid options that can be picked (as objects)
     * @param parameter The parameter to pick options for
     * @return The string value for the selected option
     */
    private @Nullable Object pickValidOption(
            @NotNull final List<?> validOptions,
            @NotNull final StrVirtualParameter parameter
    ) {
        StrParameterHandler<?> handler = parameter.getHandler();

        int tries = Env.settings().getPickingAmount();
        List<String> options = new ArrayList<>();
        validOptions.forEach(o -> options.add(handler.toStringForce(o)));
        String result = null;

        user().sendMessage(new Str(
                "Pick a " + parameter.getName()
                        + " (" + parameter.getType().getSimpleName() + ")"
        ));
        user().sendMessage(new Str(
                "This query will expire in "
                        + Env.settings().getPickingTimeout()
                        + " seconds.", C.GREEN, C.BLUE
        ));

        while (tries-- > 0 && (result == null || !options.contains(result))) {
            user().sendMessage(new Str(
                    "Please pick a valid option.", C.GREEN, C.BLUE
            ));

            CompletableFuture<Integer> future = new CompletableFuture<>();
            for (int i = 0; i < options.size(); i++) {
                int finalI = i;
                user().sendMessage(new Str(
                        "- " + options.get(i),
                        C.GREEN,
                        C.BLUE,
                        () -> future.complete(finalI),
                        new Str(options.get(i), C.GREEN, C.BLUE)
                ));
            }
            user().playSound(StrUser.StrSoundEffect.PICK_OPTION);

            try {
                result = options.get(
                        future.get(
                                Env.settings().getPickingTimeout(),
                                TimeUnit.SECONDS
                        )
                );
            } catch (InterruptedException
                    | ExecutionException
                    | TimeoutException ignored
            ) {

            }
        }

        if (result != null && options.contains(result)) {
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).equals(result)) {
                    return validOptions.get(i);
                }
            }
        } else {
            user().sendMessage(new Str(C.RED).a(
                    "You did not enter a correct option within 3 tries."
            ));
            user().sendMessage(new Str(C.RED).a(
                    "Please double-check your arguments & option picking."
            ));
        }

        return null;
    }

    /**
     * Validate parameters.
     * @param params The parameters to validate
     * @param parseExceptions Map of exceptions for debugging purposes
     * @return True if valid, false if not
     */
    private boolean validateParameters(
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object>
                    params,
            @NotNull final ConcurrentHashMap<
                    StrVirtualParameter, StrParameterHandler.StrParseException
            > parseExceptions
    ) {
        boolean valid = true;
        for (StrVirtualParameter parameter : getParameters()) {
            if (!params.containsKey(parameter)) {
                debug(new Str(C.RED).a("Parameter: ")
                        .a(C.BLUE).a(parameter.getName())
                        .a(C.RED).a(" not in mapping."));
                Str message = new Str(C.RED).a("Parameter: ")
                        .a(C.BLUE).a(parameter.help(user()));
                if (parseExceptions.containsKey(parameter)) {
                    StrParameterHandler.StrParseException e
                            = parseExceptions.get(parameter);
                    message.a(" (").a(C.BLUE).a(e.getType().getSimpleName())
                            .a(C.RED).a(") failed for ")
                            .a(C.BLUE).a(e.getInput())
                            .a(C.RED).a(". Reason: ")
                            .a(C.BLUE).a(e.getReason());
                } else {
                    message.a(" not specified. Please add.");
                }
                user().sendMessage(message);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Parses a parameter into a map after parsing.
     * @param params The parameter map to store the value into
     * @param parseExceptionArgs Parameters which ran into parseExceptions
     * @param badArgs bad arguments (with '==' for example)
     * @param option The parameter type to parse into
     * @param value The value to parse
     * @return True if successful, false if not.
     * Nothing is added on parsing failure.
     */
    private boolean parseParamInto(
            @NotNull final ConcurrentHashMap<StrVirtualParameter, Object>
                    params,
            @NotNull final List<String> badArgs,
            @NotNull final ConcurrentHashMap<
                    StrVirtualParameter, StrParameterHandler.StrParseException
            > parseExceptionArgs,
            @NotNull final StrVirtualParameter option,
            @NotNull final String value
    ) {
        try {
            params.put(option,
                    value.equalsIgnoreCase("null")
                            ? NULL_PARAM
                            : option.getHandler().parseSafe(value)
            );
            return true;
        } catch (StrParameterHandler.StrWhichException e) {
            debug(new Str(C.RED).a("Value ")
                    .a(C.BLUE).a(value).a(C.RED).a(" returned multiple options"));
            if (Env.settings().isPickFirstOnMultiple()
                    || user().supportsClickables()) {
                debug(new Str(C.GREEN).a("Adding: ")
                        .a(C.BLUE).a(e.getOptions().get(0).toString()));
                params.put(option, e.getOptions().get(0));
            } else {
                Object result = pickValidOption(e.getOptions(), option);
                if (result == null) {
                    badArgs.add(option.getDefault());
                } else {
                    params.put(option, result);
                }
            }
            return true;
        } catch (StrParameterHandler.StrParseException e) {
            parseExceptionArgs.put(option, e);
        } catch (Throwable e) {
            debug(new Str(
                    "Failed to parse into: '" + option.getName()
                            + "' value '" + value + "'")
            );
            e.printStackTrace();
        }
        return false;
    }

    /**
     * List this node and any combination of parameters,
     * (disregarding order and value, just the parameter type/name)
     * to form a string-based graph representation.
     * @param prefix prefix all substrings with this prefix,
     *              so it aligns with previous nodes
     * @param spacing the space to append
     *               to the prefix for parameter combinations
     * @param current the current graph
     * @param exampleInput example input for NGram match scores
     */
    public void getListing(
            @NotNull final String prefix,
            @NotNull final String spacing,
            @NotNull final List<String> current,
            @NotNull final List<String> exampleInput
    ) {
        current.add(prefix + getName()
                + (getAliases().isEmpty() ? "" : " (" + getAliases() + ")")
                + " has " + getParameters().size() + " "
                + (getParameters().size() > 1 ? "parameters" : "parameter")
                + " matches with " + exampleInput.get(0) + " @ "
                + ((double) NGram.nGramMatch(exampleInput.get(0), getName())
                / NGram.nGramMatch(getName(), getName())));
        for (int i = 0; i < getParameters().size(); i++) {
            getParameters().get(i).getListing(
                    prefix + spacing,
                    current,
                    exampleInput.get(Math.min(i + 1, exampleInput.size() - 1))
            );
        }
    }
}
