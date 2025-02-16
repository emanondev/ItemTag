package emanondev.itemtag.gui;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.gui.PagedGui;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import emanondev.itemtag.EffectsInfo;
import emanondev.itemtag.ItemTag;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EffectsGui implements PagedGui {

    private static final int EFFECTS_SLOTS = 5 * 9;
    private final Player target;
    private final Inventory inventory;
    private final List<EffectData> effects = new ArrayList<>();
    private final List<EquipData> equips = new ArrayList<>();
    private final EffectsInfo info;
    private int page = 1;

    public EffectsGui(Player target, ItemStack item) {
        String title = this.getLanguageMessage("gui.effects.title",
                "%player_name%", target.getName());
        this.inventory = Bukkit.createInventory(this, 6 * 9, title);
        this.target = target;
        this.info = new EffectsInfo(item);

        for (PotionEffectType type : PotionEffectType.values())
            if (type != null)
                if (info.hasEffect(type)) {
                    PotionEffect effect = info.getEffect(type);

                    if (VersionUtils.isVersionAfter(1, 13))
                        effects.add(new EffectData(type, effect.getAmplifier(), effect.isAmbient(),
                                effect.hasParticles(), effect.hasIcon()));
                    else
                        effects.add(new EffectData(type, effect.getAmplifier(), effect.isAmbient(),
                                effect.hasParticles(), true));
                } else {
                    effects.add(new EffectData(type, -1, true, true, true));
                }
        for (EquipmentSlot slot : EquipmentSlot.values())
            if (!slot.name().equals("BODY"))
                equips.add(new EquipData(slot/*, info.isValidSlot(slot)*/));
        effects.sort((e1, e2) -> Aliases.POTION_EFFECT.getName(e1.type).compareToIgnoreCase(Aliases.POTION_EFFECT.getName(e2.type)));
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
        if (event.getClick() == ClickType.DOUBLE_CLICK)
            return;

        if (event.getSlot() == EFFECTS_SLOTS + 1) {
            setPage(getPage() - 1);
            return;
        }
        if (event.getSlot() == EFFECTS_SLOTS + 2) {
            setPage(getPage() + 1);
            return;
        }

        if (event.getSlot() > EFFECTS_SLOTS) {
            equips.get(inventory.getSize() - event.getSlot() - 1).onClick(event);
            return;
        }

        effects.get(event.getSlot() + (page - 1) * EFFECTS_SLOTS).onClick(event);
    }

    @Override
    public void onDrag(InventoryDragEvent event) {
    }

    @Override
    public void onOpen(InventoryOpenEvent event) {
        updateInventory();
    }

    private void updateInventory() {
        for (int i = 0; i + (page - 1) * EFFECTS_SLOTS < effects.size() && i < EFFECTS_SLOTS; i++)
            inventory.setItem(i, effects.get(i + (page - 1) * EFFECTS_SLOTS).getItem());

        for (int i = 0; i < equips.size(); i++)
            inventory.setItem(inventory.getSize() - 1 - i, equips.get(i).getItem());

        if (page > 1)
            this.inventory.setItem(EFFECTS_SLOTS + 1, getPreviousPageItem());
        if (page < getMaxPage())
            this.inventory.setItem(EFFECTS_SLOTS + 2, getNextPageItem());
    }

    @SuppressWarnings("deprecation")
    private void update(EquipData equipData) {
        info.toggleSlot(equipData.slot);
        info.update();
        target.setItemInHand(info.getItem());
        updateInventory();
    }

    @SuppressWarnings("deprecation")
    private void update(EffectData effectData) {
        if (effectData.amplifier < 0)
            info.removeEffect(effectData.type);
        else
            info.addEffect(EffectsInfo.craftPotionEffect(effectData.type,
                    effectData.amplifier, effectData.ambient, effectData.particles, effectData.icon));
        info.update();
        target.setItemInHand(info.getItem());
        updateInventory();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public Player getTargetPlayer() {
        return target;
    }

    @Override
    public @NotNull ItemTag getPlugin() {
        return ItemTag.get();
    }

    @Override
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        page = Math.max(1, Math.min(page, getMaxPage()));
        if (page != this.page) {
            this.inventory.clear();
            this.page = page;
            updateInventory();
        }
    }

    public int getMaxPage() {
        return effects.size() / EFFECTS_SLOTS +
                (effects.size() % EFFECTS_SLOTS == 0 ? 0 : 1);
    }

    private class EquipData {
        private final EquipmentSlot slot;
        private final ItemStack item;
        //private boolean enabled;

        private EquipData(EquipmentSlot slot/*, boolean enabled*/) {
            this.slot = slot;
            //this.enabled = enabled;
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
            item.setItemMeta(meta);
        }

        public ItemStack getItem() {
            ItemMeta meta = loadLanguageDescription(ItemUtils.getMeta(item), "gui.effects.slot", "%slot%", Aliases.EQUIPMENT_SLOTS.getName(slot),
                    "%value%", Aliases.BOOLEAN.getName(info.isValidSlot(slot)));
            if (info.isValidSlot(slot))
                meta.addEnchant(Enchantment.LURE, 1, true);
            else
                meta.removeEnchant(Enchantment.LURE);
            item.setItemMeta(meta);
            return item;
        }

        public void onClick(InventoryClickEvent event) {
            update(this);
        }
    }

    private class EffectData {
        private final PotionEffectType type;
        private final ItemStack item;
        private int amplifier;
        private boolean ambient;
        private boolean particles;
        private boolean icon;

        private EffectData(PotionEffectType type, int amplifier, boolean ambient, boolean particles, boolean icon) {
            this.type = type;
            this.amplifier = amplifier;
            this.ambient = ambient;
            this.particles = particles;
            this.icon = icon;
            item = new ItemStack(Material.POTION);
        }

        public ItemStack getItem() {
            PotionMeta meta = (PotionMeta) ItemUtils.getMeta(item);
            if (VersionUtils.isVersionAfter(1, 11))
                meta.setColor(type.getColor());
            meta.addItemFlags(ItemFlag.values());
            loadLanguageDescription(meta, "gui.effects.potion", "%effect%", Aliases.POTION_EFFECT.getName(type)
                            .replace("_", " "), "%level%", String.valueOf(amplifier + 1), "%particles%", Aliases.BOOLEAN.getName(particles),
                    "%ambient%", Aliases.BOOLEAN.getName(ambient), "%icon%",
                    VersionUtils.isVersionAfter(1, 13) ? Aliases.BOOLEAN.getName(icon) : getLanguageMessage("gui.effects.icon-unsupported"), "%duration%",
                    getLanguageMessage(type.isInstant() ? "gui.effects.potion-instant" : "gui.effects.potion-unlimited"),
                    "%middle_click%", getLanguageMessage("gui.middleclick." + (getTargetPlayer().getGameMode() == GameMode.CREATIVE ? "creative" : "other")));
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
                case NUMBER_KEY:
                    if (event.getHotbarButton() != 0)
                        return;
                case CREATIVE:
                case MIDDLE: //middle click or 1, note middle click doesn't work unless in creative mode
                    if (VersionUtils.isVersionAfter(1, 13))
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