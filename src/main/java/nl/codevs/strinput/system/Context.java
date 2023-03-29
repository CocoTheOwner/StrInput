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

import nl.codevs.strinput.system.util.ContextContainer;

/**
 * Environment variables available on a per-thread basis.
 * <ul>
 *     <li>{@link #touch(StrUser)} store a user for the current thread</li>
 *     <li>{@link #touch(StrCenter)} store a center for the current thread</li>
 * </ul>
 * <ul>
 *     <li>{@link #user()} get the current {@link StrUser}</li>
 *     <li>{@link #center()} get the current {@link StrCenter}</li>
 *     <li>{@link #settings()} get the current {@link StrSettings}
 *     (based on {@link #center()})</li>
 * </ul>
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class Context {

    private Context() {
        // Never used
    }

    /**
     * Context container for {@link StrUser}s.
     */
    private static final ContextContainer<StrUser> USER_CONTEXT_CONTAINER = new ContextContainer<>();

    /**
     * Context container for {@link StrCenter}s.
     */
    private static final ContextContainer<StrCenter> CENTER_CONTEXT_CONTAINER = new ContextContainer<>();

    /**
     * Get the current {@link StrUser}.
     * @return the current {@link StrUser}
     */
    public static StrUser user() {
        return USER_CONTEXT_CONTAINER.get();
    }

    /**
     * Get the current {@link StrCenter}.
     * @return the current {@link StrCenter}
     */
    public static StrCenter center() {
        return CENTER_CONTEXT_CONTAINER.get();
    }

    /**
     * Get the current {@link StrSettings}.
     * @return the current {@link StrSettings}
     */
    public static StrSettings settings() {
        return center().getSettings();
    }

    /**
     * Add the {@link StrCenter} to the context map and removes dead threads.
     *
     * @param center the {@link StrCenter}
     */
    public static void touch(final StrCenter center) {
        CENTER_CONTEXT_CONTAINER.touch(center);
    }

    /**
     * Add the {@link StrUser} to the context map and removes dead threads.
     *
     * @param user the {@link StrUser}
     */
    public static void touch(final StrUser user) {
        USER_CONTEXT_CONTAINER.touch(user);
    }

    /**
     * Get whether the current thread is registered.
     * @return {@code true} if the current thread is registered, else {@code false}
     */
    public static boolean registered() {
        return center() != null;
    }
}
