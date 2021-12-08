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

import nl.codevs.strinput.system.api.StrCenter;
import nl.codevs.strinput.system.api.StrUser;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.util.AtomicCache;
import nl.codevs.strinput.system.api.Param;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import nl.codevs.strinput.system.util.NGram;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link Param} annotated method parameter's virtual representation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class StrVirtualParameter {

    /**
     * The underlying parameter.
     */
    private final Parameter parameter;
    /**
     * The annotation.
     */
    private final Param annotation;
    /**
     * Handler cache.
     */
    private final AtomicCache<StrParameterHandler<?>> handlerCache
            = new AtomicCache<>();
    /**
     * Example cache.
     */
    private final AtomicCache<List<String>> exampleCache = new AtomicCache<>();

    /**
     * Create a virtual parameter.<br>
     * Assumes {@code parameter} is annotated by @{@link Param}.
     * @param param the parameter
     */
    public StrVirtualParameter(final @NotNull Parameter param) {
        this.parameter = param;
        this.annotation = param.getDeclaredAnnotation(Param.class);
    }

    /**
     * Get the handler for this parameter.
     * @return a parameter handler for this type, or null if none exists for it.
     */
    public StrParameterHandler<?> getHandler() {
        return handlerCache.acquire(() -> {
            try {
                return StrCenter.ParameterHandling
                        .getHandler(parameter.getType());
            } catch (StrCenter.ParameterHandling
                    .StrNoParameterHandlerException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Get a list of example values for this parameter.
     * @return A list of example values
     */
    public List<String> getExamples() {
        return exampleCache.acquire(() ->
                getHandler().getPossibilities()
                        .stream()
                        .map(p -> getHandler().toStringForce(p))
                        .collect(Collectors.toList())
        );
    }

    /**
     * Get the type of the parameter.
     * @return the type of the parameter
     */
    public @NotNull Class<?> getType() {
        return parameter.getType();
    }

    /**
     * Get the default string.
     * @return the default string
     */
    public @NotNull String getDefault() {
        return annotation.defaultValue().trim();
    }

    /**
     * Get if the parameter has a default.
     * @return true if the parameter has a default
     */
    public boolean hasDefault() {
        return !getDefault().isEmpty();
    }

    /**
     * Get if the parameter is required.
     * @return true if the parameter has no default
     */
    public boolean isRequired() {
        return !hasDefault();
    }

    /**
     * Get default value for this parameter.
     * {@code null} if there is none (check {@link #hasDefault()} first).
     * @return an instance of the parameter type
     * @throws StrParameterHandler.StrParseException
     * thrown when parsing fails
     * @throws StrParameterHandler.StrWhichException
     * thrown when multiple options are possible
     */
    public @Nullable Object getDefaultValue() throws
            StrParameterHandler.StrParseException,
            StrParameterHandler.StrWhichException {
        return hasDefault() ? getHandler().parseSafe(getDefault()) : null;
    }

    /**
     * Get the description of this parameter.
     * @return the description
     */
    public String getDescription() {
        return annotation.description().isBlank()
                ? Param.DEFAULT_DESCRIPTION
                : annotation.description();
    }

    /**
     * Get the name of this parameter.
     * @return the name
     */
    public String getName() {
        return annotation.name().isBlank()
                ? parameter.getName()
                : annotation.name();
    }

    /**
     * Get all names referencing this command.<br>
     * Consists of the main name and non-blank aliases.
     * @return the names
     */
    public @NotNull List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add(getName());
        for (String alias : annotation.aliases()) {
            if (alias.isBlank()) {
                continue;
            }
            if (names.contains(alias)) {
                continue;
            }
            names.add(alias);
        }
        return names;
    }

    /**
     * Get if the parameter is contextual.
     * Make sure there is a
     * {@link nl.codevs.strinput.system.context.StrContextHandler}
     * available for this type.
     * These are to be registered with the constructor on the command center.
     * @return true if the parameter is contextual
     */
    public boolean isContextual() {
        return annotation.contextual();
    }

    /**
     * Get help for a user.
     * @param user the user to get help for
     * @return the help
     */
    @Contract("_ -> new")
    public @NotNull Str help(@NotNull final StrUser user) {
        return new Str("Node help of " + getName() + " for " + user.getName());
    }

    /**
     * List this parameter (just the parameter type/name)
     * to form a string-based graph representation.
     * @param prefix prefix all substrings with this prefix,
     *              so it aligns with previous nodes
     * @param current the current graph
     * @param exampleMatch an example to match this against
     */
    public void getListing(
            @NotNull final String prefix,
            @NotNull final List<String> current,
            @NotNull final String exampleMatch
    ) {
        current.add(prefix + getName()
                + " of type '" + getType().getSimpleName() + "'"
                + (
                        hasDefault()
                                ? " defaults to '" + getDefault() + "'"
                                : " has no default"
                )
                + " and " + (
                        isContextual()
                                ? "is contextual"
                                : "is not contextual"
                )
                + " matches with " + exampleMatch
                + " @ " + ((double) NGram.nGramMatch(
                        exampleMatch, getName()
                ) / NGram.nGramMatch(
                        getName(), getName())
                )
        );
    }
}
