package nl.codevs.strinput.system.exceptions;

import lombok.Getter;
import nl.codevs.strinput.system.contexts.StrContextHandler;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;

/**
 * Thrown when a decree parameter is parsed, but parsing fails
 */
@Getter
public class StrParseException extends Exception {
    private final Class<?> type;
    private final Str input;
    private final Str reason;
    private final Str message;

    public StrParseException(Class<?> type, String input, Throwable reason) {
        this(type, input, reason.getClass().getSimpleName() + " - " + reason.getMessage());
    }
    public StrParseException(Class<?> type, String input, String reason) {
        super();
        this.type = type;
        this.input = new Str(input);
        this.reason = new Str(reason);
        this.message = new Str(C.GOLD).a("Could not parse ").a(C.GOLD).a(input).a(C.R).a(" (").a(C.GOLD).a(type.getSimpleName()).a(C.R).a(") because of: ").a(C.GOLD).a(reason);
    }
}
