package nl.codevs.strinput.system.parameter;

import nl.codevs.strinput.system.exception.StrNoParameterHandlerException;
import nl.codevs.strinput.system.parameter.StrParameterHandler;

import java.util.ArrayList;
import java.util.List;

public class StrParameter extends ArrayList<StrParameterHandler<?>> {

    public StrParameter(StrParameterHandler<?>[] handlers) {
        addAll(List.of(handlers));
    }

    /**
     * Get handler for a type.
     * @param type the type to get the handler for
     * @return the parameter handler for the type
     * @throws StrNoParameterHandlerException if no parameter handler could be found
     */
    public StrParameterHandler<?> getHandler(Class<?> type) throws StrNoParameterHandlerException {
        for (StrParameterHandler<?> parameterHandler : this) {
            if (parameterHandler.supports(type)) {
                return parameterHandler;
            }
        }
        throw new StrNoParameterHandlerException(type);
    }


}
