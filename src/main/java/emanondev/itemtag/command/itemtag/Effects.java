package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.ItemEdit;
import emanondev.itemtag.EffectsInfo;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import emanondev.itemtag.gui.EffectsGui;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Effects extends ListenerSubCmd {
    private final boolean is1_8 = ItemEdit.GAME_VERSION < 9;
    private final boolean is1_10orLower = ItemEdit.GAME_VERSION < 11;

    public Effects(ItemTagCommand cmd) {
        super("effects", cmd, true, true);
        this.load();
        if (ItemEdit.GAME_VERSION >= 11)
            getPlugin().registerListener(new EffectsResurrectListener(this));
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
                //old item had any active effects
                List<PotionEffect> instant = new ArrayList<>();
                HashMap<PotionEffectType, PotionEffect> oldEffects = new HashMap<>();
                HashMap<PotionEffectType, PotionEffect> newEffects = new HashMap<>();
                for (PotionEffect effect : oldInfo.getEffects())
                    if (!effect.getType().isInstant())
                        //load all the old effects
                        oldEffects.put(effect.getType(), effect);
                if (!isAirOrNull(event.getTo())) {
                    EffectsInfo newInfo = new EffectsInfo(event.getTo());
                    if (newInfo.isValidSlot(event.getSlotType()) && newInfo.hasAnyEffects()) {
                        //new object has any active effects
                        for (PotionEffect effect : newInfo.getEffects()) {
                            //load all the new effects
                            if (effect.getType().isInstant())
                                instant.add(effect);
                            else
                                newEffects.put(effect.getType(), effect);
                        }
                    }
                }
                for (EquipmentSlot slot : EquipmentSlot.values()) {
                    //for each slot (except event slot) look on effects
                    if (slot == event.getSlotType())
                        continue;
                    ItemStack item = getEquip(event.getPlayer(), slot);
                    if (isAirOrNull(item))
                        continue;
                    EffectsInfo info = new EffectsInfo(item);
                    if (!info.isValidSlot(slot) || !info.hasAnyEffects())
                        continue;
                    for (PotionEffect effect : info.getEffects()) {
                        //for each effect if it's missing or higher put it on oldEffects, same for newEffects
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
                //apply instant effects
                for (PotionEffect effect : instant)
                    addEffect(event.getPlayer(), effect);


                HashSet<PotionEffectType> types = new HashSet<>(oldEffects.keySet());
                types.addAll(newEffects.keySet());
                for (PotionEffectType eType : types) {
                    //for each effect type present on oldEffects or newEffects
                    if (newEffects.containsKey(eType)) {
                        if (!oldEffects.containsKey(eType) || oldEffects.get(eType).getAmplifier() != newEffects.get(eType).getAmplifier())
                            //if a newEffect was not present on oldEffect, or has different amplifier: reset it
                            addEffect(event.getPlayer(), newEffects.get(eType));
                    } else //if (oldEffects.containsKey(eType)) //which is always true
                        event.getPlayer().removePotionEffect(eType);
                }
                return;
            }
        }


        //old item has no active effects (or it's null/air)
        if (!isAirOrNull(event.getTo())) {
            EffectsInfo newInfo = new EffectsInfo(event.getTo());
            if (newInfo.isValidSlot(event.getSlotType()) && newInfo.hasAnyEffects()) {
                //if the new item has some active effects
                for (PotionEffect effect : newInfo.getEffects())
                    if (effect.getType().isInstant())
                        addEffect(event.getPlayer(), effect);
                    else if (!event.getPlayer().hasPotionEffect(effect.getType()))
                        addEffect(event.getPlayer(), effect);
                    else {
                        PotionEffect currentEffect = null;
                        if (!is1_10orLower)// safe
                            currentEffect = event.getPlayer().getPotionEffect(effect.getType());
                        else
                            for (PotionEffect k : event.getPlayer().getActivePotionEffects())
                                if (k.getType().equals(effect.getType())) {
                                    currentEffect = k;
                                    break;
                                }

                        if (currentEffect.getDuration() < 3600 * 20) //could be changed checking the whole equipment effects, but this way seems faster and still fair
                            if (ItemEdit.GAME_VERSION >= 16)
                                addEffect(event.getPlayer(), effect);
                            else if (currentEffect.getAmplifier() < effect.getAmplifier())
                                addEffect(event.getPlayer(), effect);
                    }
            }
        }
    }

    private void addEffect(Player target, PotionEffect effect) {
        if (ItemEdit.GAME_VERSION >= 16)
            target.addPotionEffect(effect);
        else
            target.addPotionEffect(effect, true);
    }

    @EventHandler(ignoreCancelled = true)
    private void event(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET)
            Bukkit.getScheduler().runTaskLater(this.getPlugin(), () -> restoreEffects(event.getPlayer()), 1L);
    }

    public void restoreEffects(Player p) {
        if (!p.isOnline() || p.isDead())
            return;
        HashMap<PotionEffectType, PotionEffect> newEffects = new HashMap<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack item = p.getInventory().getItem(slot);
            if (isAirOrNull(item))
                continue;
            EffectsInfo newInfo = new EffectsInfo(item);
            if (newInfo.isValidSlot(slot) && newInfo.hasAnyEffects()) {
                //new object has any active effects
                for (PotionEffect effect : newInfo.getEffects()) {
                    //load all the new effects
                    if (effect.getType().isInstant())
                        continue;
                    if (!newEffects.containsKey(effect.getType()) ||
                            effect.getAmplifier() > newEffects.get(effect.getType()).getAmplifier())
                        newEffects.put(effect.getType(), effect);
                }
            }
        }
        for (PotionEffect effect : newEffects.values())
            if (ItemEdit.GAME_VERSION >= 16)
                p.addPotionEffect(effect);
            else
                p.addPotionEffect(effect, true);
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
