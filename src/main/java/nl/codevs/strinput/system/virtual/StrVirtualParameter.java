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

import nl.codevs.strinput.system.Param;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.exceptions.StrNoParameterHandlerException;
import nl.codevs.strinput.system.parameters.StrParameterHandler;

import java.lang.reflect.Parameter;

/**
 * A {@link Param} annotated method parameter's virtual representation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrVirtualParameter {

    /**
     * The underlying parameter.
     */
    private final Parameter parameter;
    /**
     * The annotation.
     */
    private final Param param;

    /**
     * Create a virtual parameter.<br>
     * Assumes {@code parameter} is annotated by @{@link Param}.
     * @param parameter the parameter
     */
    public StrVirtualParameter(Parameter parameter) {
        this.parameter = parameter;
        this.param = parameter.getDeclaredAnnotation(Param.class);
    }

    /**
     * Get the handler for this parameter.
     * @return a parameter handler for this type, or null if none exists for it.
     */
    public StrParameterHandler<?> getHandler() {
        try {
            return StrCenter.getHandler(parameter.getType());
        } catch (StrNoParameterHandlerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
