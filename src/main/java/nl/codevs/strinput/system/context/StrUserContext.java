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

package nl.codevs.strinput.system.context;

import nl.codevs.strinput.system.StrUser;

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Context handling (user sender handling).
 *
 * This system REQUIRES:
 * <ul>
 *     <li>each command must be handled in a new thread</li>
 *     <li>a call to {@link #touch(StrUser)} asap after a command call</li>
 * </ul>
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public abstract class StrUserContext {

    private static final ConcurrentHashMap<Thread, StrUser> context = new ConcurrentHashMap<>();

    /**
     * Get the current user from the current thread's context
     *
     * @return the {@link StrUser} for this thread
     */
    public static StrUser get() {
        return context.get(Thread.currentThread());
    }

    /**
     * Add the {@link StrUser} to the context map & removes dead threads
     *
     * @param user the user
     */
    public static void touch(StrUser user) {
        synchronized (context) {
            context.put(Thread.currentThread(), user);

            Enumeration<Thread> contextKeys = context.keys();

            while (contextKeys.hasMoreElements()) {
                Thread thread = contextKeys.nextElement();
                if (!thread.isAlive()) {
                    context.remove(thread);
                }
            }
        }
    }
}
