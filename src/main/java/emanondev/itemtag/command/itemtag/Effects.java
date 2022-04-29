package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.ItemEdit;
import emanondev.itemtag.EffectsInfo;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import emanondev.itemtag.gui.EffectsGui;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Effects extends ListenerSubCmd {
    private final boolean is1_8 = Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) < 9;
    private final boolean is1_10orLower = Integer.parseInt(ItemEdit.NMS_VERSION.split("_")[1]) < 11;

    public Effects(ItemTagCommand cmd) {
        super("effects", cmd, true, true);
        this.load();
    }

    public void reload() {
        super.reload();
        this.load();
    }

    private void load() {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length != 1) {
            onFail(p, alias);
            return;
        }
        p.openInventory(new EffectsGui(p, p.getItemInHand()).getInventory());
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }


    @EventHandler
    public void onEquipChange(EquipmentChangeEvent event) {
        if (!isAirOrNull(event.getFrom())) {
            EffectsInfo oldInfo = new EffectsInfo(event.getFrom());
            if (oldInfo.isValidSlot(event.getSlotType()) && oldInfo.hasAnyEffects()) {
                List<PotionEffect> instant = new ArrayList<>();
                HashMap<PotionEffectType, PotionEffect> oldEffects = new HashMap<>();
                HashMap<PotionEffectType, PotionEffect> newEffects = new HashMap<>();
                for (PotionEffect effect : oldInfo.getEffects())
                    if (!effect.getType().isInstant())
                        oldEffects.put(effect.getType(), effect);
                if (!isAirOrNull(event.getTo())) {
                    EffectsInfo newInfo = new EffectsInfo(event.getTo());
                    if (newInfo.isValidSlot(event.getSlotType()) && newInfo.hasAnyEffects()) {
                        for (PotionEffect effect : newInfo.getEffects()) {
                            if (effect.getType().isInstant())
                                instant.add(effect);
                            else
                                newEffects.put(effect.getType(), effect);
                        }
                    }
                }
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    if (slot == event.getSlotType())
                        continue;
                    ItemStack item = getEquip(event.getPlayer(), slot);
                    if (isAirOrNull(item))
                        continue;
                    EffectsInfo info = new EffectsInfo(item);
                    if (!info.isValidSlot(slot) || !info.hasAnyEffects())
                        continue;
                    for (PotionEffect effect : info.getEffects()) {
                        if (effect.getType().isInstant())
                            continue;
                        if (!oldEffects.containsKey(effect.getType()))
                            oldEffects.put(effect.getType(), effect);
                        else if (oldEffects.get(effect.getType()).getAmplifier() < effect.getAmplifier())
                            oldEffects.put(effect.getType(), effect);
                        if (!newEffects.containsKey(effect.getType()))
                            newEffects.put(effect.getType(), effect);
                        else if (newEffects.get(effect.getType()).getAmplifier() < effect.getAmplifier())
                            newEffects.put(effect.getType(), effect);
                    }
                }
                for (PotionEffect effect : instant)
                    event.getPlayer().addPotionEffect(effect, true);
                HashSet<PotionEffectType> types = new HashSet<>(oldEffects.keySet());
                types.addAll(newEffects.keySet());
                for (PotionEffectType eType : types) {
                    if (newEffects.containsKey(eType))
                        event.getPlayer().addPotionEffect(newEffects.get(eType), true);
                    else if (oldEffects.containsKey(eType))
                        event.getPlayer().removePotionEffect(eType);
                }
                return;
            }
        }
        if (!isAirOrNull(event.getTo())) {
            EffectsInfo newInfo = new EffectsInfo(event.getTo());
            if (newInfo.isValidSlot(event.getSlotType()) && newInfo.hasAnyEffects()) {
                for (PotionEffect effect : newInfo.getEffects())
                    if (effect.getType().isInstant())
                        event.getPlayer().addPotionEffect(effect, true);
                    else if (!event.getPlayer().hasPotionEffect(effect.getType()))
                        event.getPlayer().addPotionEffect(effect, true);
                    else {
                        PotionEffect pEffect = null;
                        if (!is1_10orLower)// safe
                            pEffect = event.getPlayer().getPotionEffect(effect.getType());
                        else
                            for (PotionEffect k : event.getPlayer().getActivePotionEffects())
                                if (k.getType().equals(effect.getType())) {
                                    pEffect = k;
                                    break;
                                }
                        if (pEffect.getDuration() < 3600 * 20)
                            event.getPlayer().addPotionEffect(effect, true);
                        else if (pEffect.getAmplifier() <= effect.getAmplifier())
                            event.getPlayer().addPotionEffect(effect, true);
                    }
            }
        }
    }


    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    protected ItemStack getEquip(Player p, EquipmentSlot slot) {
        switch (slot) {
            case CHEST:
                return p.getEquipment().getChestplate();
            case FEET:
                return p.getEquipment().getBoots();
            case HAND:
                if (!is1_8)// safe
                    return p.getInventory().getItemInMainHand();
                return p.getInventory().getItemInHand();
            case HEAD:
                return p.getEquipment().getHelmet();
            case LEGS:
                return p.getEquipment().getLeggings();
        }// safe
        if (!is1_8 && slot == EquipmentSlot.OFF_HAND)
            return p.getInventory().getItemInOffHand();
        return null;
    }

}
