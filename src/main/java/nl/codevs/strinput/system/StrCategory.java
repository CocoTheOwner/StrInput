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
package nl.codevs.strinput.system;


import org.jetbrains.annotations.NotNull;

/**
 * Command and/or command category interface.
 * Register commands in your
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public interface StrCategory {

    /**
     * @return The sender of the command, the user.
     */
    @NotNull default StrUser user() {
        return Context.user();
    }

    /**
     * @return The command center running the system.
     */
    @SuppressWarnings("unused")
    @NotNull default StrCenter center() {
        return Context.center();
    }

}
