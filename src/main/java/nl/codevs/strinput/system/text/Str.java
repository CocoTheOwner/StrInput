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

package nl.codevs.strinput.system.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Messages passing through the system.
 * Has additional properties such as colors, on-click events and more.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class Str {

    /**
     * Message text.
     */
    private final List<Component> text;

    /**
     * Create a new message.
     * @param messageText vararg message components forming the text.
     */
    public Str(Component... messageText) {
        text = List.of(messageText);
    }

    /**
     * Create a new message.
     * @param messageText the list of message components forming the text.
     */
    public Str(List<Component> messageText) {
        text = messageText;
    }

    /**
     * Create a new message.
     * @param messageText vararg string forming the text. Does not support colors!
     */
    public Str(String... messageText) {
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
    public Str add(String newText) {
        return add(new Component(newText, ComponentType.TEXT));
    }
    public Str a(String newText){
        return add(newText);
    }

    /**
     * Add a component
     * @param newText the new text component
     * @return this
     */
    public Str add(Component newText) {
        text.add(newText);
        return this;
    }
    public Str a(Component newText){
        return add(newText);
    }

    /**
     * Add a Str to another Str.
     * @param newText the new text components
     * @return this
     */
    public Str add(Str newText) {
        for (Component component : newText.text) {
            add(component);
        }
        return this;
    }
    public Str a(Str input) {
        return add(input);
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
    public static class Component {
        final String value;
        final ComponentType type;
        public Component(String textValue, ComponentType componentType) {
            value = textValue;
            type = componentType;
        }
    }

    /**
     * Component type.
     */
    public enum ComponentType {
        TEXT,
        COLOR
    }
}
