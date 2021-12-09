package nl.codevs.strinput.examples.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import nl.codevs.strinput.system.StrInput;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Discord bot implementation.
 * @author Sjoerd van de Goor
 * @since v0.2
 */
public class DiscordBot extends ListenerAdapter {

    /**
     * Bot instance.
     */
    public static DiscordBot BOT;

    /**
     * Main method.
     * @param args runtime arguments
     */
    public static void main(final String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("token.txt"));
            assert DiscordCommands.class.isAnnotationPresent(StrInput.class);
            BOT = new DiscordBot(reader.readLine(), "!", DiscordCommands.class.getDeclaredAnnotation(StrInput.class).name());
        } catch (LoginException | InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Command center.
     */
    private final DiscordCenter center;

    /**
     * The bot token.
     */
    private final String token;

    /**
     * Command prefix.
     */
    private final String prefix;

    /**
     * JDA.
     */
    private final JDA jda;

    /**
     * Create a new Discord bot.
     * @param token the bot token
     * @param prefix command prefix
     * @param activityCommand command to display in activity
     *
     * @throws LoginException if login fails
     * @throws InterruptedException if waiting for JDA setup fails
     */
    public DiscordBot(String token, String prefix, String activityCommand) throws LoginException, InterruptedException {
        this.token = token;
        this.jda = setup(token, prefix, activityCommand);
        this.center = new DiscordCenter(jda);
        this.prefix = prefix;
    }

    /**
     * Main method.
     *
     * @param token bot token
     * @param prefix bot command prefix
     * @param activityCommand command to display in activity
     *
     * @throws LoginException if the bot token isn't working
     * @throws InterruptedException if the setup fails
     *
     * @return the set-up JDA
     */
    public JDA setup(String token, String prefix, String activityCommand) throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(token);

        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.listening(prefix + activityCommand));
        // Add listener
        builder.addEventListeners(this);
        // Set intents
        builder.setEnabledIntents(
                GatewayIntent.DIRECT_MESSAGES,
                GatewayIntent.DIRECT_MESSAGE_TYPING,
                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_MESSAGE_TYPING,
                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                GatewayIntent.GUILD_VOICE_STATES,
                GatewayIntent.GUILD_EMOJIS
        );

        return builder.build().awaitReady();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        System.out.println("Setup!");
    }

    /**
     * When a message is received.
     * @param event the received event
     */
    @Override
    public void onMessageReceived(@NotNull final MessageReceivedEvent event) {
        if (!event.getMessage().getContentRaw().startsWith(prefix)) {
            return;
        }
        if (event.getAuthor().isBot()) {
            return;
        }
        center.onCommand(event, prefix);
    }
}
