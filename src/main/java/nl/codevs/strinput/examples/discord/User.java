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

package nl.codevs.strinput.examples.discord;

import net.dv8tion.jda.api.entities.Member;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.StrUser;
import nl.codevs.strinput.system.text.StrClickable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

/**
 * Discord Bot integration example.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class User extends StrUser {

    Member member;

    public User(Member member){
        this.member = member;
    }


    /**
     * Send a message to the sender.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(Str message) {

    }

    /**
     * Send multiple options when there is something to choose from.<br>
     * Note that it is required to have an Str.
     *
     * @param clickables the clickable options to send
     */
    @Override
    public void sendOptions(List<StrClickable> clickables) {

    }

    /**
     * Whether this user is a console (all-rights, no properties)
     *
     * @return true if the user is a console
     */
    @Override
    public boolean isConsole() {
        return false;
    }

    /**
     * Add additional fields to your users that should be stored
     *
     * @param field field name
     *
     * @return an object
     */
    @Override
    public @Nullable Object accessField(@NotNull String field) {
        if (field.equalsIgnoreCase("member")) {
            return member;
        };
        return null;
    }
}
