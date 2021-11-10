package emanondev.itemtag.command.itemtag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;

public class Usable extends ListenerSubCmd {
	private final static String USABLE_KEY = ItemTag.get().getName().toLowerCase()+":usable";

	public Usable(ItemTagCommand cmd) {
		super("usable", cmd, true, true);
	}

	@Override
	public void onCmd(CommandSender sender, String[] args) {
		Player p = (Player) sender;
		ItemStack item = this.getItemInHand(p);
		try {
			if (args.length > 2)
				throw new IllegalArgumentException("Wrong param number");
			boolean placeableValue = args.length == 2 ? Aliases.BOOLEAN.convertAlias(args[1])
					: ItemTag.get().getTagManager().hasBooleanTag(USABLE_KEY, item);
			ItemMeta meta;
			if (placeableValue) {
				meta = ItemTag.get().getTagManager().removeTag(USABLE_KEY, item).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.usable"), p, true));
			} else {
				meta = ItemTag.get().getTagManager().setTag(USABLE_KEY, item, false).getItemMeta();
				Util.sendMessage(p, UtilsString.fix(this.getConfString("feedback.unusable"), p, true));
			}
			item.setItemMeta(meta);
		} catch (Exception e) {
			onFail(p);
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args) {
		if (args.length == 2)
			return Util.complete(args[1], Aliases.BOOLEAN);
		return new ArrayList<String>();
	}

	@EventHandler
	private void event(PlayerInteractEvent event) {
		switch (event.getAction()) {
		case RIGHT_CLICK_AIR:
		case RIGHT_CLICK_BLOCK:
			if (ItemTag.get().getTagManager().hasBooleanTag(USABLE_KEY, event.getItem())) {
				event.setUseItemInHand(Result.DENY);
				new BukkitRunnable() {
					public void run() {
						event.getPlayer().updateInventory();
					}
				}.runTaskLater(ItemTag.get(), 1L);
			}
		default:
			return;

		}
	}

	@EventHandler
	private void event(PlayerBucketFillEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (item == null || item.getType() != Material.BUCKET)
			item = event.getPlayer().getInventory().getItemInOffHand();

		if (ItemTag.get().getTagManager().hasBooleanTag(USABLE_KEY, item))
			event.setCancelled(true);
	}

	@EventHandler
	private void event(PlayerBucketEmptyEvent event) {
		ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
		if (item == null || (item.getType() != Material.LAVA_BUCKET && item.getType() != Material.WATER_BUCKET))
			item = event.getPlayer().getInventory().getItemInOffHand();

		if (ItemTag.get().getTagManager().hasBooleanTag(USABLE_KEY, item))
			event.setCancelled(true);
	}
}
