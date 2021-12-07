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

import nl.codevs.strinput.examples.spigotmc.command.SpigotCommands;
import nl.codevs.strinput.system.text.Str;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import javax.management.InstanceAlreadyExistsException;

/**
 * A Spigot plugin example
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class SpigotPlugin extends JavaPlugin implements CommandExecutor, Listener {
    private static SpigotCenter commandSystem;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return commandSystem.onCommand(sender, command, label, args);
    }

    @Override
    public void onEnable() {
        try {
            commandSystem = new SpigotCenter(
                    this,
                    new SpigotConsole(),
                    true,
                    new SpigotCommands()
            );
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
            new SpigotConsole().sendMessage(new Str("The command system is already running!"));
        }
    }
}
