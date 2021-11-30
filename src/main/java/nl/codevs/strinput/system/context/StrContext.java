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

package nl.codevs.strinput.system.context;

import nl.codevs.strinput.system.exception.StrNoParameterHandlerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Str context handling.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrContext extends ArrayList<StrContextHandler<?>> {

    /**
     * Setup context handlers.
     * @param contextHandlers the context handlers
     */
    public StrContext(StrContextHandler<?>[] contextHandlers) {
        addAll(List.of(contextHandlers));
    }

    /**
     * Get context handler for a type.
     * @param type the type to get the context handler for
     * @return the context handler for the type
     * @throws StrNoParameterHandlerException if no context handler could be found
     */
    public StrContextHandler<?> getContextHandler(Class<?> type) throws StrNoParameterHandlerException {
        for (StrContextHandler<?> parameterHandler : this) {
            if (parameterHandler.supports(type)) {
                return parameterHandler;
            }
        }
        throw new StrNoParameterHandlerException(type);
    }
}
