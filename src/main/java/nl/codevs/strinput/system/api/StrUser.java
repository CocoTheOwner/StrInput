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
package nl.codevs.strinput.system.api;

import nl.codevs.strinput.system.text.Str;

import java.util.Arrays;
import java.util.List;

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
    String getName();

    /**
     * Send a message to the user.
     * @param message the message to send
     */
    void sendMessage(Str message);

    /**
     * @return whether this user supports clickable {@link Str}s.
     */
    boolean supportsClickables();

    /**
     * Send multiple messages to the user.<br>
     * Overwrite {@link #sendMessage(List)} instead of this, as this points there.
     * @param messages the messages to send
     */
    default void sendMessage(Str[] messages) {
        sendMessage(Arrays.asList(messages));
    }

    /**
     * Send multiple messages to the user. Uses a loop of {@link #sendMessage}.
     * @param messages the messages to send
     */
    default void sendMessage(List<Str> messages) {
        for (Str message : messages) {
            sendMessage(message);
        }
    }

    /**
     * Play a sound effect
     * @param sfx the sound effect type
     */
    void playSound(StrSoundEffect sfx);

    /**
     * If this sender supports context, i.e. has values it stores for getting data automatically (instead of specifying it in commands).
     * See {@link StrCenter.ContextHandling}.
     * @return true if the user supports context
     */
    boolean supportsContext();

    /**
     * Whether this user has permission for a certain node or not.
     * @param permission the permissions node
     * @return true if permitted.
     */
    boolean hasPermission(String permission);

    /**
     * Sound effects.
     */
    enum StrSoundEffect {
        /**
         * Successful tab.
         * TODO: Implement tab
         */
        SUCCESSFUL_TAB,
        /**
         * Failed tab.
         * TODO: Implement tab
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
        PICK_OPTION;
    }

}
