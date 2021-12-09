package nl.codevs.strinput.examples.discord.extensions;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import nl.codevs.strinput.examples.discord.DiscordUser;
import nl.codevs.strinput.system.Env;
import nl.codevs.strinput.system.parameter.StrParameterHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class DiscordUserHandler implements StrParameterHandler<User> {
    /**
     * Get all possible values for this type.<br>
     * Do not specify lists of very high length (10^6)
     *
     * @return a list of possibilities
     */
    @Override
    public List<User> getPossibilities() {

        Guild guild = ((DiscordUser) Env.user()).guild();
        if (guild == null) {
            return null;
        }
        return guild.getMembers().stream().map(Member::getUser).collect(Collectors.toList());
    }

    /**
     * Whether this handler supports the type or not.
     *
     * @param type a type
     * @return true if it supports the type
     */
    @Override
    public boolean supports(@NotNull Class<?> type) {
        return type.equals(User.class);
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
     *                   (Exceptions don't have to be caught in the parser)
     */
    @Override
    public @NotNull User parse(@NotNull String text) throws Throwable {
        List<User> options = getPossibilities(text);

        if (options.isEmpty()) {
            throw new StrParseException(DiscordUserHandler.class, text, "No options found for input!");
        }

        if (options.size() > 1) {
            List<User> filteredOptions = options.stream().filter(o -> o.getName().equals(text)).toList();
            if (filteredOptions.size() == 1) {
                return filteredOptions.get(0);
            } else if (filteredOptions.size() > 1) {
                throw new StrWhichException(DiscordUserHandler.class, text, filteredOptions);
            } else {
                throw new StrWhichException(DiscordUserHandler.class, text, options);
            }
        }

        return options.get(0);
    }

    /**
     * Get a random default value.
     *
     * @return the random default
     */
    @Override
    public @NotNull String getRandomDefault() {
        return getPossibilities().get(
                new Random().nextInt(getPossibilities().size() - 1)
        ).getName();
    }

    /**
     * Get all possible values for this type,
     * filtered with some input string.<br>
     *
     * @param input the input string to filter by
     * @return a list of possibilities
     */
    @Override
    public @NotNull List<User> getPossibilities(@NotNull final String input) {
        return getPossibilities().stream().filter(p ->
                p.getName().contains(input.toLowerCase(Locale.ROOT))
                        || input.contains(String.valueOf(p.getIdLong()))
        ).collect(Collectors.toList());
    }
}
