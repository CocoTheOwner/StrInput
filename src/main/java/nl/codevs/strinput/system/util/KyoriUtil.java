package nl.codevs.strinput.system.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KyoriUtil {

    /**
     * Convert a component (and all its garbage) to a human-readable string
     * @param component the component to translate
     * @return the string representing the component
     */
    public static @NotNull String componentToString(@NotNull TextComponent component) {
        StringBuilder out = new StringBuilder();
        List<Component> queue = new ArrayList<>(List.of(component));
        while (!queue.isEmpty()) {
            Component head = queue.remove(0);
            if (head instanceof TextComponent tc) {
                out.append(tc.content());
                if (tc.hoverEvent() != null) {
                    if (Objects.requireNonNull(tc.hoverEvent()).value() instanceof TextComponent tcc) {
                        out.append(" (").append(tcc.content()).append(")");
                    } else {
                        throw new RuntimeException("Non-TextComponent Hover Event on: "
                                + Objects.requireNonNull(tc.hoverEvent()).value());
                    }
                }
                List<Component> newQueue = new ArrayList<>();
                newQueue.addAll(tc.children());
                newQueue.addAll(queue);
                queue = newQueue;
            } else {
                throw new RuntimeException("Non-TextComponent Content on: " + head);
            }
        }
        return out.toString();
    }
}
