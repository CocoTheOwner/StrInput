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
package nl.codevs.strinput.examples.spigotmc.extensions;

import nl.codevs.strinput.examples.spigotmc.SpigotUser;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.context.StrContextHandler;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

/**
 * World context.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class SpigotWorldContext implements StrContextHandler<World> {
    /**
     * The type this context handler handles.
     *
     * @param type the type to check for support
     *
     * @return the type
     */
    @Override
    public boolean supports(@NotNull final Class<?> type) {
        return type.equals(World.class);
    }

    /**
     * The handler for this context.
     * Can use any data found in the user object for context derivation.
     *
     * @param user the user whose data may be used
     *
     * @return the value in the assigned type
     */
    @Override
    public @NotNull World handle(@NotNull final StrUser user) {
        return ((SpigotUser) user).getPlayer().getWorld();
    }
}
