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
}
