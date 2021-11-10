package emanondev.itemtag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import emanondev.itemedit.ItemEdit;

public class EffectsInfo {
	private final static String EFFECTS_LIST_KEY = ItemTag.get().getName().toLowerCase()+":effects_list";
	private final static String EFFECTS_EQUIPS_KEY = ItemTag.get().getName().toLowerCase()+":effects_equips";

	private final EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
	private final HashMap<PotionEffectType, PotionEffect> effects = new HashMap<>();
	private ItemStack item;

	public EffectsInfo(ItemStack item) {
		this.item = item;
		if (ItemTag.get().getTagManager().hasStringTag(EFFECTS_LIST_KEY, item))
			for (PotionEffect effect : stringToEffects(ItemTag.get().getTagManager().getString(EFFECTS_LIST_KEY, item)))
				effects.put(effect.getType(), effect);
		if (ItemTag.get().getTagManager().hasStringTag(EFFECTS_EQUIPS_KEY, item))
			for (EquipmentSlot slot : stringToEquips(ItemTag.get().getTagManager().getString(EFFECTS_EQUIPS_KEY, item)))
				slots.add(slot);
	}

	private String effectsToString() {
		if (effects.isEmpty())
			return null;
		StringBuilder str = new StringBuilder("");
		List<PotionEffect> list = new ArrayList<>(effects.values());

		if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 12)
			str.append(list.get(0).getType().getName() + "," + list.get(0).getAmplifier() + ","
					+ list.get(0).isAmbient() + "," + list.get(0).hasParticles() + "," + list.get(0).hasIcon());
		else
			str.append(list.get(0).getType().getName() + "," + list.get(0).getAmplifier() + ","
					+ list.get(0).isAmbient() + "," + list.get(0).hasParticles() + ",true");
		for (int i = 1; i < list.size(); i++) {
			str.append(";");
			if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 12)
				str.append(list.get(i).getType().getName() + "," + list.get(i).getAmplifier() + ","
						+ list.get(i).isAmbient() + "," + list.get(i).hasParticles() + "," + list.get(i).hasIcon());
			else
				str.append(list.get(i).getType().getName() + "," + list.get(i).getAmplifier() + ","
						+ list.get(i).isAmbient() + "," + list.get(i).hasParticles() + ",true");
		}
		return str.toString();
	}

	private List<PotionEffect> stringToEffects(String txt) {
		List<PotionEffect> list = new ArrayList<>();
		if (txt == null || txt.isEmpty())
			return list;
		String[] effects = txt.split(";");
		for (String rawEffect : effects) {
			String[] args = rawEffect.split(",");
			if (Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) > 12)
				list.add(new PotionEffect(PotionEffectType.getByName(args[0]),
						PotionEffectType.getByName(args[0]).isInstant() ? 1 : (20 * 3600 * 12),
						Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]),
						Boolean.parseBoolean(args[4])));
			else
				list.add(new PotionEffect(PotionEffectType.getByName(args[0]),
						PotionEffectType.getByName(args[0]).isInstant() ? 1 : (20 * 3600 * 12),
						Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3])));
		}
		return list;
	}

	private String equipsToString() {
		if (slots.isEmpty())
			return null;
		List<EquipmentSlot> list = new ArrayList<>(slots);
		StringBuilder str = new StringBuilder("");
		str.append(list.get(0).name());
		for (int i = 1; i < list.size(); i++)
			str.append(";").append(list.get(i).name());
		return str.toString();
	}

	private Collection<EquipmentSlot> stringToEquips(String txt) {
		EnumSet<EquipmentSlot> equips = EnumSet.noneOf(EquipmentSlot.class);
		if (txt == null || txt.isEmpty())
			return equips;
		String[] eq = txt.split(";");
		for (String rawEq : eq)
			equips.add(EquipmentSlot.valueOf(rawEq));
		return equips;
	}

	public PotionEffect getEffect(PotionEffectType type) {
		return effects.get(type);
	}

	public boolean hasEffect(PotionEffectType type) {
		return effects.containsKey(type);
	}

	public Collection<PotionEffect> getEffects() {
		return effects.values();
	}

	public boolean isValidSlot(EquipmentSlot slot) {
		return slots.contains(slot);
	}

	public EnumSet<EquipmentSlot> getValidSlots() {
		return slots;
	}

	public void addEffect(PotionEffect effect) {
		effects.put(effect.getType(), effect);
	}

	public void removeEffect(PotionEffectType type) {
		effects.remove(type);
	}

	public void setSlot(EquipmentSlot slot, boolean value) {
		if (value)
			slots.add(slot);
		else
			slots.remove(slot);
	}

	public void update() {
		if (effects.isEmpty())
			item = ItemTag.get().getTagManager().removeTag(EFFECTS_LIST_KEY, item);
		else
			item = ItemTag.get().getTagManager().setTag(EFFECTS_LIST_KEY, item, effectsToString());
		if (slots.isEmpty())
			item = ItemTag.get().getTagManager().removeTag(EFFECTS_EQUIPS_KEY, item);
		else
			item = ItemTag.get().getTagManager().setTag(EFFECTS_EQUIPS_KEY, item, equipsToString());
	}

	public ItemStack getItem() {
		return item;
	}

	public boolean hasAnyEffects() {
		return !effects.isEmpty();
	}
}