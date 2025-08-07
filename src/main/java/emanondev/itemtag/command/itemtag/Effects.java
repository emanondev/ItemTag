package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.VersionUtils;
import emanondev.itemtag.EffectsInfo;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.ItemTagUtility;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.ListenerSubCmd;
import emanondev.itemtag.compability.FoliaRunnable;
import emanondev.itemtag.compability.SchedulerUtils;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import emanondev.itemtag.gui.EffectsGui;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;

public class Effects extends ListenerSubCmd {

    public Effects(ItemTagCommand cmd) {
        super("effects", cmd, true, true);
        this.load();
        if (VersionUtils.isVersionAfter(1, 11))
            getPlugin().registerListener(new EffectsResurrectListener(this));
    }

    public void reload() {
        super.reload();
        this.load();
    }

    private void load() {
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length != 1) {
            switch (args[1].toLowerCase()) {
                case "set": {
                    set(p, alias, args);
                    return;
                }
                case "modify": {
                    modify(p, alias, args);
                    return;
                }
                case "slots": {
                    slots(p, alias, args);
                    return;
                }
                case "remove": {
                    remove(p, alias, args);
                    return;
                }
            }
            onFail(p, alias);
            return;
        }
        p.openInventory(new EffectsGui(p, getItemInHand(p)).getInventory());
    }

    //it effects set <type> <ampl> [] [] []
    private void set(Player p, String alias, String[] args) {
        try {
            PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[2]);
            int amplifier = Integer.parseInt(args[3]) - 1;
            Boolean particles = args.length >= 5 ? Aliases.BOOLEAN.convertAlias(args[4]) : Boolean.TRUE;
            Boolean ambient = args.length >= 6 ? Aliases.BOOLEAN.convertAlias(args[5]) : Boolean.FALSE;
            Boolean icon = args.length >= 7 ? Aliases.BOOLEAN.convertAlias(args[6]) : Boolean.TRUE;
            if (type == null || particles == null || ambient == null || icon == null)
                throw new IllegalArgumentException();

            EffectsInfo info = new EffectsInfo(getItemInHand(p));
            if (amplifier < 0)
                info.removeEffect(type);
            else
                info.addEffect(EffectsInfo.craftPotionEffect(type, amplifier, ambient, particles, icon));
            info.update();
            ItemUtils.setHandItem(p, info.getItem());
        } catch (Exception e) {
            //TODO
            onFail(p, alias);
        }
    }

    private void modify(Player p, String alias, String[] args) {
        try {
            PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[2]);
            EffectsInfo info = new EffectsInfo(getItemInHand(p));
            PotionEffect effect = info.getEffect(type);
            int amplifier = Integer.parseInt(args[3]) - 1;
            Boolean particles = args.length >= 5 ? Aliases.BOOLEAN.convertAlias(args[4]) : effect == null ? Boolean.TRUE : (Boolean) effect.hasParticles();
            Boolean ambient = args.length >= 6 ? Aliases.BOOLEAN.convertAlias(args[5]) : effect == null ? Boolean.FALSE : (Boolean) effect.isAmbient();
            Boolean icon = args.length >= 7 ? Aliases.BOOLEAN.convertAlias(args[6]) : effect == null ? Boolean.TRUE : (Boolean) effect.hasIcon();
            if (type == null || particles == null || ambient == null || icon == null)
                throw new IllegalArgumentException();

            amplifier += effect != null ? effect.getAmplifier() : 0;
            if (amplifier < 0)
                info.removeEffect(type);
            else
                info.addEffect(EffectsInfo.craftPotionEffect(type, amplifier, ambient, particles, icon));
            info.update();
            ItemUtils.setHandItem(p, info.getItem());
        } catch (Exception e) {
            //TODO
            onFail(p, alias);
        }
    }

    private void remove(Player p, String alias, String[] args) {
        try {
            PotionEffectType type = Aliases.POTION_EFFECT.convertAlias(args[2]);
            if (type == null)
                throw new IllegalArgumentException();

            EffectsInfo info = new EffectsInfo(getItemInHand(p));
            if (!info.hasEffect(type))
                return;
            info.removeEffect(type);
            info.update();
            ItemUtils.setHandItem(p, info.getItem());
        } catch (Exception e) {
            //TODO
            onFail(p, alias);
        }
    }

    private void slots(Player p, String alias, String[] args) {
        try {
            EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
            for (int i = 2; i < args.length; i++)
                slots.add(Aliases.EQUIPMENT_SLOTS.convertAlias(args[i]));

            EffectsInfo info = new EffectsInfo(getItemInHand(p));
            for (EquipmentSlot slot : ItemTagUtility.getPlayerEquipmentSlots())
                if (slots.contains(slot) != info.isValidSlot(slot))
                    info.toggleSlot(slot);
            info.update();
            ItemUtils.setHandItem(p, info.getItem());
        } catch (Exception e) {
            //TODO
            onFail(p, alias);
        }
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        switch (args.length) {
            case 2: {
                return CompleteUtility.complete(args[1], "set", "modify", "slots", "remove");
            }
            case 3: {
                switch (args[1].toLowerCase()) {
                    case "set":
                    case "modify":
                    case "remove": {
                        return CompleteUtility.complete(args[2], Aliases.POTION_EFFECT);
                    }
                    case "slots": {
                        return CompleteUtility.complete(args[2], Aliases.EQUIPMENT_SLOTS);
                    }
                }
                return Collections.emptyList();
            }
            case 4: {
                switch (args[1].toLowerCase()) {
                    case "set":
                    case "modify": {
                        return CompleteUtility.complete(args[3], "1", "2", "3");
                    }
                    case "slots": {
                        return CompleteUtility.complete(args[3], Aliases.EQUIPMENT_SLOTS);
                    }
                }
                return Collections.emptyList();
            }
            case 5:
            case 6:
            case 7: {
                switch (args[1].toLowerCase()) {
                    case "set":
                    case "modify": {
                        return CompleteUtility.complete(args[4], Aliases.BOOLEAN);
                    }
                    case "slots": {
                        return CompleteUtility.complete(args[4], Aliases.EQUIPMENT_SLOTS);
                    }
                }
                return Collections.emptyList();
            }
            case 8: {
                if ("slots".equalsIgnoreCase(args[1])) {
                    return CompleteUtility.complete(args[4], Aliases.EQUIPMENT_SLOTS);
                }
                return Collections.emptyList();
            }
        }
        return Collections.emptyList();
    }

    private Map<PotionEffectType, PotionEffect> getPotionEffects(ItemStack item, EquipmentSlot slot, boolean ignoreInstant) {
        if (ItemUtils.isAirOrNull(item))
            return Collections.emptyMap();
        EffectsInfo info = new EffectsInfo(item);
        if (!info.isValidSlot(slot) || !info.hasAnyEffects())
            return Collections.emptyMap();
        if (!ignoreInstant)
            return info.getEffectsMap();
        HashMap<PotionEffectType, PotionEffect> map = new HashMap<>(info.getEffectsMap());
        map.entrySet().removeIf(e -> e.getKey().isInstant());
        return map;
    }


    private int getAmplifier(Map<PotionEffectType, PotionEffect> map, PotionEffectType type) {
        if (!map.containsKey(type))
            return -1;
        return map.get(type).getAmplifier();
    }

    @EventHandler
    public void onEquipChange(EquipmentChangeEvent event) {
        Map<PotionEffectType, PotionEffect> oldEffects = getPotionEffects(event.getFrom(), event.getSlotType(), true);
        Map<PotionEffectType, PotionEffect> newEffects = new HashMap<>(getPotionEffects(event.getTo(), event.getSlotType(), false));
        if (oldEffects.isEmpty() && newEffects.isEmpty())
            return;
        Map<PotionEffectType, PotionEffect> equipsEffects = new HashMap<>();
        for (EquipmentSlot slot : ItemTagUtility.getPlayerEquipmentSlots()) {
            //for each slot (except event slot) look on effects
            if (slot == event.getSlotType())
                continue;
            getPotionEffects(getEquip(event.getPlayer(), slot), slot, true).forEach((k, v) -> {
                if (getAmplifier(equipsEffects, k) < v.getAmplifier())
                    equipsEffects.put(k, v);
            });
        }
        HashSet<PotionEffectType> keys = new HashSet<>(oldEffects.keySet());
        keys.addAll(newEffects.keySet());
        keys.forEach((k) -> {
            if (k.isInstant()) {
                addEffect(event.getPlayer(), k, newEffects.get(k));
                return;
            }
            int newAmplifier = getAmplifier(newEffects, k);
            int oldAmplifier = getAmplifier(oldEffects, k);
            int equipAmplifier = getAmplifier(equipsEffects, k);
            PotionEffect max = newAmplifier > equipAmplifier ? newEffects.get(k) : equipsEffects.get(k);
            int maxAmplifier = Math.max(newAmplifier, equipAmplifier);
            //Bukkit.broadcastMessage(oldAmplifier+" -> "+newAmplifier + " ("+equipAmplifier+") of "+k.getName() +" Replace "+(oldAmplifier != maxAmplifier));
            if (oldAmplifier != maxAmplifier) //TODO has some unnecessary replacements
                addEffect(event.getPlayer(), k, max);
        });
    }

    @EventHandler
    private void onPlayerRespawn(PlayerRespawnEvent event) {
        SchedulerUtils.runTaskLater(event.getPlayer().getLocation(), new FoliaRunnable() {
            public void run() {
                for (EquipmentSlot slot : ItemTagUtility.getPlayerEquipmentSlots()) {
                    ItemStack equip = getEquip(event.getPlayer(), slot);
                    if (ItemUtils.isAirOrNull(equip))
                        continue;
                    EffectsInfo newInfo = new EffectsInfo(equip);
                    if (!(newInfo.isValidSlot(slot) && newInfo.hasAnyEffects()))
                        continue;
                    //if the new item has some active effects
                    for (PotionEffect effect : newInfo.getEffects()) {
                        ItemTag.get().log(effect.getType() + " " + effect.getAmplifier() + 1);
                        if (effect.getType().isInstant() || !event.getPlayer().hasPotionEffect(effect.getType()))
                            addEffect(event.getPlayer(), effect.getType(), effect);

                        else {
                            PotionEffect currentEffect = null;
                            if (VersionUtils.isVersionAfter(1, 11))// safe
                                currentEffect = event.getPlayer().getPotionEffect(effect.getType());
                            else
                                for (PotionEffect k : event.getPlayer().getActivePotionEffects())
                                    if (k.getType().equals(effect.getType())) {
                                        currentEffect = k;
                                        break;
                                    }
                            if (currentEffect.getDuration() < 3600 * 20 && currentEffect.getDuration() >= 0) //could be changed checking the whole equipment effects, but this way seems faster and still fair
                                if (VersionUtils.isVersionAfter(1, 16))
                                    addEffect(event.getPlayer(), effect.getType(), effect);
                                else if (currentEffect.getAmplifier() <= effect.getAmplifier())
                                    addEffect(event.getPlayer(), effect.getType(), effect);
                        }
                    }
                }
            }
        }, 1L); //effect are resetted just after playerrespawnevent

    }


    private void addEffect(@NotNull Player target, @NotNull PotionEffectType type, @Nullable PotionEffect effect) {
        if (effect == null) {
            target.removePotionEffect(type);
            return;
        }
        if (type.isInstant()) {
            target.addPotionEffect(effect);
            return;
        }
        if(VersionUtils.hasFoliaAPI()) {
            try {
                Method getRegionScheduler = ItemTag.get().getServer().getClass().getMethod("getRegionScheduler");
                RegionScheduler regionScheduler = (RegionScheduler) getRegionScheduler.invoke(ItemTag.get().getServer());
                if (VersionUtils.isVersionAfter(1, 16)) {
                    if (target.hasPotionEffect(effect.getType()))
                        target.removePotionEffect(effect.getType());
                    regionScheduler.execute(ItemTag.get(), target.getLocation(), () -> {
                        target.addPotionEffect(effect);
                    });
                } else
                    regionScheduler.execute(ItemTag.get(), target.getLocation(), () -> {
                        target.addPotionEffect(effect, true);
                    });
            }catch (Exception e) {
                ItemTag.get().log("Failed to get Folia RegionScheduler");
                return;
            }
            return;
        }
        if (VersionUtils.isVersionAfter(1, 16)) {
            if (target.hasPotionEffect(effect.getType()))
                target.removePotionEffect(effect.getType());
            target.addPotionEffect(effect);
        } else
            target.addPotionEffect(effect, true);
    }

    @EventHandler(ignoreCancelled = true)
    private void event(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET)
            SchedulerUtils.runTaskLater(event.getPlayer().getLocation(), () -> restoreEffects(event.getPlayer()), 1L);
    }

    public void restoreEffects(Player p) {
        if (!p.isOnline() || p.isDead())
            return;
        HashMap<PotionEffectType, PotionEffect> newEffects = new HashMap<>();
        for (EquipmentSlot slot : ItemTagUtility.getPlayerEquipmentSlots()) {
            Map<PotionEffectType, PotionEffect> newInfo = getPotionEffects(getEquip(p, slot), slot, true);
            newInfo.forEach((k, v) -> {
                if (getAmplifier(newEffects, k) < v.getAmplifier())
                    newEffects.put(k, v);
            });
        }
        newEffects.forEach((k, v) -> addEffect(p, k, v));
    }

    protected ItemStack getEquip(Player p, EquipmentSlot slot) {
        switch (slot) {
            case CHEST:
                return p.getEquipment().getChestplate();
            case FEET:
                return p.getEquipment().getBoots();
            case HAND:
                return getItemInHand(p);
            case HEAD:
                return p.getEquipment().getHelmet();
            case LEGS:
                return p.getEquipment().getLeggings();
        }// safe
        if (VersionUtils.isVersionAfter(1, 9) && slot == EquipmentSlot.OFF_HAND)
            return p.getInventory().getItemInOffHand();
        return null;
    }

}
