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

package nl.codevs.strinput.system;

import java.awt.*;
import java.lang.invoke.VarHandle;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Messages passing through the system.
 * Has additional properties such as colors, on-click events and more.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrMessage {

    /**
     * Message text.
     */
    private final List<Component> text;

    /**
     * Create a new message.
     * @param messageText vararg message components forming the text.
     */
    public StrMessage(Component... messageText) {
        text = List.of(messageText);
    }

    /**
     * Create a new message.
     * @param messageText the list of message components forming the text.
     */
    public StrMessage(List<Component> messageText) {
        text = messageText;
    }

    /**
     * Create a new message.
     * @param messageText vararg string forming the text. Does not support colors!
     */
    public StrMessage(String... messageText) {
        text = new ArrayList<>();
        for (String s : messageText) {
            text.add(new Component(s, ComponentType.TEXT));
        }
    }

    /**
     * Return the ordered text components with the specified types.
     * @param types the list of types which components to get
     * @return ordered text components of a type in {@code types}
     */
    public List<Component> getText(ComponentType... types) {
        List<ComponentType> typesList = Arrays.asList(types);
        return text.stream().filter(c -> typesList.contains(c.type)).toList();
    }

    /**
     * Add a string
     * @param newText the next text string
     * @return this
     */
    public StrMessage add(String newText) {
        return add(new Component(newText, ComponentType.TEXT));
    }

    /**
     * Add a component
     * @param newText the new text component
     * @return this
     */
    public StrMessage add(Component newText) {
        text.add(newText);
        return this;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     *
     * @apiNote In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * The string output is not necessarily stable over time or across
     * JVM invocations.
     * @implSpec The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        for (Component component : getText(ComponentType.TEXT)) {
            res.append(component.value);
        }
        return res.toString();
    }

    /**
     * Component.
     */
    public record Component(String value, ComponentType type) {

    }

    /**
     * Component type.
     */
    public enum ComponentType {
        TEXT,
        COLOR
    }

    /**
     * Colors.
     */
    public enum C {
        R("red"),
        G("green"),
        B("blue");

        private final String string;

        C (String full) {
            string = full;
        }

        /**
         * Get a color by name.
         * @param color the name of the color.
         * @return the color
         * @throws InvalidParameterException if the input {@code color} cannot be found
         */
        public static C g(String color) throws InvalidParameterException {
            color = color.toLowerCase(Locale.ROOT);
            for (C value : values()) {
                if (value.name().startsWith(color) || value.string.startsWith(color)) {
                    return value;
                }
            }
            throw new InvalidParameterException("Input color: " + color + " not found.");
        }


        /**
         * Returns the name of this enum constant, as contained in the
         * declaration.  This method may be overridden, though it typically
         * isn't necessary or desirable.  An enum class should override this
         * method when a more "programmer-friendly" string form exists.
         *
         * @return the name of this enum constant
         */
        @Override
        public String toString() {
            return string;
        }
    }
}
