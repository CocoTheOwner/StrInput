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

import nl.codevs.strinput.system.util.AtomicCache;
import nl.codevs.strinput.system.api.Param;
import nl.codevs.strinput.system.api.StrCenter;
import nl.codevs.strinput.system.exception.StrNoParameterHandlerException;
import nl.codevs.strinput.system.exception.StrParseException;
import nl.codevs.strinput.system.exception.StrWhichException;
import nl.codevs.strinput.system.parameter.StrParameterHandler;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

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
    private final Param param;
    /**
     * Handler cache.
     */
    private final AtomicCache<StrParameterHandler<?>> handlerCache = new AtomicCache<>();
    /**
     * Example cache.
     */
    private final AtomicCache<List<String>> exampleCache = new AtomicCache<>();
    /**
     * Command center.
     */
    private final StrCenter center;

    /**
     * Create a virtual parameter.<br>
     * Assumes {@code parameter} is annotated by @{@link Param}.
     * @param parameter the parameter
     * @param center the command center
     */
    public StrVirtualParameter(Parameter parameter, StrCenter center) {
        this.center = center;
        this.parameter = parameter;
        this.param = parameter.getDeclaredAnnotation(Param.class);
    }

    /**
     * Get the handler for this parameter.
     * @return a parameter handler for this type, or null if none exists for it.
     */
    public StrParameterHandler<?> getHandler() {
        return handlerCache.acquire(() -> {
            try {
                return center.parameter.getHandler(parameter.getType());
            } catch (StrNoParameterHandlerException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * Get a list of example values for this parameter
     * @return A list of example values
     */
    public List<String> getExamples() {
        return exampleCache.acquire(() -> {
            List<String> examples = new ArrayList<>();

            if (getHandler().getPossibilities() == null) {
                examples.add(getHandler().getRandomDefault());
                return examples;
            }

            getHandler().getPossibilities().forEach(p -> {
                examples.add(getHandler().toStringForce(p));
            });

            if (examples.isEmpty()) {
                examples.add(getHandler().getRandomDefault());
            }

            return examples;
        });
    }

    /**
     * Get the type of the parameter.
     * @return the type of the parameter
     */
    public Class<?> getType() {
        return parameter.getType();
    }

    /**
     * Get the default string.
     * @return the default string
     */
    public String getDefault() {
        return param.defaultValue().trim();
    }

    /**
     * Get if the parameter has a default.
     * @return true if the parameter has a default
     */
    public boolean hasDefault() {
        return getDefault().isEmpty();
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
     * @return an instance of the parameter type
     * @throws StrParseException thrown when parsing fails
     * @throws StrWhichException thrown when multiple options are possible
     */
    public Object getDefaultValue() throws StrParseException, StrWhichException {
        return hasDefault() ? getHandler().parseSafe(getDefault()) : null;
    }

    /**
     * Get the description of this parameter.
     * @return the description
     */
    public String getDescription() {
        return param.description().isBlank() ? Param.DEFAULT_DESCRIPTION : param.description();
    }

    /**
     * Get the name of this parameter.
     * @return the name
     */
    public String getName() {
        return param.name().isBlank() ? parameter.getName() : param.name();
    }

    /**
     * Get all names referencing this command.<br>
     * Consists of the main name and non-blank aliases.
     * @return the names
     */
    public List<String> getNames() {
        List<String> names = new ArrayList<>();
        names.add(getName());
        for (String alias : param.aliases()) {
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
     * Make sure there is a {@link nl.codevs.strinput.system.context.StrContextHandler} available for this type.
     * @return true if the parameter is contextual
     */
    public boolean isContextual() {
        return param.contextual();
    }
}