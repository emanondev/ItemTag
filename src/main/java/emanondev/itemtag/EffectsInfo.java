package emanondev.itemtag;

import emanondev.itemedit.utility.VersionUtils;
import lombok.Getter;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class EffectsInfo {
    private final static String EFFECTS_LIST_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":effects_list";
    private final static String EFFECTS_EQUIPS_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":effects_equips";

    private final EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
    private final HashMap<PotionEffectType, PotionEffect> effects = new HashMap<>();
    @Getter
    private final ItemStack item;
    private final TagItem tagItem;

    public EffectsInfo(@Nullable ItemStack item) {
        this.item = item;
        this.tagItem = ItemTag.getTagItem(this.item);
        if (tagItem.hasStringTag(EFFECTS_LIST_KEY))
            for (PotionEffect effect : stringToEffects(tagItem.getString(EFFECTS_LIST_KEY)))
                effects.put(effect.getType(), effect);
        if (tagItem.hasStringTag(EFFECTS_EQUIPS_KEY))
            slots.addAll(stringToEquips(tagItem.getString(EFFECTS_EQUIPS_KEY)));
        if (slots.isEmpty())
            slots.addAll(ItemTagUtility.getPlayerEquipmentSlots());
    }

    /**
     * @return an effect with unlimited duration (or just big if < 1.19.4)
     */
    public static PotionEffect craftPotionEffect(PotionEffectType type, int amplifier, boolean ambient,
                                                 boolean particles,
                                                 boolean icon) {
        int duration = type.isInstant() ? 1 : (VersionUtils.isVersionUpTo(1, 19, 3) ?
                (20 * 3600 * 12) : PotionEffect.INFINITE_DURATION);
        if (VersionUtils.isVersionAfter(1, 12))
            return new PotionEffect(type, duration, amplifier, ambient, particles, icon);
        return new PotionEffect(type, duration, amplifier, ambient, particles);
    }

    private String effectsToString() {
        if (effects.isEmpty())
            return null;
        List<PotionEffect> list = new ArrayList<>(effects.values());
        StringBuilder str = new StringBuilder().append(list.get(0).getType().getName())//TODO should use list.get(0).getType().getKey() for 1.20.6+
                .append(",").append(list.get(0).getAmplifier())
                .append(",").append(list.get(0).isAmbient()).append(",").append(list.get(0).hasParticles());
        if (VersionUtils.isVersionAfter(1, 13))
            str.append(",").append(list.get(0).hasIcon());
        else
            str.append(",true");
        for (int i = 1; i < list.size(); i++) {
            str.append(";").append(list.get(i).getType().getName()).append(",").append(list.get(i).getAmplifier()).append(",")
                    .append(list.get(i).isAmbient()).append(",").append(list.get(i).hasParticles());
            if (VersionUtils.isVersionAfter(1, 13))
                str.append(",").append(list.get(i).hasIcon());
            else
                str.append(",true");
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
            PotionEffectType type = PotionEffectType.getByName(args[0]);
            /* TODO should use Registry.EFFECT.getByKey for 1.20.6+
            if (type==null)
                type = Registry.EFFECT.getByKey(args[0]);
             */
            if (type == null)
                continue;
            list.add(craftPotionEffect(type,
                    Integer.parseInt(args[1]), Boolean.parseBoolean(args[2]), Boolean.parseBoolean(args[3]),
                    Boolean.parseBoolean(args[4])));
        }
        return list;
    }

    private String equipsToString() {
        if (slots.size() == ItemTagUtility.getPlayerEquipmentSlots().size())
            return null;
        List<EquipmentSlot> list = new ArrayList<>(slots);
        StringBuilder str = new StringBuilder();
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

    public Map<PotionEffectType, PotionEffect> getEffectsMap() {
        return Collections.unmodifiableMap(effects);
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

    public void toggleSlot(EquipmentSlot slot) {
        if (slots.contains(slot)) {
            slots.remove(slot);
            if (slots.isEmpty())
                slots.addAll(ItemTagUtility.getPlayerEquipmentSlots());
            return;
        }
        slots.add(slot);
    }

    public void update() {
        if (effects.isEmpty())
            tagItem.removeTag(EFFECTS_LIST_KEY);
        else
            tagItem.setTag(EFFECTS_LIST_KEY, effectsToString());
        if (slots.size() == ItemTagUtility.getPlayerEquipmentSlots().size())
            tagItem.removeTag(EFFECTS_EQUIPS_KEY);
        else
            tagItem.setTag(EFFECTS_EQUIPS_KEY, equipsToString());
    }

    public boolean hasAnyEffects() {
        return !effects.isEmpty();
    }
}