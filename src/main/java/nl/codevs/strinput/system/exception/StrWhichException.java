package nl.codevs.strinput.system.exception;

import lombok.Getter;

import java.util.List;

/**
 * Thrown when more than one option is available for a singular mapping<br>
 * Like having a hashmap where one input maps to two outputs.
 */
public class StrWhichException extends Exception {
    @Getter
    private final List<?> options;
    public StrWhichException(Class<?> type, String input, List<?> options) {
        super("Cannot parse \"" + input + "\" into type " + type.getSimpleName() + " because of multiple options");
        this.options = options;
    }
}
