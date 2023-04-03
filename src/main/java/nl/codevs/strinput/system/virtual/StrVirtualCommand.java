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
package nl.codevs.strinput.system.virtual;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import nl.codevs.strinput.system.*;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import nl.codevs.strinput.system.util.C;
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
            debug(C.GREEN + "Entered arguments: " + C.BLUE + String.join(C.GREEN + ", " + C.BLUE, arguments));
        }

        ConcurrentHashMap<StrVirtualParameter, Object> params
                = computeParameters(arguments);

        if (params == null) {
            error(C.RED + "Parameter parsing failed for " + C.BLUE + getName());
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
                error("Failed to handle command because of missing param: " + C.BLUE + parameter.getName() + C.RED + "!");
                error("Stored parameters: " + C.BLUE + params.keySet().stream()
                                .map(StrVirtualParameter::getName)
                                .collect(Collectors.joining(C.RED + ", " + C.BLUE))
                );
                error("This is a big problem"
                        + " within the Decree system,"
                        + " as it should have been caught earlier."
                        + " Please contact the author(s)!");
                user().sendMessage(C.RED + "A seriously problematic error occurred in the command system.");
                user().sendMessage(C.RED + " Please contact your admin!");
                return false;
            }

            Object value = params.get(parameter);
            finalParams[x++] = value.equals(NULL_PARAM) ? null : value;
        }
        if (!getParameters().isEmpty()) {
            debug("Elements that will be parsed (" + C.BLUE + finalParams.length + C.GREEN + " of "
                    + C.BLUE + getParameters().size() + C.GREEN + "):");
            debug(C.BLUE + Arrays.stream(finalParams)
                    .map(Object::toString)
                    .collect(Collectors.joining(C.GREEN + ", " + C.BLUE)));
        }
        StrUser user = user();
        StrCenter center = center();
        Runnable rx = () -> {
            try {
                try {
                    Context.touch(user);
                    Context.touch(center);
                    method.setAccessible(true);
                    method.invoke(getParent().getInstance(), finalParams);
                } catch (InvocationTargetException e) {
                    if (e.getCause().getMessage()
                            .endsWith("may only be triggered synchronously.")) {
                        warning("Command sent asynchronously while it must be ran sync.");
                        warning(" Set " + C.BLUE + "'sync = true'" + C.YELLOW +
                                " in the annotation of the command or category!");
                        center().printException(e);
                        user().sendMessage(C.RED + "The command you tried to run (" + C.BLUE + getPath()
                                + C.RED + ") may only be run sync!");
                        user().sendMessage(C.RED + "This is a configuration error in the command system, please contact your admin.");
                    } else {
                        throw e;
                    }
                }
            } catch (Throwable e) {
                center().printException(e);
                user().sendMessage(C.RED + "Uncaught Exception thrown while executing, contact your admin!");
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
        TextComponent.Builder component = Component.text()
                .content(center().getCommandPrefix() + getPath())
                .color(NamedTextColor.BLUE)
                .clickEvent(ClickEvent.runCommand(getPath()))
                .hoverEvent(Component.text(getAnnotation().description()));
        for (StrVirtualParameter parameter : parameters) {
            component.append(Component.text(" ")).append(parameter.help(user));
        }
        user.sendMessage(component.build());
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
         * Note that despite the great amount of loops,
         * the average runtime is still O(log(n)).
         * This is because of the ever-decreasing number of
         * arguments & options that are already matched.
         *
         * If all arguments are already matched
         *  in the first (quick equals) loop,
         *  the runtime is actually O(1)
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
                    debug("Parameter fixed by replacing '==' with '=' (new arg: " + C.BLUE + arg + C.GREEN + ")");
                    keyedArgs.add(arg);
                } else {
                    badArgs.add(arg);
                }
                continue;
            }

            if (Context.settings().isAllowNullInput()
                    && splitArg.get(1).equalsIgnoreCase("null")) {
                debug(C.GREEN + "Null parameter added: " + C.BLUE + arg);
                nullArgs.add(splitArg.get(0));
                continue;
            }

            if (splitArg.get(0).isEmpty()) {
                debug(C.GREEN + "Parameter key has empty value (full arg: " + C.BLUE + arg + C.GREEN + ")");
                while (!arg.startsWith("=")) {
                    arg = arg.substring(1);
                }
                keylessArgs.add(arg);
                continue;
            }

            if (splitArg.get(1).isEmpty()) {
                debug(C.GREEN + "Parameter key: " + C.BLUE + splitArg.get(0) + C.GREEN +
                        " has empty value (full arg: " + C.BLUE + arg + C.GREEN + ")");
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
    @SuppressWarnings("UnnecessaryUnicodeEscape")
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
        if (!Context.center().getSettings().isDebug()) {
            return;
        }

        // Convert nullArgs
        nullArgs.replaceAll(s -> s + "=null");

        // Debug
        if (Context.settings().isAllowNullInput()) {
            debug((nullArgs.isEmpty() ? C.GREEN.toString() : C.RED.toString())
                    + "Unmatched null argument" + (nullArgs.size() == 1 ? "" : "s") + ": "
                    + C.BLUE + (!nullArgs.isEmpty() ? String.join(C.RED + ", " + C.BLUE, nullArgs): "NONE")
            );
        }
        debug((keylessArgs.isEmpty() ? C.GREEN.toString() : C.RED.toString())
                + "Unmatched keyless argument" + (keylessArgs.size() == 1 ? "" : "s") + ": "
                + C.BLUE + (!keylessArgs.isEmpty() ? String.join(C.RED + ", " + C.BLUE, keylessArgs) : "NONE")
        );
        debug((keyedArgs.isEmpty() ? C.GREEN.toString() : C.RED.toString())
                + "Unmatched keyed argument" + (keyedArgs.size() == 1 ? "" : "s") + ": "
                + C.BLUE + (!keyedArgs.isEmpty() ? String.join(C.RED + ", " + C.BLUE, keyedArgs) : "NONE")
        );
        debug((badArgs.isEmpty() ? C.GREEN.toString() : C.RED.toString())
                + "Bad argument" + (badArgs.size() == 1 ? "" : "s") + ": "
                + C.BLUE + (!badArgs.isEmpty() ? String.join(C.RED + ", " + C.BLUE, badArgs) : "NONE")
        );
        debug((parseExceptionArgs.isEmpty() ? C.GREEN.toString() : C.RED.toString())
                + "Failed argument" + (parseExceptionArgs.size() == 1 ? "" : "s") + ": "
                + C.BLUE + (parseExceptionArgs.isEmpty() ? "\n" : "NONE")
        );
        if (!parseExceptionArgs.isEmpty()) {
            debug(C.BLUE + String.join(
                    C.RED + ", " + C.BLUE,
                    parseExceptionArgs
                            .values()
                            .stream()
                            .map(Throwable::getMessage)
                            .toList()
            ));
        }
        debug((options.isEmpty() ? C.GREEN.toString() : C.RED.toString())
                + "Unfulfilled parameter" + (options.size() == 1 ? "" : "s") + ": "
                + C.BLUE + (!options.isEmpty() ? String.join(C.RED + ", " + C.BLUE,
                        options.stream().map(StrVirtualParameter::getName).toList()) : "NONE")
        );
        debug((dashArgs.isEmpty() ? C.GREEN.toString() : C.RED.toString())
                + "Unfulfilled -boolean parameter" + (dashArgs.size() == 1 ? "" : "s") + ": "
                + C.BLUE + (!dashArgs.isEmpty() ? String.join(C.RED + ", " + C.BLUE, dashArgs) : "NONE")
        );

        List<String> mappings = new ArrayList<>();
        mappings.add(C.GREEN + "Parameter mapping:");
        params.forEach((param, object) -> mappings.add(
                C.GREEN + "\u0009 - ("
                + C.BLUE + param.getType().getSimpleName()
                + C.GREEN + ") "
                + C.BLUE + param.getName()
                + C.GREEN + " -> "
                + C.BLUE + object.toString().replace(String.valueOf(NULL_PARAM), "null")
        ));
        options.forEach(param -> mappings.add(
                C.GREEN + "\u0009 - ("
                + C.BLUE + param.getType().getSimpleName()
                + C.GREEN + ") "
                + C.BLUE + param.getName()
                + C.GREEN + " -> "
                + C.RED + "NONE")
        );

        for (String mapping : mappings) {
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
                    warning(C.RED + "Default value " + C.BLUE + option.getDefault() + C.RED + " could not be parsed to " + option.getType().getSimpleName());
                    warning(C.RED + "Reason: " + C.BLUE + e.getMessage());
                    center().printException(e);
                } catch (StrParameterHandler.StrWhichException e) {
                    options.remove(option);
                    if (Context.settings().isPickFirstOnMultiple()) {
                        debug(C.GREEN + "Adding the first option for parameter " + C.BLUE + option.getName());
                        params.put(option, e.getOptions().get(0));
                    } else {
                        debug(C.YELLOW + "Default value " + C.BLUE + option.getDefault() + C.YELLOW + " returned multiple options");
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
                } catch (StrCenter.ContextHandling.StrNoContextHandlerException e) {
                    error(C.RED + "Parameter " + option.getName()
                            + " marked as contextual without available context handler ("
                            + option.getType().getSimpleName()
                            + ")."
                    );
                    user().sendMessage(C.RED + "Parameter " + C.BLUE + option.help(user())
                            + C.RED + " marked as contextual without"
                            + " available context handler ("
                            + option.getType().getSimpleName()
                            + "). Please context your admin. This is a configuration error."
                    );
                    center().printException(e);
                    continue;
                }
                Object contextValue = handler.handle(user());
                if (contextValue == null) {
                    error(C.RED + "Parameter: " + C.BLUE + option.getName() + C.RED + " not fulfilled due to context"
                                    + " handler returning null.");
                } else {
                    debug(C.GREEN + "Context value for " + C.BLUE + option.getName() + C.GREEN + " set to: " + contextValue);
                    params.put(option, contextValue);
                    options.remove(option);
                }
            } else if (parseExceptionArgs.containsKey(option)) {
                error(C.RED + "Parameter: " + C.BLUE + option.getName() + C.RED + " not fulfilled due to parseException: "
                                + parseExceptionArgs.get(option).getMessage());
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

                if (Context.settings().isAllowNullInput()
                        && keylessArg.equalsIgnoreCase("null")) {
                    debug(C.GREEN + "Null parameter added: " + C.BLUE + keylessArg);
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

                    if (Context.settings().isPickFirstOnMultiple()
                            || user().replaceClickable()) {
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
                    error(C.RED + "Parsing " + C.BLUE + keylessArg + C.RED + " into " + C.BLUE + option.getName()
                            + C.RED + " failed because of: " + C.BLUE + e.getMessage());
                    center().printException(e);
                    error(C.RED + "If you see a handler in the stacktrace that StrInput wrote originally, " +
                            "please report this bug to us.");
                    error(C.RED + "If you see a custom handler of your own, there is an issue with it.");
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
    @SuppressWarnings("DuplicatedCode")
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
    @SuppressWarnings("DuplicatedCode")
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
    @SuppressWarnings("DuplicatedCode")
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

        int tries = Context.settings().getPickingAmount();
        List<String> options = new ArrayList<>();
        validOptions.forEach(o -> options.add(handler.toStringForce(o)));
        String result = null;

        user().sendMessage(C.GREEN + "Pick a " + C.BLUE + parameter.getName() + C.GREEN
                        + " (" + parameter.getType().getSimpleName() + ")"
        );
        user().sendMessage(C.GREEN + "This query will expire in "
                + C.BLUE + Context.settings().getPickingTimeout() + C.GREEN + " seconds."
        );

        while (tries-- > 0 && (result == null || !options.contains(result))) {
            user().sendMessage(C.YELLOW + "Please pick a valid option (" + tries + " tries left)");
            user().playSound(StrUser.StrSoundEffect.PICK_OPTION);

            CompletableFuture<Integer> future = new CompletableFuture<>();
            // TODO: Reimplement clickable after removing Str
            for (int i = 0; i < options.size(); i++) {
                user().sendMessage("" + C.GREEN + i + ". " + C.BLUE + options.get(i));
            }

            try {
                result = options.get(
                        future.get(
                                Context.settings().getPickingTimeout(),
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
            user().sendMessage(C.RED + "You did not enter a correct option within 3 tries.");
            user().sendMessage(C.RED + "Please double-check your arguments and run the command again.");
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
                error(C.RED + "Parameter: " + C.BLUE + parameter.getName() + C.RED + " not in mapping!");
                String message = C.RED + "Parameter: " + C.BLUE + parameter.help(user());
                if (parseExceptions.containsKey(parameter)) {
                    StrParameterHandler.StrParseException e = parseExceptions.get(parameter);
                    message += " (" + C.BLUE + e.getType().getSimpleName() + C.RED + ") failed for "
                            + C.BLUE + e.getInput() + C.RED + ". Reason: " + C.BLUE + e.getReason();
                } else {
                    message += " not specified. Please add.";
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
            if (Context.settings().isPickFirstOnMultiple()
                    || user().replaceClickable()) {
                debug(C.GREEN + "Adding: " + C.BLUE + e.getOptions().get(0).toString());
                params.put(option, e.getOptions().get(0));
            } else {
                debug("Value " + C.BLUE + value + C.YELLOW + " returned multiple options. Option picking...");
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
            error("Failed to parse into: " + C.BLUE + option.getName() + C.RED + " value " + C.BLUE + value);
            center().printException(e);
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
        String matchScore = String.valueOf((double)
                NGram.nGramMatch(exampleInput.get(0), getName()) /
                NGram.nGramMatch(getName(), getName()));
        current.add(prefix
                + "Command '" + getName() + "'"
                + (getAliases().isEmpty() ? "" : " (alias: " + getAliases() + ")")
                + " | " + getParameters().size() + (getParameters().size() > 1 ? " parameters" : " parameter")
                + " | matches '" + exampleInput.get(0) + "'"
                + " with score " + matchScore.substring(0, Math.min(matchScore.length(), 4))
        );
        for (int i = 0; i < getParameters().size(); i++) {
            getParameters().get(i).getListing(
                    prefix + spacing,
                    current,
                    exampleInput.get(Math.min(i + 1, exampleInput.size() - 1))
            );
        }
    }
}
