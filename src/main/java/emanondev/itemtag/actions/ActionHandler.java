package emanondev.itemtag.actions;

import emanondev.itemedit.utility.VersionUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ActionHandler {

    private static final Map<String, Action> actions = VersionUtils.hasFoliaAPI() ?
            new ConcurrentHashMap<>() : new HashMap<>();

    public static void handleAction(Player player, String type, String action) {
        actions.get(type).execute(player, action);
    }

    public static void registerAction(Action action) {
        if (action == null) {
            throw new NullPointerException();
        }
        if (actions.containsKey(action.getID())) {
            throw new IllegalArgumentException();
        }
        actions.put(action.getID().toLowerCase(Locale.ENGLISH), action);
    }

    public static void clearActions() {
        actions.clear();
    }

    public static void validateActionType(String type) {
        if (!actions.containsKey(type.toLowerCase(Locale.ENGLISH))) {
            throw new IllegalArgumentException();
        }
    }

    public static void validateActionInfo(String type, String text) {
        actions.get(type.toLowerCase(Locale.ENGLISH)).validateInfo(text);
    }

    public static List<String> tabComplete(CommandSender sender, String type, List<String> params) {
        if (actions.containsKey(type.toLowerCase(Locale.ENGLISH))) {
            return actions.get(type.toLowerCase(Locale.ENGLISH)).tabComplete(sender, params);
        }
        return new ArrayList<>();
    }

    public static List<String> getTypes() {
        return new ArrayList<>(actions.keySet());
    }

    public static Action getAction(String type) {
        return actions.get(type.toLowerCase(Locale.ENGLISH));
    }

    public static String fixActionInfo(String type, String actionInfo) {
        return actions.get(type.toLowerCase(Locale.ENGLISH)).fixActionInfo(actionInfo);
    }
}
