package emanondev.itemtag.actions;

import emanondev.itemedit.APlugin;
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

    public abstract List<String> getInfo();//TODO move on configuration

    public String fixActionInfo(String actionInfo) {
        return actionInfo;
    }
}
