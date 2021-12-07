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

package nl.codevs.strinput.system.virtual;

import nl.codevs.strinput.system.api.*;
import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.context.StrNoContextHandlerException;
import nl.codevs.strinput.system.parameter.StrNoParameterHandlerException;
import nl.codevs.strinput.system.parameter.StrParseException;
import nl.codevs.strinput.system.parameter.StrWhichException;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.util.NGram;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 * A {@link StrInput} annotated method's virtual representation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class StrVirtualCommand implements StrVirtual {
    private static final int nullParam = Integer.MAX_VALUE - 69420;

    /**
     * Newline.
     */
    private static final Str newLine = new Str(C.X).a("\n");

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
     * Command center.
     */
    private final StrCenter center;

    /**
     * Create a new virtual command.<br>
     * Assumes {@code command} is annotated by @{@link StrInput}.
     * @param parent the virtual category this command is in
     * @param command the underlying method
     * @param center the command center calling this command
     */
    public StrVirtualCommand(StrVirtualCategory parent, Method command, StrCenter center) {
        this.parent = parent;
        this.method = command;
        this.center = center;
        this.annotation = method.getAnnotation(StrInput.class);
        this.parameters = setupParameters();
    }

    /**
     * Calculate the parameters in this method.<br>
     * Sorted by required & contextuality.
     * @return {@link List} of {@link StrVirtualParameter}s
     */
    private List<StrVirtualParameter> setupParameters() {
        List<StrVirtualParameter> parameters = new ArrayList<>();
        Arrays.stream(method.getParameters()).filter(p -> p.isAnnotationPresent(Param.class)).forEach(p -> parameters.add(new StrVirtualParameter(p)));
        return parameters;
    }

    /**
     * Get parameters.
     * @return the parameters
     */
    public List<StrVirtualParameter> getParameters() {
        return parameters;
    }

    /**
     * Get the parent virtual.
     *
     * @return the parent virtual
     */
    @Override
    public @Nullable StrVirtual getParent() {
        return parent;
    }

    /**
     * Get the default virtual name (when the annotation was not given a specific name)
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
     * @param user      the user that sent the command
     * @param center    the command system
     * @return true if this virtual ran successfully
     */
    @Override
    public boolean run(List<String> arguments, StrUser user, StrCenter center) {
        center.debug(new Str(C.G).a("Reached virtual command ").a(C.B).a(getName()));
        if (arguments.size() == parameters.size()) {
            center.debug(new Str(C.G).a("Running: " + getName()));
            return true;
        }
        return false;
    }

    /**
     * Send help for this virtual to a user.
     *
     * @param user the user to send help to
     */
    @Override
    public void help(StrUser user) {
        user.sendMessage(new Str(C.G).a(getName() + " " + parameters.size()));
    }


    /**
     * Compute parameter objects from string argument inputs
     * @param args The arguments (parameters) to parse into this command
     * @param user The user of the command
     * @param center The command center running this
     * @return A {@link ConcurrentHashMap} from the parameter to the instantiated object for that parameter
     */
    private ConcurrentHashMap<StrVirtualParameter, Object> computeParameters(List<String> args, StrUser user, StrCenter center) throws StrNoParameterHandlerException {

        /*
         * Apologies for the obscene amount of loops.
         * It is the only way this can be done functionally.
         *
         * Note that despite the great amount of loops the average runtime is still ~O(log(n)).
         * This is because of the ever-decreasing number of arguments & options that are already matched.
         * If all arguments are already matched in the first (quick equals) loop, the runtime is actually O(n)
         */

        ConcurrentHashMap<StrVirtualParameter, Object> parameters = new ConcurrentHashMap<>();
        ConcurrentHashMap<StrVirtualParameter, StrParseException> parseExceptionArgs = new ConcurrentHashMap<>();

        List<StrVirtualParameter> options = getParameters();
        List<String> dashBooleanArgs = new ArrayList<>();
        List<String> keylessArgs = new ArrayList<>();
        List<String> keyedArgs = new ArrayList<>();
        List<String> nullArgs = new ArrayList<>();
        List<String> badArgs = new ArrayList<>();

        // Split args into correct corresponding handlers
        for (String arg : args) {

            // These are handled later, after other fulfilled options will already have been matched
            ArrayList<String> splitArg = new ArrayList<>(List.of(arg.split("=")));

            if (splitArg.size() == 1) {

                if (arg.startsWith("-")) {
                    dashBooleanArgs.add(arg.substring(1));
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
                    center.debug(new Str(C.R).a("Parameter fixed by replacing '==' with '=' (new arg: ").a(C.B).a(arg).a(C.R).a(")"));
                } else {
                    badArgs.add(arg);
                    continue;
                }
            }

            if (StrCenter.settings.allowNullInput && splitArg.get(1).equalsIgnoreCase("null")) {
                center.debug(new Str(C.G).a("Null parameter added: ").a(C.B).a(arg));
                nullArgs.add(splitArg.get(0));
                continue;
            }

            if (splitArg.get(0).isEmpty()) {
                center.debug(new Str(C.R).a("Parameter key has empty value (full arg: ").a(C.B).a(arg).a(C.R).a(")"));
                badArgs.add(arg);
                continue;
            }

            if (splitArg.get(1).isEmpty()) {
                center.debug(new Str(C.R).a("Parameter key: ").a(C.B).a(splitArg.get(0)).a(C.R).a(" has empty value (full arg: ").a(C.B).a(arg).a(C.R).a(")").a(C.R));
                badArgs.add(arg);
                continue;
            }

            keyedArgs.add(arg);
        }

        // Quick equals
        looping: for (String arg : new ArrayList<>(keyedArgs)) {
            String key = arg.split("\\Q=\\E")[0];
            String value = arg.split("\\Q=\\E")[1];
            for (StrVirtualParameter option : options) {
                if (option.getNames().contains(key)) {
                    if (parseParamInto(parameters, badArgs, parseExceptionArgs, option, value, user)) {
                        options.remove(option);
                        keyedArgs.remove(arg);
                    } else if (StrCenter.settings.nullOnFailure) {
                        parameters.put(option, nullParam);
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
                        if (parseParamInto(parameters, badArgs, parseExceptionArgs, option, value, user)) {
                            options.remove(option);
                            keyedArgs.remove(arg);
                        } else if (StrCenter.settings.nullOnFailure) {
                            parameters.put(option, nullParam);
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
                        if (parseParamInto(parameters, badArgs, parseExceptionArgs, option, value, user)) {
                            options.remove(option);
                            keyedArgs.remove(arg);
                        } else if (StrCenter.settings.nullOnFailure) {
                            parameters.put(option, nullParam);
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
                        if (parseParamInto(parameters, badArgs, parseExceptionArgs, option, value, user)) {
                            options.remove(option);
                            keyedArgs.remove(arg);
                        } else if (StrCenter.settings.nullOnFailure) {
                            parameters.put(option, nullParam);
                        }
                        continue looping;
                    }
                }
            }
        }

        // Quick equals null
        looping: for (String key : new ArrayList<>(nullArgs)) {
            for (StrVirtualParameter option : options) {
                if (option.getNames().contains(key)) {
                    parameters.put(option, nullParam);
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
                        parameters.put(option, nullParam);
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
                        parameters.put(option, nullParam);
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
                        parameters.put(option, nullParam);
                        options.remove(option);
                        nullArgs.remove(key);
                        continue looping;
                    }
                }
            }
        }

        // Keyless arguments
        looping: for (StrVirtualParameter option : new ArrayList<>(options)) {
            if (option.getHandler().supports(boolean.class)) {
                for (String dashBooleanArg : new ArrayList<>(dashBooleanArgs)) {
                    if (option.getNames().contains(dashBooleanArg)) {
                        parameters.put(option, true);
                        dashBooleanArgs.remove(dashBooleanArg);
                        options.remove(option);
                    }
                }

                for (String dashBooleanArg : new ArrayList<>(dashBooleanArgs)) {
                    for (String name : option.getNames()) {
                        if (name.equalsIgnoreCase(dashBooleanArg)) {
                            parameters.put(option, true);
                            dashBooleanArgs.remove(dashBooleanArg);
                            options.remove(option);
                        }
                    }
                }

                for (String dashBooleanArg : new ArrayList<>(dashBooleanArgs)) {
                    for (String name : option.getNames()) {
                        if (name.contains(dashBooleanArg)) {
                            parameters.put(option, true);
                            dashBooleanArgs.remove(dashBooleanArg);
                            options.remove(option);
                        }
                    }
                }

                for (String dashBooleanArg : new ArrayList<>(dashBooleanArgs)) {
                    for (String name : option.getNames()) {
                        if (dashBooleanArg.contains(name)) {
                            parameters.put(option, true);
                            dashBooleanArgs.remove(dashBooleanArg);
                            options.remove(option);
                        }
                    }
                }
            }

            for (String keylessArg : new ArrayList<>(keylessArgs)) {

                if (StrCenter.settings.allowNullInput && keylessArg.equalsIgnoreCase("null")) {
                    center.debug(new Str(C.G).a("Null parameter added: ").a(C.B).a(keylessArg));
                    parameters.put(option, nullParam);
                    continue looping;
                }

                try {
                    Object result = option.getHandler().parseSafe(keylessArg);
                    parseExceptionArgs.remove(option);
                    options.remove(option);
                    keylessArgs.remove(keylessArg);
                    parameters.put(option, result);
                    continue looping;

                } catch (StrParseException e) {
                    parseExceptionArgs.put(option, e);
                } catch (StrWhichException e) {
                    parseExceptionArgs.remove(option);
                    options.remove(option);
                    keylessArgs.remove(keylessArg);

                    if (StrCenter.settings.pickFirstOnMultiple) {
                        parameters.put(option, e.getOptions().get(0));
                    } else {
                        Object result = pickValidOption(user, e.getOptions(), option);
                        if (result == null) {
                            badArgs.add(keylessArg);
                        } else {
                            parameters.put(option, result);
                        }
                        continue looping;
                    }
                } catch (Throwable e) {
                    // This exception is actually something that is broken
                    center.debug(new Str(C.R).a("Parsing ").a(C.B).a(keylessArg).a(C.R).a(" into ").a(C.B).a(option.getName()).a(C.R).a(" failed because of: ").a(C.B).a(e.getMessage()));
                    e.printStackTrace();
                    center.debug(new Str(C.R).a("If you see a handler in the stacktrace that we (").a(C.G).a("StrInput").a(C.R).a(") wrote, please report this bug to us."));
                    center.debug(new Str(C.R).a("If you see a custom handler of your own, there is an issue with it."));
                }
            }
        }

        // Remaining parameters
        for (StrVirtualParameter option : new ArrayList<>(options)) {
            if (option.hasDefault()) {
                parseExceptionArgs.remove(option);
                try {
                    Object val = option.getDefaultValue();
                    parameters.put(option, val == null ? nullParam : val);
                    options.remove(option);
                } catch (StrParseException e) {
                    if (StrCenter.settings.nullOnFailure) {
                        parameters.put(option, nullParam);
                        options.remove(option);
                    } else {
                        center.debug(new Str(C.R).a("Default value ").a(C.B).a(option.getDefault()).a(C.R).a(" could not be parsed to ").a(option.getType().getSimpleName()));
                        center.debug(new Str(C.R).a("Reason: ").a(C.B).a(e.getMessage()));
                    }
                } catch (StrWhichException e) {
                    center.debug(new Str(C.R).a("Default value ").a(C.B).a(option.getDefault()).a(C.R).a(" returned multiple options"));
                    options.remove(option);
                    if (StrCenter.settings.pickFirstOnMultiple) {
                        center.debug(new Str(C.G).a("Adding the first option for parameter ").a(C.B).a(option.getName()));
                        parameters.put(option, e.getOptions().get(0));
                    } else {
                        Object result = pickValidOption(user, e.getOptions(), option);
                        if (result == null) {
                            badArgs.add(option.getDefault());
                        } else {
                            parameters.put(option, result);
                        }
                    }
                }
            } else if (option.isContextual() && user.supportsContext()) {
                parseExceptionArgs.remove(option);
                StrContextHandler<?> handler;
                try {
                    handler = StrCenter.ContextHandling.getContextHandler(option.getType());
                } catch (StrNoContextHandlerException e) {
                    center.debug(new Str(C.R).a("Parameter " + option.getName() + " marked as contextual without available context handler (" + option.getType().getSimpleName() + ")."));
                    user.sendMessage(new Str(C.R).a("Parameter ").a(C.B).a(option.help(user)).a(C.R).a(" marked as contextual without available context handler (" + option.getType().getSimpleName() + "). Please context your admin."));
                    e.printStackTrace();
                    continue;
                }
                Object contextValue = handler.handle(user);
                center.debug(new Str(C.G).a("Context value for ").a(C.B).a(option.getName()).a(C.G).a(" set to: " + handler.handle(user)));
                parameters.put(option, contextValue);
                options.remove(option);
            } else if (parseExceptionArgs.containsKey(option)) {
                center.debug(new Str(C.R).a("Parameter: ").a(C.B).a(option.getName()).a(C.R).a(" not fulfilled due to parseException: " + parseExceptionArgs.get(option).getMessage()));
            }
        }

        // Convert nullArgs
        for (int i = 0; i < nullArgs.size(); i++) {
            nullArgs.set(i, nullArgs.get(i) + "=null");
        }

        // Debug
        if (StrCenter.settings.allowNullInput) {
            center.debug(new Str(nullArgs.isEmpty() ? C.G : C.R)        .a("Unmatched null argument" +        (nullArgs.size() == 1           ? "":"s") + ": ").a(C.B).a(!nullArgs.isEmpty()          ? String.join(", ", nullArgs) : "NONE"));
        }
        center.debug(new Str(keylessArgs.isEmpty() ? C.G : C.R)         .a("Unmatched keyless argument" +     (keylessArgs.size() == 1        ? "":"s") + ": ").a(C.B + (!keylessArgs.isEmpty()        ? String.join(", ", keylessArgs) : "NONE")));
        center.debug(new Str(keyedArgs.isEmpty() ? C.G : C.R)           .a("Unmatched keyed argument" +       (keyedArgs.size() == 1          ? "":"s") + ": ").a(C.B + (!keyedArgs.isEmpty()          ? String.join(", ", keyedArgs) : "NONE")));
        center.debug(new Str(badArgs.isEmpty() ? C.G : C.R)             .a("Bad argument" +                   (badArgs.size() == 1            ? "":"s") + ": ").a(C.B + (!badArgs.isEmpty()            ? String.join(", ", badArgs) : "NONE")));
        center.debug(new Str(parseExceptionArgs.isEmpty() ? C.G : C.R)  .a("Failed argument" +                (parseExceptionArgs.size() <= 1 ? "":"s") + ":\n"));
        center.debug(parseExceptionArgs.values().stream().map(e -> new Str(C.B).a(e.getMessage())).toList());
        center.debug(new Str(options.isEmpty() ? C.G : C.R)             .a("Unfulfilled parameter" +          (options.size() == 1            ? "":"s") + ": ").a(C.B + (!options.isEmpty()            ? String.join(", ", options.stream().map(StrVirtualParameter::getName).toList()) : "NONE")));
        center.debug(new Str(dashBooleanArgs.isEmpty() ? C.G : C.R)     .a("Unfulfilled -boolean parameter" + (dashBooleanArgs.size() == 1    ? "":"s") + ": ").a(C.B + (!dashBooleanArgs.isEmpty()    ? String.join(", ", dashBooleanArgs) : "NONE")));

        List<Str> mappings = new ArrayList<>();
        mappings.add(new Str(C.G).a("Parameter mapping:"));
        parameters.forEach((param, object) -> mappings.add(new Str(C.G)
                .a("\u0009 - (")
                .a(C.B)
                .a(param.getType().getSimpleName())
                .a(C.G)
                .a(") ")
                .a(C.B)
                .a(param.getName())
                .a(C.G)
                .a(" → ")
                .a(C.B)
                .a(object.toString().replace(String.valueOf(nullParam), "null"))));
        options.forEach(param -> mappings.add(new Str(C.G)
                .a("\u0009 - (")
                .a(C.B)
                .a(param.getType().getSimpleName())
                .a(C.G)
                .a(") ")
                .a(C.B)
                .a(param.getName())
                .a(C.G)
                .a(" → ")
                .a(C.R)
                .a("NONE")));

        center.debug(mappings);

        if (validateParameters(parameters, user, parseExceptionArgs)) {
            return parameters;
        } else {
            return null;
        }
    }

    /**
     * Instruct the user to pick a valid option
     * @param user The user that must pick an option
     * @param validOptions The valid options that can be picked (as objects)
     * @return The string value for the selected option
     */
    private Object pickValidOption(StrUser user, List<?> validOptions, StrVirtualParameter parameter) {
        StrParameterHandler<?> handler = parameter.getHandler();

        int tries = 3;
        List<String> options = new ArrayList<>();
        validOptions.forEach(o -> options.add(handler.toStringForce(o)));
        String result = null;

        user.sendMessage(new Str("Pick a " + parameter.getName() + " (" + parameter.getType().getSimpleName() + ")"));
        user.sendMessage(new Str("This query will expire in 15 seconds.", C.G, C.B));

        while (tries-- > 0 && (result == null || !options.contains(result))) {
            user.sendMessage(new Str("Please pick a valid option.", C.G, C.B));

            CompletableFuture<Integer> future = new CompletableFuture<>();
            for (int i = 0; i < options.size(); i++) {
                int finalI = i;
                user.sendMessage(new Str("- " + options.get(i), C.G, C.B, user1 -> {
                    future.complete(finalI);
                }, new Str(options.get(i), C.G, C.B)));
            }
            user.playSound(StrUser.StrSoundEffect.PICK_OPTION);

            try {
                result = options.get(future.get(15, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException ignored) {

            }
        }

        if (result != null && options.contains(result)) {
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).equals(result)) {
                    return validOptions.get(i);
                }
            }
        } else {
            user.sendMessage(new Str(C.R).a("You did not enter a correct option within 3 tries."));
            user.sendMessage(new Str(C.R).a("Please double-check your arguments & option picking."));
        }

        return null;
    }

    /**
     * Validate parameters
     * @param parameters The parameters to validate
     * @param user The user of the command
     * @return True if valid, false if not
     */
    private boolean validateParameters(ConcurrentHashMap<StrVirtualParameter, Object> parameters, StrUser user, ConcurrentHashMap<StrVirtualParameter, StrParseException> parseExceptions) {
        boolean valid = true;
        for (StrVirtualParameter parameter : getParameters()) {
            if (!parameters.containsKey(parameter)) {
                center.debug(new Str(C.R).a("Parameter: ").a(C.B).a(parameter.getName()).a(C.R).a(" not in mapping."));
                Str message = new Str(C.R).a("Parameter: ").a(C.B).a(parameter.help(user));
                if (parseExceptions.containsKey(parameter)) {
                    StrParseException e = parseExceptions.get(parameter);
                    message.a(" (").a(C.B).a(e.getType().getSimpleName()).a(C.R).a(") failed for ").a(C.B).a(e.getInput()).a(C.R).a(". Reason: ").a(C.B).a(e.getReason());
                } else {
                    message.a(" not specified. Please add.");
                }
                user.sendMessage(message);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Parses a parameter into a map after parsing
     * @param parameters The parameter map to store the value into
     * @param parseExceptionArgs Parameters which ran into parseExceptions
     * @param option The parameter type to parse into
     * @param value The value to parse
     * @return True if successful, false if not. Nothing is added on parsing failure.
     */
    private boolean parseParamInto(
            ConcurrentHashMap<StrVirtualParameter, Object> parameters,
            List<String> badArgs,
            ConcurrentHashMap<StrVirtualParameter, StrParseException> parseExceptionArgs,
            StrVirtualParameter option,
            String value,
            StrUser sender
    ) {
        try {
            parameters.put(option, value.equalsIgnoreCase("null") ? nullParam : option.getHandler().parseSafe(value));
            return true;
        } catch (StrWhichException e) {
            center.debug(new Str(C.R).a("Value ").a(C.B).a(value).a(C.R).a(" returned multiple options"));
            if (StrCenter.settings.pickFirstOnMultiple) {
                center.debug(new Str(C.G).a("Adding: ").a(C.B).a(e.getOptions().get(0).toString()));
                parameters.put(option, e.getOptions().get(0));
            } else {
                Object result = pickValidOption(sender, e.getOptions(), option);
                if (result == null) {
                    badArgs.add(option.getDefault());
                } else {
                    parameters.put(option, result);
                }
            }
            return true;
        } catch (StrParseException e) {
            parseExceptionArgs.put(option, e);
        } catch (Throwable e) {
            center.debug(new Str("Failed to parse into: '" + option.getName() + "' value '" + value + "'"));
            e.printStackTrace();
        }
        return false;
    }

    /**
     * List this node and any combination of parameters (disregarding order and value, just the parameter type/name) to form a string-based graph representation.
     * @param prefix prefix all substrings with this prefix, so it aligns with previous nodes
     * @param spacing the space to append to the prefix for parameter combinations
     * @param current the current graph
     * @param exampleInput example input for NGram match scores
     */
    public void getListing(String prefix, String spacing, List<String> current, List<String> exampleInput) {
        current.add(prefix + getName() + (getAliases().isEmpty() ? "" : " (" + getAliases() + ")") + " has " + getParameters().size() + " " + (getParameters().size() > 1 ? "parameters" : "parameter") + " matches with " + exampleInput.get(0) + " @ " + ((double) NGram.nGramMatch(exampleInput.get(0), getName()) / NGram.nGramMatch(getName(), getName())));
        for (int i = 0; i < getParameters().size(); i++) {
            getParameters().get(i).getListing(prefix + spacing, current, exampleInput.get(Math.min(i + 1, exampleInput.size() - 1)));
        }
    }
}
