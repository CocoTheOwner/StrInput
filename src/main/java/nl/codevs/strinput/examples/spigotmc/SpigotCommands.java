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
package nl.codevs.strinput.examples.spigotmc;

import nl.codevs.strinput.system.Param;
import nl.codevs.strinput.system.StrInput;
import org.bukkit.entity.Player;

/**
 * Some spigot commands that players could run.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@StrInput(description = "Main command class", name = "plugin")
public final class SpigotCommands implements SpigotCommandCategory {

    /**
     * Kill a player.
     * @param player the player to kill
     */
    @StrInput(description = "Kill a player", aliases = "k")
    public void kill(
            @Param(
                    description = "The player to kill",
                    name = "player"
            )
            final Player player
    ) {
        player.setHealth(0);
    }

    /**
     * Max player health.
     */
    private static final int MAX_PLAYER_HEALTH = 20;

    /**
     * Heal a player.
     * @param player the player to heal
     */
    @StrInput(description = "Heal a player")
    public void heal(
            @Param(
                    description = "The player to heal",
                    name = "player"
            )
            final Player player
    ) {
        player.setHealth(MAX_PLAYER_HEALTH);
    }
}
