/*
 * This file is part of the StrInput distribution.
 * (https://github.com/CocoTheOwner/StrInput)
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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Clickable and on-hover information messages. Wraps a {@link String}.
 * <p>
 * Note that the actual text of this component
 * is stored in {@link #content}.<br>
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public record Str(String content, String onHover, Runnable onClick) {

    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        return "Str{" +
                "content='" + content + '\'' +
                ", onHover='" + onHover + '\'' +
                ", onClick=" + onClick +
                '}';
    }
}
