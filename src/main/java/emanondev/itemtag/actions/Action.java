package emanondev.itemtag.actions;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Action {
	
	private final String id;

	public Action(String id) {
		this.id = id.toLowerCase();
		
	}

	public String getID() {
		return id;
	}
	/**
	 * @throws IllegalArgumentException if text is not valid
	 * @param text
	 */
	public abstract void validateInfo(String text);
	/**
	 * @throws IllegalArgumentException if text is not valid
	 * @param text
	 */
	public abstract void execute(Player player,String text);

	/**
	 * 
	 * @param sender
	 * @param params
	 * @return allowed completitions
	 */
	public abstract List<String> tabComplete(CommandSender sender,List<String> params);

	public abstract List<String> getInfo();
}
