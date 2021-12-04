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

package nl.codevs.strinput.system.virtual;

import nl.codevs.strinput.system.api.Param;
import nl.codevs.strinput.system.api.StrCenter;
import nl.codevs.strinput.system.api.StrInput;
import nl.codevs.strinput.system.api.StrUser;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A {@link StrInput} annotated method's virtual representation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class StrVirtualCommand implements StrVirtual {

    /**
     * Newline.
     */
    private static final Str newLine = new Str(C.RESET).a("\n");

    /**
     * Command parameters.
     */
    private final List<StrVirtualParameter> parameters;
    /**
     * The underlying method.
     */
    private final Method method;
    /**
     * Parent category.
     */
    private final StrVirtualCategory parent;
    /**
     * Command annotation.
     */
    private final StrInput annotation;
    /**
     * Command center.
     */
    private final StrCenter center;

    /**
     * Create a new virtual command.<br>
     * Assumes {@code command} is annotated by @{@link StrInput}.
     * @param parent the virtual category this command is in
     * @param command the underlying method
     * @param center the command center calling this command
     */
    public StrVirtualCommand(StrVirtualCategory parent, Method command, StrCenter center) {
        this.parent = parent;
        this.method = command;
        this.center = center;
        this.annotation = method.getAnnotation(StrInput.class);
        this.parameters = setupParameters();
    }

    /**
     * Calculate the parameters in this method.<br>
     * Sorted by required & contextuality.
     * @return {@link List} of {@link StrVirtualParameter}s
     */
    private List<StrVirtualParameter> setupParameters() {
        List<StrVirtualParameter> parameters = new ArrayList<>();
        Arrays.stream(method.getParameters()).filter(p -> p.isAnnotationPresent(Param.class)).forEach(p -> parameters.add(new StrVirtualParameter(p, center)));
        return parameters;
    }

    /**
     * Get the parent virtual.
     *
     * @return the parent virtual
     */
    @Override
    public @Nullable StrVirtual getParent() {
        return parent;
    }

    /**
     * Get the default virtual name (when the annotation was not given a specific name)
     *
     * @return the name
     */
    @Override
    public @NotNull String getDefaultName() {
        return method.getName();
    }

    /**
     * Get the annotation on the class/method.
     *
     * @return the annotation
     */
    @Override
    public @NotNull StrInput getAnnotation() {
        return annotation;
    }

    /**
     * Run the virtual.
     *
     * @param arguments the remaining arguments
     * @param user      the user that sent the command
     * @param center    the command system
     * @return true if this virtual ran successfully
     */
    @Override
    public boolean run(List<String> arguments, StrUser user, StrCenter center) {
        center.debug(new Str(C.Y).a("Reached virtual command " + getName()));
        if (arguments.size() == parameters.size()) {
            center.debug(new Str(C.G).a("Running: " + getName()));
            return true;
        }
        return false;
    }

    /**
     * Send help for this virtual to a user.
     *
     * @param user the user to send help to
     */
    @Override
    public void help(StrUser user) {
        user.sendMessage(new Str(C.G).a(getName() + " " + parameters.size()));
    }
}
