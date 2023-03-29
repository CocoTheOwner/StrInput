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
package environment;

import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * StrUser test implementation.
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class TestUser implements StrUser {

    public static final TestUser SUT = new TestUser();

    public final List<String> messages = new ArrayList<>();

    /**
     * The name of the user (something to identify them by).
     *
     * @return the name of the user
     */
    @Override
    public @NotNull String getName() {
        return "testUser";
    }

    /**
     * Send a message to the user.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(@NotNull String message) {
        message = C.removeColor(message);
        System.out.println(C.removeColor(message));
        messages.add(message);
    }

    /**
     * @return whether this user supports clickable {@link Str}s.
     */
    @Override
    public boolean supportsClickable() {
        return false;
    }

    /**
     * Play a sound effect
     *
     * @param sfx the sound effect type
     */
    @Override
    public void playSound(@NotNull StrSoundEffect sfx) {

    }

    /**
     * If this sender supports context, i.e. has values it stores for getting data automatically (instead of specifying it in commands).
     * See {@link StrCenter.ContextHandling}.
     *
     * @return true if the user supports context
     */
    @Override
    public boolean supportsContext() {
        return true;
    }

    /**
     * Whether this user has permission for a certain node or not.
     *
     * @param permission the permissions node
     * @return true if permitted.
     */
    @Override
    public boolean hasPermission(@NotNull String permission) {
        return true;
    }

}
