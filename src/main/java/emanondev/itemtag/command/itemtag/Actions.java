package emanondev.itemtag.command.itemtag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsInventory;
import emanondev.itemedit.UtilsInventory.ExcessManage;
import emanondev.itemedit.UtilsString;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagManager;
import emanondev.itemtag.actions.ActionHandler;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;
import net.md_5.bungee.api.chat.BaseComponent;

public class Actions extends ListenerSubCmd {

	private final static String ACTIONS_KEY = ItemTag.get().getName().toLowerCase()+":actions";
	private final static String ACTION_USES_KEY = ItemTag.get().getName().toLowerCase()+":uses";
	private final static String ACTION_COOLDOWN_KEY = ItemTag.get().getName().toLowerCase()+":cooldown";
	private final static String ACTION_COOLDOWN_ID_KEY = ItemTag.get().getName().toLowerCase()+":cooldown_id";
	private final static String ACTION_PERMISSION_KEY = ItemTag.get().getName().toLowerCase()+":permission";
	private final static String LINK = "%%;%%";
	private final static String TYPE_SEPARATOR = "%%:%%";

	private BaseComponent[] helpSet;
	private BaseComponent[] helpAdd;
	private BaseComponent[] helpAddLine;
	private BaseComponent[] helpPermission;
	private BaseComponent[] helpCooldown;
	private BaseComponent[] helpCooldownId;
	private BaseComponent[] helpUses;
	private BaseComponent[] helpRemove;

	private static final String[] actionsSub = new String[] { "add", "addline", "set", "permission", "cooldown",
			"cooldownid", "uses", "remove", "info" };

	public Actions(ItemTagCommand cmd) {
		super("actions", cmd, true, true);
		load();
	}

	private void load() {
		this.helpSet = this.craftFailFeedback(getConfString("set.params"),
				String.join("\n", getConfStringList("set.description")));
		this.helpRemove = this.craftFailFeedback(getConfString("remove.params"),
				String.join("\n", getConfStringList("remove.description")));
		this.helpAdd = this.craftFailFeedback(getConfString("add.params"),
				String.join("\n", getConfStringList("add.description")));
		this.helpAddLine = this.craftFailFeedback(getConfString("addline.params"),
				String.join("\n", getConfStringList("addline.description")));
		this.helpPermission = this.craftFailFeedback(getConfString("permission.params"),
				String.join("\n", getConfStringList("permission.description")));
		this.helpCooldown = this.craftFailFeedback(getConfString("cooldown.params"),
				String.join("\n", getConfStringList("cooldown.description")));
		this.helpCooldownId = this.craftFailFeedback(getConfString("cooldownid.params"),
				String.join("\n", getConfStringList("cooldownid.description")));
		this.helpUses = this.craftFailFeedback(getConfString("uses.params"),
				String.join("\n", getConfStringList("uses.description")));
	}

	public void reload() {
		load();
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		if (args.length == 1) {
			onFail(p);
			return;
		}
		ItemStack item = this.getItemInHand(p);
		try {
			switch (args[1].toLowerCase()) {
			case "add":
				add((Player) sender, args, item);
				return;
			case "addline":
				addLine((Player) sender, args, item);
				return;
			case "remove":
				remove((Player) sender, args, item);
				return;
			case "set":
				set((Player) sender, args, item);
				return;
			case "uses":
				uses((Player) sender, args, item);
				return;
			case "cooldown":
				cooldown((Player) sender, args, item);
				return;
			case "cooldownid":
				cooldownId((Player) sender, args, item);
				return;
			case "permission":
				permission((Player) sender, args, item);
				return;
			case "info":
				info((Player) sender, args, item);
				return;
			}
			onFail(p);
		} catch (Exception e) {
			e.printStackTrace();
			onFail(p);
		}
	}

	private void info(Player sender, String[] args, ItemStack item) {
		TagManager tManager = ItemTag.get().getTagManager();
		if (!tManager.hasStringTag(ACTIONS_KEY, item)) {
			Util.sendMessage(sender, ChatColor.translateAlternateColorCodes('&', "&cThis item has no actions binded"));
			return;
		}
		
		ArrayList<String> list = new ArrayList<>();
		list.add("&b&lItemTag Actions Info");
		String permission = tManager.hasStringTag(ACTION_PERMISSION_KEY, item)
				? tManager.getString(ACTION_PERMISSION_KEY, item)
				: null;
		if (permission!=null)
			list.add("&bTo use this item &e"+permission+"&b permission is required");
		else
			list.add("&bTo use this item no permission is required");
		long cooldown = (long) (tManager.hasIntegerTag(ACTION_COOLDOWN_KEY, item)
				? tManager.getInteger(ACTION_COOLDOWN_KEY, item)
				: 0);
		String cooldownId = tManager.hasStringTag(ACTION_COOLDOWN_ID_KEY, item)
					? tManager.getString(ACTION_COOLDOWN_ID_KEY, item)
					: "default";
		if (cooldown>0)
			list.add("&bUsing this item multiple times apply a cooldown of &e"+(cooldown/1000)
					+" &bseconds, cooldown ID is &e"+cooldownId);

		list.add("&bExecuted actions are:");
		String[] actions = tManager.getString(ACTIONS_KEY, item).split(LINK);
		for (String action:actions)
			list.add("&b- &6"+action.split(TYPE_SEPARATOR)[0]+" &e"+
						action.split(TYPE_SEPARATOR)[1]);
		
		
		int uses = tManager.hasIntegerTag(ACTION_USES_KEY, item)
				? tManager.getInteger(ACTION_USES_KEY, item)
				: 1;
		list.add("&bThis item has &e"+uses+" &buses left");
		Util.sendMessage(sender, ChatColor.translateAlternateColorCodes('&',String.join("\n", list)));
	}

	// itemtag actions setpermission <permission>
	private void permission(Player p, String[] args, ItemStack item) {
		try {
			if (args.length > 3)
				throw new IllegalArgumentException("Wrong param number");
			String permission = args.length == 2 ? null : args[2].toLowerCase();
			ItemMeta meta;
			if (permission != null) {
				meta = ItemTag.get().getTagManager().setTag(ACTION_PERMISSION_KEY, item, permission).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.permission.set"), p, true,
						"%permission%", permission));
			} else {
				meta = ItemTag.get().getTagManager().removeTag(ACTION_PERMISSION_KEY, item).getItemMeta();
				Util.sendMessage(p,
						UtilsString.fix(this.getConfString("feedback.actions.permission.removed"), p, true));
			}
			item.setItemMeta(meta);
		} catch (Exception e) {
			p.spigot().sendMessage(helpPermission);
		}
	}

	private void cooldownId(Player p, String[] args, ItemStack item) {
		try {
			if (args.length > 3)
				throw new IllegalArgumentException("Wrong param number");
			String cooldownId = args.length == 2 ? null : args[2].toLowerCase();
			ItemMeta meta;
			if (cooldownId != null) {
				meta = ItemTag.get().getTagManager().setTag(ACTION_COOLDOWN_ID_KEY, item, cooldownId).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.cooldownid.set"), p, true,
						"%id%", cooldownId));
			} else {
				meta = ItemTag.get().getTagManager().removeTag(ACTION_COOLDOWN_ID_KEY, item).getItemMeta();
				Util.sendMessage(p,
						UtilsString.fix(this.getConfString("feedback.actions.cooldownid.removed"), p, true));
			}
			item.setItemMeta(meta);
		} catch (Exception e) {
			p.spigot().sendMessage(helpCooldownId);
		}
	}

	private void cooldown(Player p, String[] args, ItemStack item) {
		try {
			if (args.length > 3)
				throw new IllegalArgumentException("Wrong param number");
			int cooldownMs = args.length == 2 ? 0 : Integer.valueOf(args[2]);
			ItemMeta meta;
			if (cooldownMs > 0) {
				meta = ItemTag.get().getTagManager().setTag(ACTION_COOLDOWN_KEY, item, cooldownMs).getItemMeta();
				Util.sendMessage(p,
						UtilsString.fix(this.getConfString("feedback.actions.cooldown.set"), p, true, "%cooldown_ms%",
								String.valueOf(cooldownMs), "%cooldown_seconds%", String.valueOf(cooldownMs / 1000)));
			} else {
				meta = ItemTag.get().getTagManager().removeTag(ACTION_COOLDOWN_KEY, item).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.cooldown.removed"), p, true));
			}
			item.setItemMeta(meta);
		} catch (Exception e) {
			p.spigot().sendMessage(helpCooldown);
		}
	}

	private void uses(Player p, String[] args, ItemStack item) {
		try {
			if (args.length > 3)
				throw new IllegalArgumentException("Wrong param number");
			int uses = args.length == 2 ? 1 : Integer.valueOf(args[2]);
			if (uses == 0)
				throw new IllegalArgumentException();
			ItemMeta meta;
			if (uses != 1) {
				meta = ItemTag.get().getTagManager().setTag(ACTION_USES_KEY, item, uses).getItemMeta();
				if (uses > 0)
					Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.uses.set"), p, true,
							"%uses%", String.valueOf(uses)));
				else
					Util.sendMessage(p,
							UtilsString.fix(this.getConfString("feedback.actions.uses.unlimited"), p, true));
			} else {
				meta = ItemTag.get().getTagManager().removeTag(ACTION_USES_KEY, item).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.uses.set"), p, true, "%uses%",
						String.valueOf(1)));
			}
			item.setItemMeta(meta);
		} catch (Exception e) {
			p.spigot().sendMessage(helpUses);
		}
	}

	//
	private void set(Player p, String[] args, ItemStack item) {
		try {
			if (args.length < 4)
				throw new IllegalArgumentException("Wrong param number");
			int line = Integer.valueOf(args[2]) - 1;
			if (line < 0)
				throw new IllegalArgumentException();
			ArrayList<String> tmp = new ArrayList<>();
			for (int i = 4; i < args.length; i++)
				tmp.add(args[i]);
			String actionType = args[3].toLowerCase();
			String actionInfo = String.join(" ", tmp.toArray(new String[0]));
			try {
				ActionHandler.validateActionType(actionType);
			} catch (Exception e) {
				Util.sendMessage(p,
						ChatColor.translateAlternateColorCodes('&',
								"&c'&6" + actionType + "&c' is not a valid type\n&cValid types &e"
										+ String.join("&c, &e", ActionHandler.getTypes())));
				return;
			}
			try {
				ActionHandler.validateActionInfo(actionType, actionInfo);
			} catch (Exception e) {
				Util.sendMessage(p,
						ChatColor.translateAlternateColorCodes('&',
								"&c'&6" + actionInfo + "&c' is not a valid info for &6" + actionType + "\n"
										+ String.join("\n", ActionHandler.getAction(actionType).getInfo())));
				return;
			}
			String action = actionType + TYPE_SEPARATOR + actionInfo;
			ItemMeta meta;
			if (!ItemTag.get().getTagManager().hasStringTag(ACTIONS_KEY, item))
				meta = ItemTag.get().getTagManager().setTag(ACTIONS_KEY, item, action).getItemMeta();
			else {
				List<String> list = new ArrayList<>(
						Arrays.asList(ItemTag.get().getTagManager().getString(ACTIONS_KEY, item).split(LINK)));
				list.set(line, action);
				meta = ItemTag.get().getTagManager().setTag(ACTIONS_KEY, item, String.join(LINK, list)).getItemMeta();
			}
			Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.set"), p, true, "%line%",
					String.valueOf(line + 1), "%action%", action.replace(TYPE_SEPARATOR, " ")));
			item.setItemMeta(meta);
		} catch (Exception e) {
			p.spigot().sendMessage(helpSet);
		}
	}

	private void remove(Player p, String[] args, ItemStack item) {
		try {
			if (args.length != 3)
				throw new IllegalArgumentException("Wrong param number");
			int line = Integer.valueOf(args[2]) - 1;
			if (line < 0)
				throw new IllegalArgumentException();
			ItemMeta meta;
			String action;
			if (!ItemTag.get().getTagManager().hasStringTag(ACTIONS_KEY, item))
				throw new IllegalArgumentException();
			else {
				List<String> list = new ArrayList<>(
						Arrays.asList(ItemTag.get().getTagManager().getString(ACTIONS_KEY, item).split(LINK)));
				action = list.remove(line);
				meta = ItemTag.get().getTagManager().setTag(ACTIONS_KEY, item, String.join(LINK, list)).getItemMeta();
			}
			Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.remove"), p, true, "%line%",
					String.valueOf(line + 1), "%action%", action.replace(TYPE_SEPARATOR, " ")));
			item.setItemMeta(meta);
		} catch (Exception e) {
			p.spigot().sendMessage(helpRemove);
		}
	}

	// add
	private void add(Player p, String[] args, ItemStack item) {
		try {
			if (args.length < 3)
				throw new IllegalArgumentException("Wrong param number");
			ArrayList<String> tmp = new ArrayList<>();
			for (int i = 3; i < args.length; i++)
				tmp.add(args[i]);
			String actionType = args[2].toLowerCase();
			String actionInfo = String.join(" ", tmp.toArray(new String[0]));
			try {
				ActionHandler.validateActionType(actionType);
			} catch (Exception e) {
				Util.sendMessage(p,
						ChatColor.translateAlternateColorCodes('&',
								"&c'&6" + actionType + "&c' is not a valid type\n&cValid types &e"
										+ String.join("&c, &e", ActionHandler.getTypes())));
				return;
			}
			try {
				ActionHandler.validateActionInfo(actionType, actionInfo);
			} catch (Exception e) {
				Util.sendMessage(p,
						ChatColor.translateAlternateColorCodes('&',
								"&c'&6" + actionInfo + "&c' is not a valid info for &6" + actionType + "\n"
										+ String.join("\n", ActionHandler.getAction(actionType).getInfo())));
				return;
			}
			String action = actionType + TYPE_SEPARATOR + actionInfo;
			ItemMeta meta;
			if (!ItemTag.get().getTagManager().hasStringTag(ACTIONS_KEY, item))
				meta = ItemTag.get().getTagManager().setTag(ACTIONS_KEY, item, action).getItemMeta();
			else {
				List<String> list = new ArrayList<>(
						Arrays.asList(ItemTag.get().getTagManager().getString(ACTIONS_KEY, item).split(LINK)));
				list.add(action);
				meta = ItemTag.get().getTagManager().setTag(ACTIONS_KEY, item, String.join(LINK, list)).getItemMeta();
			}
			Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.add"), p, true, "%action%",
					action.replace(TYPE_SEPARATOR, " ")));
			item.setItemMeta(meta);
		} catch (Exception e) {
			e.printStackTrace();
			p.spigot().sendMessage(helpAdd);
		}
	}

	// add
	private void addLine(Player p, String[] args, ItemStack item) {
		try {
			if (args.length < 4)
				throw new IllegalArgumentException("Wrong param number");
			int line = Integer.valueOf(args[2]) - 1;
			ArrayList<String> tmp = new ArrayList<>();
			for (int i = 4; i < args.length; i++)
				tmp.add(args[i]);
			String actionType = args[3].toLowerCase();
			String actionInfo = String.join(" ", tmp.toArray(new String[0]));
			try {
				ActionHandler.validateActionType(actionType);
			} catch (Exception e) {
				Util.sendMessage(p,
						ChatColor.translateAlternateColorCodes('&',
								"&c'&6" + actionType + "&c' is not a valid type\n&cValid types &e"
										+ String.join("&c, &e", ActionHandler.getTypes())));
				return;
			}
			try {
				ActionHandler.validateActionInfo(actionType, actionInfo);
			} catch (Exception e) {
				Util.sendMessage(p,
						ChatColor.translateAlternateColorCodes('&',
								"&c'&6" + actionInfo + "&c' is not a valid info for &6" + actionType + "\n"
										+ String.join("\n", ActionHandler.getAction(actionType).getInfo())));
				return;
			}
			String action = actionType + TYPE_SEPARATOR + actionInfo;
			ItemMeta meta;
			if (!ItemTag.get().getTagManager().hasStringTag(ACTIONS_KEY, item))
				meta = ItemTag.get().getTagManager().setTag(ACTIONS_KEY, item, action).getItemMeta();
			else {
				List<String> list = new ArrayList<>(
						Arrays.asList(ItemTag.get().getTagManager().getString(ACTIONS_KEY, item).split(LINK)));
				list.add(line, action);
				meta = ItemTag.get().getTagManager().setTag(ACTIONS_KEY, item, String.join(LINK, list)).getItemMeta();
			}
			Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.actions.add"), p, true, "%action%",
					action.replace(TYPE_SEPARATOR, " ")));
			item.setItemMeta(meta);
		} catch (Exception e) {
			p.spigot().sendMessage(helpAddLine);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		switch (args.length) {
		case 2:
			return Util.complete(args[1], actionsSub);
		case 3:
			switch (args[1].toLowerCase()) {
			case "add": {
				return Util.complete(args[2], ActionHandler.getTypes());
			}
			case "setuses": {
				return Util.complete(args[2], Arrays.asList("-1", "1", "5", "10"));
			}
			}
			return new ArrayList<>();
		case 4:
			switch (args[1].toLowerCase()) {
			case "add": {
				List<String> params = new ArrayList<>();
				for (int i = 3; i < args.length; i++)
					params.add(args[i]);
				return ActionHandler.tabComplete(sender, args[2].toLowerCase(), params);
			}
			case "set":
			case "addline": {
				return Util.complete(args[3], ActionHandler.getTypes());
			}
			}
		default:
			switch (args[1].toLowerCase()) {
			case "add": {
				List<String> params = new ArrayList<>();
				for (int i = 3; i < args.length; i++)
					params.add(args[i]);
				return ActionHandler.tabComplete(sender, args[2].toLowerCase(), params);
			}
			case "set":
			case "addline": {
				List<String> params = new ArrayList<>();
				for (int i = 4; i < args.length; i++)
					params.add(args[i]);
				return ActionHandler.tabComplete(sender, args[2].toLowerCase(), params);
			}
			}
		}
		return new ArrayList<String>();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	private void event(PlayerInteractEvent event) {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			TagManager tManager = ItemTag.get().getTagManager();
			if (!tManager.hasStringTag(ACTIONS_KEY, event.getItem()))
				return;
			String permission = tManager.hasStringTag(ACTION_PERMISSION_KEY, event.getItem())
					? tManager.getString(ACTION_PERMISSION_KEY, event.getItem())
					: null;
			if (permission != null && !event.getPlayer().hasPermission(permission))
				return;
			long cooldown = (long) (tManager.hasIntegerTag(ACTION_COOLDOWN_KEY, event.getItem())
					? tManager.getInteger(ACTION_COOLDOWN_KEY, event.getItem())
					: 0);
			if (cooldown > 0) {
				String cooldownId = tManager.hasStringTag(ACTION_COOLDOWN_ID_KEY, event.getItem())
						? tManager.getString(ACTION_COOLDOWN_ID_KEY, event.getItem())
						: "default";
				if (ItemTag.get().getCooldownAPI().hasCooldown(event.getPlayer(), cooldownId))
					return;
				ItemTag.get().getCooldownAPI().setCooldown(event.getPlayer(), cooldownId, cooldown);
			}

			for (String action : tManager.getString(ACTIONS_KEY, event.getItem()).split(LINK))
				try {
					ActionHandler.handleAction(event.getPlayer(), action.split(TYPE_SEPARATOR)[0],
							action.split(TYPE_SEPARATOR)[1]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			
			
			int uses = tManager.hasIntegerTag(ACTION_USES_KEY, event.getItem())
					? tManager.getInteger(ACTION_USES_KEY, event.getItem())
					: 1;
			if (uses > 0) {
				if (uses == 1)
					event.getItem().setAmount(event.getItem().getAmount() - 1);
				else {
					// clone.setAmount(1);
					try {
						if (event.getItem().getAmount() == 1)
							if (event.getHand() == EquipmentSlot.HAND)
								event.getPlayer().getEquipment()
										.setItemInMainHand(tManager.setTag(ACTION_USES_KEY, event.getItem(), uses - 1));
							else
								event.getPlayer().getEquipment()
										.setItemInOffHand(tManager.setTag(ACTION_USES_KEY, event.getItem(), uses - 1));
						else {
							ItemStack clone = event.getItem();
							clone.setAmount(clone.getAmount() - 1);
							if (event.getHand() == EquipmentSlot.HAND)
								event.getPlayer().getEquipment().setItemInMainHand(clone);
							else
								event.getPlayer().getEquipment().setItemInOffHand(clone);
							clone = tManager.setTag(ACTION_USES_KEY, clone.clone(), uses - 1);
							UtilsInventory.giveAmount(event.getPlayer(), clone, 1, ExcessManage.DROP_EXCESS);
						}

					} catch (Throwable t) { //1.8 compability
						if (event.getItem().getAmount() == 1)
							event.getPlayer().getInventory()
									.setItemInHand(tManager.setTag(ACTION_USES_KEY, event.getItem(), uses - 1));
						else {
							ItemStack clone = event.getItem();
							clone.setAmount(clone.getAmount() - 1);
							event.getPlayer().getInventory().setItemInHand(clone);
							clone = tManager.setTag(ACTION_USES_KEY, clone.clone(), uses - 1);
							UtilsInventory.giveAmount(event.getPlayer(), clone, 1, ExcessManage.DROP_EXCESS);
						}
					}
				}

			}

		default:
			return;

		}
	}

}
