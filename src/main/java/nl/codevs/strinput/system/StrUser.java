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

package nl.codevs.strinput.system;

import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.text.StrClickable;

import java.util.Arrays;
import java.util.List;

/**
 * Instance of a user (the sender/receiver, end-user client).
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public abstract class StrUser {

    /**
     * Send a message to the sender.
     * @param message the message to send
     */
    public abstract void sendMessage(Str message);

    /**
     * Send multiple options when there is something to choose from.<br>
     * Note that it is required to have an Str.
     * @param clickables the clickable options to send
     */
    public abstract void sendOptions(List<StrClickable> clickables);

    /**
     * Send multiple messages to the sender.<br>
     * Overwrite {@link #sendMessage(List)} instead of this, as this points there.
     * @param messages the messages to send
     */
    public void sendMessage(Str[] messages) {
        sendMessage(Arrays.asList(messages));
    }

    /**
     * Send multiple messages to the sender. Uses a loop of {@link #sendMessage}.
     * @param messages the messages to send
     */
    public void sendMessage(List<Str> messages) {
        for (Str message : messages) {
            sendMessage(message);
        }
    }
}
