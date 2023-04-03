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

import net.kyori.adventure.text.TextComponent;
import nl.codevs.strinput.system.Context;
import nl.codevs.strinput.system.StrUser;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

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
    public @NotNull String getName() {
        return "Spigot Console";
    }

    /**
     * Send a message to the user.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(@NotNull TextComponent message) {
        if (Context.center() instanceof SpigotCenter spigotCenter) {
            spigotCenter.getAudiences().console().sendMessage(message);
        } else {
            throw new RuntimeException("Found non-SpigotCenter for SpigotConsole!");
        }
    }

    /**
     * @return whether this user supports clickable messages.
     */
    @Override
    public boolean replaceClickable() {
        return true;
    }


    /**
     * Play a sound effect.
     *
     * @param sfx the sound effect type
     */
    @Override
    public void playSound(@NotNull final StrSoundEffect sfx) {

    }

    /**
     * If this sender supports context,
     * i.e. has values it stores for getting data automatically
     * (instead of specifying it in commands).
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
    public boolean hasPermission(@NotNull final String permission) {
        return true;
    }
}
