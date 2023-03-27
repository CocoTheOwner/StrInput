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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Command parameters' annotation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {

    /**
     * The default description.
     */
    String DEFAULT_DESCRIPTION = "No Description Provided";

    /**
     * The main name of this command.<br>
     * Required parameter.<br>
     * This is what is used in game,
     * alongside any (if specified) {@link #aliases() aliases}
     * @return the name of this parameter
     */
    String name();

    /**
     * The description of this parameter, used in help-popups in game.<br>
     * The default value is {@link #DEFAULT_DESCRIPTION}
     * @return the description of this parameter
     */
    String description() default DEFAULT_DESCRIPTION;

    /**
     * The default value for this argument.<br>
     * The entered string is parsed to the parameter type
     * similarly to how normal command input would be.<br>
     * Which indicates the variable MUST be defined
     * by the person running the command if this is undefined.<br>
     * If you define this,
     * the variable automatically becomes non-required,
     * but can still be set.
     * @return the default value (as a string) of this parameter
     */
    String defaultValue() default "";

    /**
     * The aliases of this parameter.
     * <p>
     * Provides additional values to just the {@link #name() name} if specified, or the method name by default<br>
     * Can be initialized as just a string
     * (ex. {@code "alias"}) or as an array (ex. {@code {"alias1", "alias2"}})<br>
     * If someone uses {@code /plugin foo bar=baz},
     * and you specify {@code alias="b"} here,
     * {@code /plugin foo b=baz} will do the exact same.
     * @return the aliases of this parameter
     */
    String[] aliases() default "";

    /**
     * Attempts to dynamically pull context from the user,
     * default data or uses one of the custom supported types.<br>
     * <em>Requires a context handler in
     * {@link nl.codevs.strinput.system.context}</em>
     * @return true if this parameter may be derived from context.
     */
    boolean contextual() default false;
}
