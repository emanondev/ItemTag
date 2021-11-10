package emanondev.itemtag.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.gui.Gui;
import emanondev.itemtag.EffectsInfo;
import net.md_5.bungee.api.ChatColor;

public class EffectsGui implements Gui {

	private final Player target;
	private static final YMLConfig config = ItemEdit.get().getConfig("itemtag.yml");
	private static final String subPath = "sub-commands.effects.";
	private final Inventory inventory;
	private final List<EffectData> effects = new ArrayList<>();
	private final List<EquipData> equips = new ArrayList<>();
	private final EffectsInfo info;

	public EffectsGui(Player target, ItemStack item) {
		String title = UtilsString.fix(config.loadString(subPath + "gui.title", "", false), target, true,
				"%player_name%", target.getName());
		this.inventory = Bukkit.createInventory(this, (6) * 9, title);
		this.target = target;
		this.info = new EffectsInfo(item);

		for (PotionEffectType type : PotionEffectType.values())
			if (type != null)
				if (info.hasEffect(type)) {
					PotionEffect effect = info.getEffect(type);

					if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 12)
						effects.add(new EffectData(type, effect.getAmplifier(), effect.isAmbient(),
								effect.hasParticles(), effect.hasIcon()));
					else
						effects.add(new EffectData(type, effect.getAmplifier(), effect.isAmbient(),
								effect.hasParticles(), true));
				} else {
					effects.add(new EffectData(type, -1, true, true, true));
				}
		for (EquipmentSlot slot : EquipmentSlot.values())
			equips.add(new EquipData(slot, info.isValidSlot(slot)));
		updateInventory();
	}

	@Override
	public void onClose(InventoryCloseEvent event) {
	}

	@Override
	public void onClick(InventoryClickEvent event) {
		if (!event.getWhoClicked().equals(target))
			return;
		if (!inventory.equals(event.getClickedInventory()))
			return;
		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
			return;
		if (event.getSlot() < effects.size())
			effects.get(event.getSlot()).onClick(event);
		else
			equips.get(inventory.getSize() - event.getSlot() - 1).onClick(event);
	}

	@Override
	public void onDrag(InventoryDragEvent event) {
	}

	@Override
	public void onOpen(InventoryOpenEvent event) {
		updateInventory();
	}

	private void updateInventory() {
		for (int i = 0; i < effects.size(); i++)
			inventory.setItem(i, effects.get(i).getItem());

		for (int i = 0; i < equips.size(); i++)
			inventory.setItem(inventory.getSize() - 1 - i, equips.get(i).getItem());
	}

	@SuppressWarnings("deprecation")
	private void update(EquipData equipData) {
		info.setSlot(equipData.slot, equipData.enabled);
		info.update();
		target.setItemInHand(info.getItem());
		updateInventory();
	}

	@SuppressWarnings("deprecation")
	private void update(EffectData effectData) {
		if (effectData.amplifier < 0)
			info.removeEffect(effectData.type);
		else if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 12)
			info.addEffect(new PotionEffect(effectData.type, effectData.type.isInstant() ? 1 : (20 * 3600 * 12),
					effectData.amplifier, effectData.ambient, effectData.particles, effectData.icon));
		else
			info.addEffect(new PotionEffect(effectData.type, effectData.type.isInstant() ? 1 : (20 * 3600 * 12),
					effectData.amplifier, effectData.ambient, effectData.particles));

		info.update();
		target.setItemInHand(info.getItem());
		updateInventory();
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public Player getTargetPlayer() {
		return target;
	}

	private class EquipData {
		private final EquipmentSlot slot;
		private boolean enabled;
		private final ItemStack item;

		private EquipData(EquipmentSlot slot, boolean enabled) {
			this.slot = slot;
			this.enabled = enabled;
			switch (slot) {
			case CHEST:
				item = new ItemStack(Material.IRON_CHESTPLATE);
				break;
			case FEET:
				item = new ItemStack(Material.IRON_BOOTS);
				break;
			case HAND:
				item = new ItemStack(Material.IRON_SWORD);
				break;
			case HEAD:
				item = new ItemStack(Material.IRON_HELMET);
				break;
			case LEGS:
				item = new ItemStack(Material.IRON_LEGGINGS);
				break;
			case OFF_HAND:
				item = new ItemStack(Material.SHIELD);
				break;
			default:
				throw new IllegalArgumentException();
			}
			ItemMeta meta = item.getItemMeta();
			meta.addItemFlags(ItemFlag.values());
			meta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD.toString()
					+ Aliases.EQUIPMENT_SLOTS.getName(slot).replace("_", " "));
			item.setItemMeta(meta);
		}

		public ItemStack getItem() {
			ItemMeta meta = item.getItemMeta();
			meta.setLore(Arrays.asList(
					ChatColor.AQUA.toString() + "Enabled: " + ChatColor.YELLOW + Aliases.BOOLEAN.getName(enabled)));
			if (enabled)
				meta.addEnchant(Enchantment.DURABILITY, 1, true);
			else
				meta.removeEnchant(Enchantment.DURABILITY);
			item.setItemMeta(meta);
			return item;
		}

		public void onClick(InventoryClickEvent event) {
			this.enabled = !enabled;
			update(this);
		}
	}

	private class EffectData {
		private final PotionEffectType type;
		private int amplifier;
		private boolean ambient;
		private boolean particles;
		private boolean icon;
		private ItemStack item;

		private EffectData(PotionEffectType type, int amplifier, boolean ambient, boolean particles, boolean icon) {
			this.type = type;
			this.amplifier = amplifier;
			this.ambient = ambient;
			this.particles = particles;
			this.icon = icon;
			item = new ItemStack(Material.POTION);
		}

		public ItemStack getItem() {
			PotionMeta meta = (PotionMeta) item.getItemMeta();
			if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 10)
				meta.setColor(type.getColor());
			meta.addItemFlags(ItemFlag.values());
			meta.setDisplayName(ChatColor.BLUE.toString() + ChatColor.BOLD.toString()
					+ Aliases.POTION_EFFECT.getName(type).replace("_", " "));
			List<String> text = new ArrayList<>();
			text.add(ChatColor.AQUA.toString() + "Level: " + ChatColor.YELLOW.toString() + (amplifier + 1) + " "
					+ ChatColor.GRAY + "[Left/Right Click to change]");
			text.add(ChatColor.AQUA.toString() + "Particles: " + ChatColor.YELLOW.toString()
					+ Aliases.BOOLEAN.getName(particles) + " " + ChatColor.GRAY + "[Toggle with Shift Left click]");
			text.add(ChatColor.AQUA.toString() + "Ambient: " + ChatColor.YELLOW.toString()
					+ Aliases.BOOLEAN.getName(ambient) + " " + ChatColor.GRAY + "[Toggle with Shift Right click]");
			if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 12)
				text.add(ChatColor.AQUA.toString() + "Icon: " + ChatColor.YELLOW.toString()
						+ Aliases.BOOLEAN.getName(icon) + " " + ChatColor.GRAY + "[Toggle with Middle click]");
			text.add(ChatColor.AQUA.toString() + "Duration: " + ChatColor.YELLOW.toString()
					+ (type.isInstant() ? "instant" : "unlimited"));
			meta.setLore(text);
			item.setAmount(Math.max(1, amplifier + 1));
			meta.clearCustomEffects();
			if (amplifier >= 0)
				meta.addCustomEffect(new PotionEffect(type, 1, 0), true);
			item.setItemMeta(meta);
			return item;
		}

		public void onClick(InventoryClickEvent event) {
			switch (event.getClick()) {
			case LEFT:
				amplifier = Math.min(Math.max(-1, amplifier - 1), 127);
				break;
			case RIGHT:
				amplifier = Math.min(Math.max(-1, amplifier + 1), 127);
				break;
			case MIDDLE:
				if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 12)
					icon = !icon;
				else
					return;
				break;
			case SHIFT_LEFT:
				particles = !particles;
				break;
			case SHIFT_RIGHT:
				ambient = !ambient;
				break;
			default:
				return;
			}
			update(this);
		}

	}

}