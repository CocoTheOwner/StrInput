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
package nl.codevs.strinput.examples.spigotmc.command;

import nl.codevs.strinput.examples.spigotmc.SpigotUser;
import nl.codevs.strinput.system.api.StrCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Spigot command category with auxiliary commands.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public interface SpigotCommandCategory extends StrCategory {

    /**
     * @return The player instance of the user (null if not a player).
     */
    default Player player() {
        return ((SpigotUser) user()).getPlayer();
    }

    /**
     * @return The world of the player (null if not a player).
     */
    default World world() {
        return ((SpigotUser) user()).isPlayer() ? player().getWorld() : null;
    }

}
