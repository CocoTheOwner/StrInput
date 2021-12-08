package nl.codevs.strinput.examples.spigotmc.extensions;

import nl.codevs.strinput.examples.spigotmc.SpigotUser;
import nl.codevs.strinput.system.api.Env;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SpigotWorldHandler implements StrParameterHandler<World> {
    /**
     * Get all possible values for this type.<br>
     * Do not specify lists of very high length (10^6)
     *
     * @return a list of possibilities
     */
    @Override
    public @NotNull List<World> getPossibilities() {
        return Bukkit.getWorlds();
    }

    /**
     * Whether this handler supports the type or not.
     *
     * @param type a type
     * @return true if it supports the type
     */
    @Override
    public boolean supports(@NotNull final Class<?> type) {
        return type.equals(World.class);
    }

    /**
     * Parse a string to this type.<br>
     * You can throw:
     * <ul>
     *     <li>{@link StrWhichException}
     *     to indicate multiple options (ambiguity)</li>
     *     <li>{@link StrParseException}
     *     to indicate parsing problems</li>
     * </ul>
     *
     * @param text the string to parse
     * @return an instance of this type parsed from the string
     * @throws Throwable when something else fails.
     * (Exceptions don't have to be caught in the parser)
     */
    @Override
    public @NotNull World parse(@NotNull final String text) throws Throwable {
        if (!((SpigotUser) Env.UserContext.get()).isPlayer()) {
            throw new StrParseException(
                    World.class,
                    text,
                    "User is not a player"
            );
        }
        List<World> options = getPossibilities(text);
        if (options.size() == 0) {
            throw new StrParseException(
                    World.class,
                    text,
                    "No options found for input"
            );
        } else if (options.size() == 1) {
            return options.get(0);
        } else {
            throw new StrWhichException(
                    World.class,
                    text,
                    options
            );
        }
    }

    /**
     * Get a random default value.
     *
     * @return the random default
     */
    @Override
    public @NotNull String getRandomDefault() {
        return getPossibilities().get(
                RANDOM.nextInt(getPossibilities().size())
        ).getName();
    }

    /**
     * Get all possible values for this type,
     * filtered with some input string.
     *
     * @param input the input string to filter by
     * @return a list of possibilities
     */
    @Override
    public @NotNull List<World> getPossibilities(@NotNull final String input) {
        final String i = input.toLowerCase(Locale.ROOT);
        return getPossibilities().stream().filter(
                w -> w.getName().toLowerCase(Locale.ROOT).startsWith(i)
        ).collect(Collectors.toList());
    }
}
