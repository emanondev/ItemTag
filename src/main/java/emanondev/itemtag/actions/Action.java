package emanondev.itemtag.actions;

import emanondev.itemedit.APlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public abstract class Action {

    @Getter
    private final String id;
    @Getter
    private final APlugin plugin;

    public Action(String id, APlugin plugin) {
        this.id = id.toLowerCase(Locale.ENGLISH);
        this.plugin = plugin;
    }

    /**
     * @throws IllegalArgumentException if text is not valid
     */
    public abstract void validateInfo(String text);

    /**
     * @throws IllegalArgumentException if text is not valid
     */
    public abstract void execute(Player player, String text);

    /**
     * @return allowed completitions
     */
    public abstract List<String> tabComplete(CommandSender sender, List<String> params);

    public Component getInfo(CommandSender sender) {
        List<Component> info = plugin.getTranslator().translateList(sender, "actions." + id + ".info", false);
        if (info == null || info.isEmpty()) {
            return Component.empty();
        }
        return Component.join(JoinConfiguration.separator(Component.newline()), info);
    }

    public String fixActionInfo(String actionInfo) {
        return actionInfo;
    }
}
