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

import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Environment variables available on a per-thread basis.
 * <ul>
 *     <li>{@link #touch(StrUser)} store a user for the current thread</li>
 *     <li>{@link #touch(StrCenter)} store a center for the current thread</li>
 * </ul>
 * <ul>
 *     <li>{@link #user()} get the current user</li>
 *     <li>{@link #center()} get the current center</li>
 *     <li>{@link #settings()} get the current settings
 *     (based on {@link #center()}</li>
 * </ul>
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class Env {

    private Env() {
        // Never used
    }

    /**
     * Get the current user.
     * @return the current user
     */
    public static StrUser user() {
        return UserContext.get();
    }

    /**
     * Get the current center.
     * @return the current center
     */
    public static StrCenter center() {
        return CenterContext.get();
    }

    /**
     * Get the current settings.
     * @return the current settings
     */
    public static StrSettings settings() {
        return center().getSettings();
    }

    /**
     * Add the {@link StrCenter} to the context map and removes dead threads.
     *
     * @param center the center
     */
    public static void touch(final StrCenter center) {
        CenterContext.touch(center);
    }

    /**
     * Add the {@link StrUser} to the context map and removes dead threads.
     *
     * @param user the user
     */
    public static void touch(final StrUser user) {
        UserContext.touch(user);
    }

    /**
     * Get whether this thread is registered.
     * @return true if this thread is registered
     */
    public static boolean registered() {
        return center() != null;
    }

    /**
     * Context handling (user handling).
     * <p>
     * This system REQUIRES:
     * <ul>
     *     <li>each command to be be handled in a new thread</li>
     *     <li>a call to {@link #touch(StrUser)} before context is accessed</li>
     * </ul>
     *
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class UserContext {

        /**
         * Map containing environment users.
         */
        private static final ConcurrentHashMap<Thread, StrUser> MAP = new ConcurrentHashMap<>();

        /**
         * Get the current user from the current thread's context.
         *
         * @return the {@link StrUser} for this thread
         */
        public static StrUser get() {
            return MAP.get(Thread.currentThread());
        }

        /**
         * Add the {@link StrUser} to the context map and removes dead threads.
         *
         * @param user the user
         */
        public static void touch(final StrUser user) {
            synchronized (MAP) {
                MAP.put(Thread.currentThread(), user);

                Enumeration<Thread> contextKeys = MAP.keys();

                while (contextKeys.hasMoreElements()) {
                    Thread thread = contextKeys.nextElement();
                    if (!thread.isAlive()) {
                        MAP.remove(thread);
                    }
                }
            }
        }
    }

    /**
     * Context handling for command centers (command center handling).
     * <p>
     * This system REQUIRES:
     * <ul>
     *     <li>each command to be be handled in a new thread</li>
     *     <li>a call to {@link #touch(StrCenter)} before context is accessed</li>
     * </ul>
     *
     * @author Sjoerd van de Goor
     * @since v0.1
     */
    public static class CenterContext {

        /**
         * Map containing environment {@link StrCenter}.
         */
        private static final ConcurrentHashMap<Thread, StrCenter> MAP = new ConcurrentHashMap<>();

        /**
         * Get the current center from the current thread's context.
         *
         * @return the {@link StrCenter} for this thread
         */
        public static StrCenter get() {
            return MAP.get(Thread.currentThread());
        }

        /**
         * Add the {@link StrCenter}
         * to the context map and removes dead threads.
         *
         * @param center the center
         */
        public static void touch(final StrCenter center) {
            synchronized (MAP) {

                Enumeration<Thread> contextKeys = MAP.keys();

                while (contextKeys.hasMoreElements()) {
                    Thread thread = contextKeys.nextElement();
                    if (!thread.isAlive()) {
                        MAP.remove(thread);
                    }
                }

                MAP.put(Thread.currentThread(), center);
            }
        }
    }
}
