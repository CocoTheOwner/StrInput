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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

/**
 * Instance of a user (the sender/receiver, end-user client).
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public interface StrUser {

    /**
     * The name of the user (something to identify them by).
     * @return the name of the user
     */
    @NotNull String getName();

    /**
     * Send a message to the user.
     * @param message the message to send
     */
    default void sendMessage(@NotNull String message) {
        sendMessage(Component.text(message));
    }

    /**
     * Send a message to the user.
     */
    void sendMessage(@NotNull TextComponent message);

    /**
     * @return whether this user supports clicking elements.
     */
    boolean supportsClickable();

    /**
     * Play a sound effect.
     * @param sfx the sound effect type
     */
    void playSound(@NotNull StrSoundEffect sfx);

    /**
     * If this sender supports context,
     * i.e. has values it stores for getting data automatically
     * (instead of specifying it in commands).
     *
     * @return true if the user supports context
     */
    boolean supportsContext();

    /**
     * Whether this user has permission for a certain node or not.
     * @param permission the permissions node
     * @return true if permitted.
     */
    boolean hasPermission(@NotNull String permission);

    /**
     * Sound effects.
     */
    enum StrSoundEffect {
        /**
         * Successful tab.
         */
        SUCCESSFUL_TAB,
        /**
         * Failed tab.
         */
        FAILED_TAB,
        /**
         * Successful command.
         */
        SUCCESSFUL_COMMAND,
        /**
         * Failed command.
         */
        FAILED_COMMAND,
        /**
         * Successfully picked an option.
         */
        SUCCESSFUL_PICKED,
        /**
         * Failed to pick an option.
         */
        FAILED_PICKED,
        /**
         * When a user has to pick an option.
         */
        PICK_OPTION
    }

}
