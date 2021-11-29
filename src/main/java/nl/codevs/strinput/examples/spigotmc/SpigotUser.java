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

package nl.codevs.strinput.examples.spigotmc;

import lombok.Getter;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.text.StrClickable;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Spigot user implementation
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class SpigotUser implements StrUser {

    @Getter
    private final Player player;

    public SpigotUser(Player player) {
        this.player = player;
    }

    /**
     * Send a message to the sender.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(Str message) {
        player.sendMessage(strToString(message));
    }

    /**
     * Send multiple options when there is something to choose from.<br>
     * Note that it is required to have an Str.
     *
     * @param clickables the clickable options to send
     */
    @Override
    public void sendOptions(List<StrClickable> clickables) {
        for (StrClickable clickable : clickables) {
            player.sendMessage(clickableToString(clickable));
        }
    }

    /**
     * @return whether this user supports {@link StrClickable}s.
     */
    @Override
    public boolean supportsClickables() {
        return true;
    }

    /**
     * @return Whether this user is a player or not.
     */
    public boolean isPlayer() {
        return player == null;
    }

    /**
     * Turn a {@link Str} to a string.
     * @param message the Str message to convert
     * @return the string
     * TODO: Implement colors
     */
    private String strToString(Str message) {
        return message.toString();
    }

    /**
     * Turn a {@link StrClickable} to a clickable string.
     * @param clickable the StrClickable to convert
     * @return the string
     * TODO: Implement clickable
     */
    private String clickableToString(StrClickable clickable) {
        return strToString(clickable);
    }
}
