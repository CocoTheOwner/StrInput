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

import nl.codevs.strinput.system.exception.StrNoParameterHandlerException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Str parameter handling.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrParameter {

    private static List<StrParameterHandler<?>> parameterHandlers = new ArrayList<>();

    /**
     * Get handler for a type.
     * @param type the type to get the handler for
     * @return the parameter handler for the type
     * @throws StrNoParameterHandlerException if no parameter handler could be found
     */
    public static StrParameterHandler<?> getHandler(Class<?> type) throws StrNoParameterHandlerException {
        for (StrParameterHandler<?> parameterHandler : parameterHandlers) {
            if (parameterHandler.supports(type)) {
                return parameterHandler;
            }
        }
        throw new StrNoParameterHandlerException(type);
    }

    /**
     * Register new parameter handlers.
     * @param handlers the parameter handlers
     */
    public static void register(StrParameterHandler<?>... handlers) {
        parameterHandlers.addAll(List.of(handlers));
    }
}
