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

package nl.codevs.strinput.system.exception;

import java.lang.reflect.Method;

/**
 * Exception for when a command or class is attempted to be made virtual, without an annotation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public class StrNoAnnotationException extends Exception {
    public StrNoAnnotationException(Method method, Class<?> clazz) {
        super("Cannot instantiate " + method.getName() + " in " + clazz.getSimpleName() + " because not annotated with @StrInput");
    }

    public StrNoAnnotationException(Class<?> clazz, Class<?> parent) {
        super("Cannot instantiate " + clazz.getSimpleName() + " in " + parent.getSimpleName() + " because not annotated with @StrInput");
    }

    public StrNoAnnotationException(Class<?> clazz) {
        super("Cannot instantiate " + clazz.getSimpleName() + " because not annotated with @StrInput.");
    }
}
