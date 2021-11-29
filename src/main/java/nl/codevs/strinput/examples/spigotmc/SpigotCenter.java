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

import nl.codevs.strinput.examples.spigotmc.extensions.SpigotPlayerContext;
import nl.codevs.strinput.examples.spigotmc.extensions.SpigotPlayerHandler;
import nl.codevs.strinput.examples.spigotmc.extensions.SpigotWorldContext;
import nl.codevs.strinput.examples.spigotmc.extensions.SpigotWorldHandler;
import nl.codevs.strinput.system.StrCenter;
import nl.codevs.strinput.system.StrCategory;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.contexts.StrContextHandler;
import nl.codevs.strinput.system.parameters.StrParameterHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Spigot command center.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class SpigotCenter extends StrCenter {
    /**
     * Create a new spigot command center.
     *
     * @param consoleUser  the console ({@link StrUser})
     * @param rootCommands array of root commands (usually only 1, your main command)
     */
    public SpigotCenter(StrUser consoleUser, StrCategory... rootCommands) {
        super(
                consoleUser,
                new StrParameterHandler<?>[]{
                        new SpigotPlayerHandler(),
                        new SpigotWorldHandler()
                },
                new StrContextHandler<?>[]{
                        new SpigotPlayerContext(),
                        new SpigotWorldContext()
                },
                rootCommands
        );
    }

    /**
     * Run a spigot command with StrInput.
     * @param sender the command sender
     * @param command the command
     * @param label the label
     * @param args the command arguments
     * @return true if successful
     */
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> cmd = new ArrayList<>();
        cmd.add(command.getName());
        cmd.addAll(List.of(args));
        SpigotUser user = new SpigotUser(sender.getServer().getPlayer(sender.getName()));
        return onCommand(cmd, user);
    }
}
