package environment;

import nl.codevs.strinput.system.StrSoundEffect;
import nl.codevs.strinput.system.api.StrUser;
import nl.codevs.strinput.system.text.Str;
import nl.codevs.strinput.system.text.StrClickable;

import java.util.ArrayList;
import java.util.List;

public class TestUser implements StrUser {

    List<String> messages = new ArrayList<>();

    /**
     * Send a message to the sender.
     *
     * @param message the message to send
     */
    @Override
    public void sendMessage(Str message) {
        System.out.println(message.toString());
        messages.add(message.toString());
    }

    /**
     * Send multiple options when there is something to choose from.<br>
     * Note that it is required to have a Str.<br>
     * Return {@code null} if an option choice should be forced.
     *
     * @param clickables the clickable options to send
     */
    @Override
    public void sendOptions(List<StrClickable> clickables) {
        for (StrClickable clickable : clickables) {
            sendMessage(clickable);
        }
    }

    /**
     * @return whether this user supports {@link StrClickable}s.
     */
    @Override
    public boolean supportsClickables() {
        return false;
    }

    /**
     * Play a sound effect
     *
     * @param sfx the sound effect type
     */
    @Override
    public void playSound(StrSoundEffect sfx) {

    }
}
