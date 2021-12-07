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

import nl.codevs.strinput.system.api.StrCenter;
import nl.codevs.strinput.system.api.StrUser;
import nl.codevs.strinput.system.text.Str;
import org.bukkit.Bukkit;

/**
 * Spigot console user.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class SpigotConsole implements StrUser {
    /**
     * The name of the user (something to identify them by).
     *
     * @return the name of the user
     */
    @Override
    public String getName() {
        return "StrInput console sender";
    }

    /**
     * Send a message to the user.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(Str message) {
        Bukkit.getConsoleSender().sendMessage(message.toHumanReadable());
    }

    /**
     * @return whether this user supports clickable {@link Str}s.
     */
    @Override
    public boolean supportsClickables() {
        return false;
    }

    /**
     * Play a sound effect
     *
     * @param sfx the sound effect type
     */
    @Override
    public void playSound(StrSoundEffect sfx) {

    }

    /**
     * If this sender supports context, i.e. has values it stores for getting data automatically (instead of specifying it in commands).
     * See {@link StrCenter.ContextHandling}.
     *
     * @return true if the user supports context
     */
    @Override
    public boolean supportsContext() {
        return false;
    }

    /**
     * Whether this user has permission for a certain node or not.
     *
     * @param permission the permissions node
     * @return true if permitted.
     */
    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
}
