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
package nl.codevs.strinput.examples.discord;

import nl.codevs.strinput.system.StrCategory;
import nl.codevs.strinput.system.StrInput;

/**
 * Example implementation of a category for Discord.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@StrInput(name = "strinput", description = "example commands", aliases = "str")
public class DiscordCommands implements StrCategory {

    @StrInput(name = "ping", description = "pong!")
    public void ping() {

    }
}
