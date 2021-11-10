package emanondev.itemtag.actions;

import java.util.*;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ActionHandler {
	
	public static void handleAction(Player player,String type, String action) {
		actions.get(type).execute(player, action);
	}
	
	private static final HashMap<String,Action> actions = new HashMap<>();
	
	public static void registerAction(Action action) {
		if (action==null)
			throw new NullPointerException();
		if (actions.containsKey(action.getID()))
			throw new IllegalArgumentException();
		actions.put(action.getID().toLowerCase(), action);
	}
	
	public static void clearActions() {
		actions.clear();
	}
	
	public static void validateActionType(String type) {
		if (!actions.containsKey(type.toLowerCase()))
			throw new IllegalArgumentException();
	}
	public static void validateActionInfo(String type,String text) {
		actions.get(type.toLowerCase()).validateInfo(text);
	}

	public static List<String> tabComplete(CommandSender sender,String type,List<String> params){
		if (actions.containsKey(type.toLowerCase()))
			return actions.get(type.toLowerCase()).tabComplete(sender, params);
		return new ArrayList<>();
	}
	
	public static List<String> getTypes(){
		return new ArrayList<>(actions.keySet());
	}

	public static Action getAction(String type) {
		return actions.get(type.toLowerCase());
	}

}
