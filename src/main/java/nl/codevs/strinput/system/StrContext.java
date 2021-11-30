package nl.codevs.strinput.system;

import nl.codevs.strinput.system.context.StrContextHandler;
import nl.codevs.strinput.system.exception.StrNoParameterHandlerException;

import java.util.ArrayList;
import java.util.List;

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
