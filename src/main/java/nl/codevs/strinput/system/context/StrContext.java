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

import nl.codevs.strinput.system.parameter.StrNoParameterHandlerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Str context handling.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrContext {

    /**
     * Context handler list.
     */
    private static final List<StrContextHandler<?>> contextHandlers = new ArrayList<>();

    /**
     * Get context handler for a type.
     * @param type the type to get the context handler for
     * @return the context handler for the type
     * @throws StrNoParameterHandlerException if no context handler could be found
     */
    public static StrContextHandler<?> getContextHandler(Class<?> type) throws StrNoContextHandlerException {
        for (StrContextHandler<?> parameterHandler : contextHandlers) {
            if (parameterHandler.supports(type)) {
                return parameterHandler;
            }
        }
        throw new StrNoContextHandlerException(type);
    }

    /**
     * Register new context handlers.
     * @param handlers the context handlers
     */
    public static void register(StrContextHandler<?>... handlers) {
        contextHandlers.addAll(List.of(handlers));
    }
}
