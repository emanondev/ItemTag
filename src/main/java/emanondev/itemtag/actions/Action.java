package emanondev.itemtag.actions;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;

public abstract class Action {

    private final String id;

    public Action(String id) {
        this.id = id.toLowerCase(Locale.ENGLISH);
    }

    public String getID() {
        return id;
    }

    /**
     * @param text
     * @throws IllegalArgumentException if text is not valid
     */
    public abstract void validateInfo(String text);

    /**
     * @param text
     * @throws IllegalArgumentException if text is not valid
     */
    public abstract void execute(Player player, String text);

    /**
     * @param sender
     * @param params
     * @return allowed completitions
     */
    public abstract List<String> tabComplete(CommandSender sender, List<String> params);

    public abstract List<String> getInfo();
}
