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

/**
 * Colors.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class C extends Str.Component {
    public static final String COLOR_PREFIX = "$2^@#%&#%$&$&$&%";
    public static final C R = new C("red");
    public static final C G = new C("green");
    public static final C B = new C("blue");
    public static final C Y = new C("yellow");
    public static final C GOLD = new C("gold");
    public static final C RESET = new C("reset");

    public C(String full) {
        super(COLOR_PREFIX + full, Str.ComponentType.COLOR);
    }

    /**
     * Gradient color.
     *
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class Gradient extends Str.Component {

        final C start;
        final C end;

        /**
         * Constructs a gradient color. Calls {@link Str.Component#Component(String, Str.ComponentType)} with {@code start.}{@link Str.Component#value} {@code + end.}{@link Str.Component#value}{@code , }{@link Str.ComponentType#GRADIENT}
         * @param start the start color.
         * @param end
         */
        public Gradient(C start, C end) {
            super(start.value + end.value, Str.ComponentType.GRADIENT);
            this.start = start;
            this.end = end;
        }
    }
}
