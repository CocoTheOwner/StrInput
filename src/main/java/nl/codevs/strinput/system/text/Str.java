/*
 * This file is part of the Strinput distribution.
 * (https://github.com/CocoTheOwner/Strinput)
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Messages passing through the system.<br>
 * Has additional properties such as colors,
 * on-click events and hovering.<br><br>
 *
 * Note that the actual text of this component
 * is stored in {@link #content}.<br>
 * The way to read-out strings is by going back to
 * {@link #previous} and getting its content, and then back to...<br>
 * which contains other related string components (in order)
 * with potentially different values and properties.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class Str {

    /**
     * Runnable that should be run when this is clicked.
     */
    private final Runnable onClick;

    /**
     * Another {@link Str} that should be
     * displayed when hovering over this text.
     */
    private final Str onHover;

    /**
     * The main color of this text.
     */
    private final C mainColor;

    /**
     * If {@link C#RESET} then {@link #mainColor}
     * is the only color and full color of this {@link Str}.
     * Otherwise, this is the color at the end
     * of the text, i.e. the end of the gradient.
     */
    private final C gradientColor;

    /**
     * The previous {@link Str} in the chain. Can be null.
     */
    private Str previous;

    /**
     * The string content of this {@link Str}.
     */
    private String content;

    /**
     * Get the main color of the {@link Str}.
     * @return the main color
     */
    public C getMainColor() {
        return mainColor;
    }

    /**
     * This is the end color of the gradient.
     * The first color is found in {@link #mainColor}.<br>
     * Make sure to check {@link #isGradient()}
     * to ensure this {@link Str} is actually gradient.
     * @return the end color of the gradient
     */
    public C getGradientColor() {
        return gradientColor;
    }

    /**
     * Get what to display on hover ({@link Str}).
     * @return what to display on hover
     */
    public Str getOnHover() {
        return onHover;
    }

    /**
     * Reset hover (creates a new {@link Str} without hover).
     * @return a new Str without hover
     */
    public Str resetHover() {
        return new Str(
                "",
                getMainColor(),
                getGradientColor(),
                getOnClick(),
                null,
                this
        );
    }

    /**
     * Get the text content of this {@link Str}.
     * @return the text content
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the previous {@link Str} in the chain.
     * @return the previous Str
     */
    public Str getPrevious() {
        return previous;
    }

    /**
     * Get the {@link Runnable} to run on click.
     * @return the consumer to run on click
     */
    public Runnable getOnClick() {
        return onClick;
    }

    /**
     * Reset click (creates a new {@link Str} without on-click).
     * @return a new Str without on-click
     */
    public Str resetClick() {
        return new Str(
                "",
                getMainColor(),
                getGradientColor(),
                null,
                getOnHover(),
                this
        );
    }

    /**
     * Get whether this {@link Str} is gradient or not.
     * @return true if it is gradient
     */
    public boolean isGradient() {
        return gradientColor != C.RESET;
    }

    /**
     * Reset color (creates a new {@link Str} without color and color gradient).
     * @return a new Str without color
     */
    public Str resetColor() {
        return new Str("", C.RESET, C.RESET, getOnClick(), getOnHover(), this);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     */
    public Str(@NotNull final String text) {
        this(text, C.RESET);
    }

    /**
     * Create a new {@link Str} string.
     * @param color the starting color
     */
    public Str(@NotNull final C color) {
        this("", color);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param color the starting color
     */
    public Str(@NotNull final String text, @NotNull final C color) {
        this(text, color, C.RESET);
    }

    /**
     * Create a new {@link Str} string.
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     */
    public Str(@NotNull final C startColor, @NotNull final C endColor) {
        this("", startColor, endColor);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     */
    public Str(
            @NotNull final String text,
            @NotNull final C startColor,
            @NotNull final C endColor
    ) {
        this(text, startColor, endColor, (Runnable) null);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param runOnClick the {@link Runnable} to run when this is clicked
     */
    public Str(
            @NotNull final String text,
            @Nullable final Runnable runOnClick) {
        this(text, C.RESET, runOnClick);
    }

    /**
     * Create a new {@link Str} string.
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param runOnClick the {@link Runnable} to run when this is clicked
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final C startColor,
            @NotNull final C endColor,
            @Nullable final Runnable runOnClick,
            @Nullable final Str showOnHover
    ) {
        this("", startColor, endColor, runOnClick, showOnHover);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param color the starting color
     * @param runOnClick the {@link Runnable} to run when this is clicked
     */
    public Str(
            @NotNull final String text,
            @NotNull final C color,
            @Nullable final Runnable runOnClick
    ) {
        this(text, color, C.RESET, runOnClick);
    }

    /**
     * Create a new {@link Str} string.
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param runOnClick the {@link Runnable} to run when this is clicked
     */
    public Str(
            @NotNull final C startColor,
            @NotNull final C endColor,
            @Nullable final Runnable runOnClick
    ) {
        this("", startColor, endColor, runOnClick, null);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param runOnClick the {@link Runnable} to run when this is clicked
     */
    public Str(
            @NotNull final String text,
            @NotNull final C startColor,
            @NotNull final C endColor,
            @Nullable final Runnable runOnClick
    ) {
        this(text, startColor, endColor, runOnClick, null);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final String text,
            @NotNull final Str showOnHover
    ) {
        this(text, C.RESET, showOnHover);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param color the starting color
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final String text,
            @NotNull final C color,
            @NotNull final Str showOnHover
    ) {
        this(text, color, C.RESET, showOnHover);
    }

    /**
     * Create a new {@link Str} string.
     * @param color the starting color
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final C color,
            @NotNull final Str showOnHover
    ) {
        this("", color, C.RESET, showOnHover);
    }

    /**
     * Create a new {@link Str} string.
     * @param color the starting color
     * @param runOnClick the {@link Runnable} to run when this is clicked
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final C color,
            @Nullable final Runnable runOnClick,
            @Nullable final Str showOnHover
    ) {
        this("", color, C.RESET, runOnClick, showOnHover);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final String text,
            @NotNull final C startColor,
            @NotNull final C endColor,
            @NotNull final Str showOnHover
    ) {
        this(text, startColor, endColor, null, showOnHover);
    }

    /**
     * Create a new {@link Str} string.
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final C startColor,
            @NotNull final C endColor,
            @NotNull final Str showOnHover
    ) {
        this("", startColor, endColor, null, showOnHover);
    }

    /**
     * Create a new {@link Str} string.
     * @param color the starting color
     * @param runOnClick the {@link Runnable} to run when this is clicked
     * @param showOnHover what to display on hover
     * @param last the previous {@link Str} in the series
     */
    public Str(
            @NotNull final C color,
            @Nullable final Runnable runOnClick,
            @Nullable final Str showOnHover,
            @Nullable final Str last
    ) {
        this("", color, C.RESET, runOnClick, showOnHover, last);
    }

    /**
     * Create a new {@link Str} string.
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param runOnClick the {@link Runnable} to run when this is clicked
     * @param showOnHover what to display on hover
     * @param last the previous {@link Str} in the series
     */
    public Str(
            @NotNull final C startColor,
            @NotNull final C endColor,
            @Nullable final Runnable runOnClick,
            @Nullable final Str showOnHover,
            @Nullable final Str last
    ) {
        this("", startColor, endColor, runOnClick, showOnHover, last);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param runOnClick the {@link Runnable} to run when this is clicked
     * @param showOnHover what to display on hover
     */
    public Str(
            @NotNull final String text,
            @NotNull final C startColor,
            @NotNull final C endColor,
            @Nullable final Runnable runOnClick,
            @Nullable final Str showOnHover
    ) {
        this(text, startColor, endColor, runOnClick, showOnHover, null);
    }

    /**
     * Create a new {@link Str} string.
     * @param text the initial text content
     * @param startColor the starting color
     * @param endColor the end color of the gradient
     *                ({@link C#RESET} if non-gradient)
     * @param runOnClick the {@link Runnable} to run when this is clicked
     * @param showOnHover what to display on hover
     * @param last the previous {@link Str} in the series
     */
    public Str(
            @NotNull final String text,
            @NotNull final C startColor,
            @NotNull final C endColor,
            @Nullable final Runnable runOnClick,
            @Nullable final Str showOnHover,
            @Nullable final Str last
    ) {
        content = text;
        mainColor = startColor;
        gradientColor = endColor;
        onClick = runOnClick;
        onHover = showOnHover;
        previous = last;
    }

    /**
     * Add a new string / new strings.
     * @param newText the next text string(s)
     * @return this
     */
    public @NotNull Str add(@NotNull final String... newText) {
        content += String.join(" ", newText);
        return this;
    }

    /**
     * Add a new string / new strings.
     * @param newText the next text string(s)
     * @return this
     */
    public @NotNull Str a(@NotNull final String... newText) {
        return add(newText);
    }

    /**
     * Add a new {@link Str} / new {@link Str}s.<br>
     * @param newText the new text component
     * @return this
     */
    public @NotNull Str add(@NotNull final Str newText) {
        Str last = newText;
        while (last.getPrevious() != null) {
            last = last.getPrevious();
        }
        last.previous = this;
        return newText;
    }
    /**
     * Add a new {@link Str} / new {@link Str}s.
     * @param newText the new text component(s)
     * @return this
     */
    public @NotNull Str a(@NotNull final Str newText) {
        return add(newText);
    }

    /**
     * Add a new color, which will affect text added from here on out.<br>
     * This keeps any hover or on-click effect the previous node may have.
     * @param color the color to add
     * @return the {@link Str}
     */
    public @NotNull Str add(@NotNull final C color) {
        return add(color, C.RESET);
    }
    /**
     * Add a new color, which will affect text added from here on out.<br>
     * This keeps any hover or on-click effect the previous node may have.
     * @param color the color to add
     * @return the {@link Str}
     */
    public @NotNull Str a(@NotNull final C color) {
        return add(color);
    }

    /**
     * Add a new color gradient,
     * which will affect text added from here on out.<br>
     * This keeps any hover or on-click effect the previous node may have.
     * @param startColor the start of the gradient color
     * @param endColor the end of the gradient color
     * @return the {@link Str}
     */
    public @NotNull Str add(
            @NotNull final C startColor,
            @NotNull final C endColor
    ) {
        return new Str("", startColor, endColor, onClick, onHover, this);
    }

    /**
     * Returns a string representation of the object.<br>
     * Note that this removes any non-text component from the {@link Str}.
     *
     * @return a string representation of the object.
     */
    public @NotNull String toHumanReadable() {
        if (previous == null) {
            return content;
        } else {
            return previous.toHumanReadable() + content;
        }
    }


    /**
     * Make a copy of this {@link Str}
     * to prevent previous copies from being modified.
     *
     * @return a copy of this
     */
    public @NotNull Str copy() {
        return copy(true);
    }

    /**
     * Make a copy of this {@link Str}
     * to prevent previous copies from being modified.
     * @param deep also makes copies of previous nodes
     *
     * @return the last (right-most) node in the chain;
     * a copy of the {@link Str} this was run on.
     */
    public @NotNull Str copy(final boolean deep) {
        if (deep && getPrevious() != null) {
            Str deeper = getPrevious().copy(true);
            return new Str(
                    content,
                    mainColor,
                    gradientColor,
                    onClick,
                    onHover,
                    deeper
            );
        } else {
            return new Str(
                    content,
                    mainColor,
                    gradientColor,
                    onClick,
                    onHover,
                    previous
            );
        }
    }
}
