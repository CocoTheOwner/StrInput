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

import lombok.Getter;
import nl.codevs.strinput.system.api.StrCenter;
import nl.codevs.strinput.system.api.StrCategory;
import nl.codevs.strinput.system.api.StrInput;
import nl.codevs.strinput.system.api.StrUser;
import nl.codevs.strinput.system.text.C;
import nl.codevs.strinput.system.text.Str;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A {@link StrInput} annotated method's virtual representation.
 *
 * @author Sjoerd van de Goor
 * @since v0.1
 */
public final class StrVirtualCategory implements StrVirtual {

    /**
     * Newline.
     */
    private static final Str newLine = new Str(C.RESET).a("\n");

    /**
     * Parent category.
     */
    private final StrVirtualCategory parent;
    /**
     * Commands in this category.
     */
    private final List<StrVirtualCommand> commands;
    /**
     * Subcategories.
     */
    private final List<StrVirtualCategory> subCats;
    /**
     * Annotation on this category.
     */
    private final StrInput annotation;
    /**
     * Instance of this category.
     */
    private final StrCategory instance;
    /**
     * Command center.
     */
    private final StrCenter center;
    /**
     * Command mapping for input to command.
     */
    private final ConcurrentHashMap<String, StrVirtual> commandMap = new ConcurrentHashMap<>();

    /**
     * Create a new virtual category.<br>
     * Assumes the {@code instance} is annotated by @{@link StrInput}
     * @param parent the parent category (null if root)
     * @param instance an instance of the underlying class
     * @param center command center running this system
     */
    public StrVirtualCategory(
            StrVirtualCategory parent,
            StrCategory instance,
            StrCenter center
    ) {
        this.parent = parent;
        this.annotation = instance.getClass().getAnnotation(StrInput.class);
        this.instance = instance;
        this.center = center;
        this.commands = setupCommands();
        this.subCats = setupSubCats();
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
        return instance.getClass().getSimpleName();
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
        List<StrVirtual> options = new ArrayList<>();
        options.addAll(subCats);
        options.addAll(commands);

        String next = arguments.remove(0);

        center.debug(new Str(C.G).a("Virtual " + getName() + " attempting to find a match in " + options.size() + " options with input: " + next));
        for (StrVirtual option : options) {
            if (option.doesMatch(next, user)) {
                if (option.run(new ArrayList<>(arguments), user, center)) {
                    return true;
                } else {
                    center.debug(new Str(C.R).a("Virtual " + option.getName() + " matched with " + next + " but failed to run!"));
                }
            } else {
                center.debug(new Str(C.R).a("Virtual " + option.getName() + " did not match with " + next));
            }
        }
        center.debug(new Str(C.R).a("Virtual " + getName() + " failed to find a matching option for " + next + " and returns false"));
        return false;
    }

    /**
     * Calculate {@link StrVirtualCommand}s in this category.
     * @return the list of setup virtual commands
     */
    private List<StrVirtualCommand> setupCommands() {
        List<StrVirtualCommand> commands = new ArrayList<>();

        for (Method command : instance.getClass().getDeclaredMethods()) {
            if (Modifier.isStatic(command.getModifiers()) || Modifier.isFinal(command.getModifiers()) || Modifier.isPrivate(command.getModifiers())) {
                continue;
            }

            if (!command.isAnnotationPresent(StrInput.class)) {
                continue;
            }

            commands.add(new StrVirtualCommand(this, command, center));
        }

        return commands;
    }

    /**
     * Calculate all {@link StrVirtualCategory}s in this category.
     * @return the list of setup virtual categories
     */
    private List<StrVirtualCategory> setupSubCats() {
        List<StrVirtualCategory> subCats = new ArrayList<>();

        for (Field subCat : instance.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(subCat.getModifiers())
                    || Modifier.isFinal(subCat.getModifiers())
                    || Modifier.isTransient(subCat.getModifiers())
                    || Modifier.isVolatile(subCat.getModifiers())
            ) {
                continue;
            }

            if (!subCat.getType().isAnnotationPresent(StrInput.class)) {
                continue;
            }

            subCat.setAccessible(true);
            Object childRoot;
            try {
                childRoot = subCat.get(instance);
            } catch (IllegalAccessException e) {
                center.debug("Could not get child \"" + subCat.getName() + "\" from instance: \"" + instance.getClass().getSimpleName() + "\"");
                center.debug("Because of: " + e.getMessage());
                continue;
            }
            if (childRoot == null) {
                try {
                    childRoot = subCat.getType().getConstructor().newInstance();
                    subCat.set(instance, childRoot);
                } catch (NoSuchMethodException e) {
                    center.debug("Method \"" + subCat.getName() + "\" does not exist in instance: \"" + instance.getClass().getSimpleName() + "\"");
                    center.debug("Because of: " + e.getMessage());
                } catch (IllegalAccessException e) {
                    center.debug("Could get, but not access child \"" + subCat.getName() + "\" from instance: \"" + instance.getClass().getSimpleName() + "\"");
                    center.debug("Because of: " + e.getMessage());
                } catch (InstantiationException e) {
                    center.debug("Could not instantiate \"" + subCat.getName() + "\" from instance: \"" + instance.getClass().getSimpleName() + "\"");
                    center.debug("Because of: " + e.getMessage());
                } catch (InvocationTargetException e) {
                    center.debug("Invocation exception on \"" + subCat.getName() + "\" from instance: \"" + instance.getClass().getSimpleName() + "\"");
                    center.debug("Because of: " + e.getMessage());
                    center.debug("Underlying exception: " + e.getTargetException().getMessage());
                }
            }

            if (childRoot == null) {
                continue;
            }

            subCats.add(new StrVirtualCategory(
                    this,
                    (StrCategory) childRoot,
                    center));
        }

        return subCats;
    }
}
