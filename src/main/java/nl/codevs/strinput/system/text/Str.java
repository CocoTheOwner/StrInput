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

import nl.codevs.strinput.system.api.StrUser;

import java.util.List;
import java.util.function.Consumer;

/**
 * Messages passing through the system.<br>
 * Has additional properties such as colors, on-click events and hovering.<br><br>
 *
 * Note that the actual text of this component is stored in {@link #content}.<br>
 * The way to read-out strings is by looping over {@link #stringList},<br>
 * which contains other related string components (in order) with potentially different values and properties.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class Str {

    private final Consumer<StrUser> onClick;
    private final Str onHover;
    private String content;
    private final C mainColor;
    private final C gradientColor;
    private final List<Str> stringList;

    public Consumer<StrUser> getOnClick() {
        return onClick;
    }

    /**
     * This is the end color of the gradient. The first color is found in {@link #mainColor}.
     * @return the end color of the gradient
     */
    public C getGradientColor() {
        return gradientColor;
    }

    public C getMainColor() {
        return mainColor;
    }

    public Str getOnHover() {
        return onHover;
    }

    public String getContent() {
        return content;
    }

    public List<Str> getStringList() {
        return stringList;
    }

    //    public Str setOnClick(Consumer<StrUser> runOnClick) {
//        onClick = runOnClick;
//        return this;
//    }
//    public Str resetOnClick() {
//        onClick = null;
//        return this;
//    }
//
//    public Str setOnHover(Str showOnHover) {
//        onHover = showOnHover;
//        return this;
//    }
//    public Str resetOnHover() {
//        onHover = null;
//        return this;
//    }
//
//    public Str setText(String newText) {
//        content = newText;
//        return this;
//    }
//
//    public Str setColor(C color) {
//        mainColor = color;
//        return this;
//    }
//
//    public Str setGradientColor(C startColor, C endColor) {
//        mainColor = startColor;
//        gradientColor = endColor;
//        return this;
//    }

    public Str(String text) {
        this(text, C.RESET);
    }

    public Str(C color) {
        this("", color);
    }
    public Str(String text, C color) {
        this(text, color, C.RESET);
    }

    public Str(C startColor, C endColor) {
        this("", startColor, endColor);
    }

    public Str(String text, C startColor, C endColor) {
        this(text, startColor, endColor, (Consumer<StrUser>) null);
    }

    public Str(Consumer<StrUser> runOnclick) {
        this("", runOnclick);
    }

    public Str(String text, Consumer<StrUser> runOnClick) {
        this(text, C.RESET, runOnClick);
    }

    public Str(C color, Consumer<StrUser> runOnClick) {
        this("", color, C.RESET, runOnClick);
    }

    public Str(C startColor, C endColor, Consumer<StrUser> onClick, Str onHover) {
        this("", startColor, endColor, onClick, onHover);
    }

    public Str(String text, C color, Consumer<StrUser> runOnClick) {
        this(text, color, C.RESET, runOnClick);
    }

    public Str(C startColor, C endColor, Consumer<StrUser> runOnClick) {
        this("", startColor, endColor, runOnClick, null);
    }

    public Str(String text, C startColor, C endColor, Consumer<StrUser> runOnClick) {
        this(text, startColor, endColor, runOnClick, null);
    }

    public Str(Str showOnHover) {
        this("", C.RESET, showOnHover);
    }

    public Str(String text, Str showOnHover) {
        this(text, C.RESET, showOnHover);
    }

    public Str(String text, C color, Str showOnHover) {
        this(text, color, C.RESET, showOnHover);
    }

    public Str(C color, Str showOnHover) {
        this("", color, C.RESET, showOnHover);
    }

    public Str(C color, Consumer<StrUser> runOnClick, Str showOnHover) {
        this("", color, C.RESET, runOnClick, showOnHover);
    }

    public Str(String text, C startColor, C endColor, Str showOnHover) {
        this(text, startColor, endColor, null, showOnHover);
    }

    public Str(C startColor, C endColor, Str showOnHover) {
        this("", startColor, endColor, null, showOnHover);
    }

    public Str(C color, Consumer<StrUser> onClick, Str onHover, List<Str> previous) {
        this("", color, C.RESET, onClick, onHover, previous);
    }

    public Str(C startColor, C endColor, Consumer<StrUser> runOnClick, Str showOnHover, List<Str> previous) {
        this("", startColor, endColor, runOnClick, showOnHover, previous);
    }

    public Str(String text, C startColor, C endColor, Consumer<StrUser> runOnClick, Str showOnHover) {
        this(text, startColor, endColor, runOnClick, showOnHover, null);
    }

    public Str(String text, C startColor, C endColor, Consumer<StrUser> runOnClick, Str showOnHover, List<Str> strings) {
        content = text;
        mainColor = startColor;
        gradientColor = endColor;
        onClick = runOnClick;
        onHover = showOnHover;
        stringList = strings;
    }

    /**
     * Add a new string / new strings.
     * @param newText the next text string(s)
     * @return this
     */
    public Str add(String... newText) {
        content += " " + String.join(" ", newText);
        return this;
    }

    /**
     * Add a new string / new strings.
     * @param newText the next text string(s)
     * @return this
     */
    public Str a(String... newText){
        return add(newText);
    }

    /**
     * Add a new {@link Str} / new {@link Str}s.
     * @param newText the new text component(s)
     * @return this
     */
    public Str add(Str... newText) {
        stringList.addAll(List.of(newText));
        return this;
    }

    /**
     * Add a new {@link Str} / new {@link Str}s.
     * @param newText the new text component(s)
     * @return this
     */
    public Str a(Str... newText) {
        return add(newText);
    }

    /**
     * Add a new color, which will affect text added from here on out.<br>
     * This keeps any hover or on-click effect the previous node may have.
     * @param color the color to add
     * @return the {@link Str}
     */
    public Str add(C color) {
        Str newColored = new Str(color, onClick, onHover, stringList);
        stringList.add(newColored);
        return newColored; // because new text should be of the new color
    }
    /**
     * Add a new color, which will affect text added from here on out.<br>
     * This keeps any hover or on-click effect the previous node may have.
     * @param color the color to add
     * @return the {@link Str}
     */
    public Str a(C color) {
        return add(color);
    }

    /**
     * Add a new color gradient, which will affect text added from here on out.<br>
     * This keeps any hover or on-click effect the previous node may have.
     * @param startColor the start of the gradient color
     * @param endColor the end of the gradient color
     * @return the {@link Str}
     */
    public Str add(C startColor, C endColor) {
        Str newGradient = new Str(startColor, endColor, onClick, onHover, stringList);
        stringList.add(newGradient);
        return newGradient;
    }

    /**
     * Returns a string representation of the object.<br>
     * Note that this removes any non-text component from the {@link Str}.
     *
     * @return a string representation of the object.
     */
    public String toHumanReadable() {
        return String.join(" ", stringList.stream().map(s -> s.content).toList());
    }
}
