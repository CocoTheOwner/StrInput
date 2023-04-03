package nl.codevs.strinput.system.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.junit.jupiter.api.Test;

import javax.naming.Name;

import static org.junit.jupiter.api.Assertions.*;

class KyoriUtilTest {

    @Test
    void componentToString() {
        assertEquals("yes", KyoriUtil.componentToString(Component.text("yes")));
        assertEquals("yes", KyoriUtil.componentToString(Component.text("yes").color(NamedTextColor.BLUE)));
        assertEquals("yes (apple)", KyoriUtil.componentToString(Component.text("yes").hoverEvent(Component.text("apple"))));
        assertEquals("yes please (sir)", KyoriUtil.componentToString(Component.text("yes").append(Component.text(" please").hoverEvent(Component.text("sir")))));
        assertEquals("yes please (sir) monkey (two)", KyoriUtil.componentToString(
                Component.text("yes")
                        .append(Component.text(" please").hoverEvent(Component.text("sir")))
                        .append(Component.text(" monkey").hoverEvent(Component.text("two")))
        ));
        TextComponent test = Component.text("Node help of ").color(NamedTextColor.GREEN)
                .append(Component.text("command").color(NamedTextColor.BLUE))
                .append(Component.text(" for ").color(NamedTextColor.GREEN))
                .append(Component.text("user").color(NamedTextColor.BLUE));
        assertEquals("Node help of command for user", KyoriUtil.componentToString(test));
        assertEquals("YepNode help of command for userNode help of command for user", KyoriUtil.componentToString(
                Component.text("Yep")
                        .append(test)
                        .append(test)
        ));
    }
}