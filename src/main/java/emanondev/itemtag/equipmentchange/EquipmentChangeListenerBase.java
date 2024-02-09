package emanondev.itemtag.equipmentchange;

import emanondev.itemedit.ItemEdit;
import emanondev.itemtag.ItemTag;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class EquipmentChangeListenerBase implements Listener {

    protected final HashMap<Player, EnumMap<EquipmentSlot, ItemStack>> equips = new HashMap<>();
    protected final HashSet<Player> clickDrop = new HashSet<>();
    private final boolean is1_8 = ItemEdit.GAME_VERSION < 9;
    private int maxCheckedPlayerPerTick = 5;
    //private final boolean is1_10orLower = ItemEdit.GAME_VERSION < 11;
    private TimerCheckTask timerTask = null;

    public void reload() {
        if (timerTask != null)
            timerTask.cancel();
        long timerCheckFrequencyTicks = Math.max(5, ItemTag.get().getConfig().getInteger("equipment_change.frequency_ticks", 10));
        maxCheckedPlayerPerTick = Math.max(1, ItemTag.get().getConfig().getInteger("equipment_change.max_checked_players_per_tick", 5));
        for (Player p : equips.keySet())
            untrackPlayer(p);

        equips.clear();
        for (Player p : Bukkit.getOnlinePlayers())
            trackPlayer(p);

        timerTask = new TimerCheckTask();
        timerTask.runTaskTimer(ItemTag.get(), timerCheckFrequencyTicks, timerCheckFrequencyTicks);

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void event(PlayerJoinEvent event) {
        trackPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void event(PlayerQuitEvent event) {
        untrackPlayer(event.getPlayer());
    }

    @EventHandler
    private void event(PlayerDeathEvent event) {
        if (event.getEntity().hasMetadata("NPC") || event.getEntity().hasMetadata("BOT"))
            return;
        for (EquipmentSlot type : EquipmentSlot.values()) {
            ItemStack item = getEquip(event.getEntity(), type);
            if (!isAirOrNull(item))
                onEquipChange(event.getEntity(), EquipmentChangeEvent.EquipMethod.DEATH, type, item, null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerTeleportEvent event) {
        if (event.getPlayer().hasMetadata("NPC") || event.getPlayer().hasMetadata("BOT"))
            return;
        if (!equips.containsKey(event.getPlayer()))
            return; // some plugins teleport players just after login, before join this listener
        new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.PLUGIN_WORLD_CHANGE, EquipmentSlot.values())
                .runTaskLater(ItemTag.get(), 1L);
    }

    @EventHandler
    private void event(PlayerRespawnEvent event) {
        if (event.getPlayer().hasMetadata("NPC") || event.getPlayer().hasMetadata("BOT"))
            return;
        for (EquipmentSlot type : EquipmentSlot.values()) {
            ItemStack item = getEquip(event.getPlayer(), type);
            if (!isAirOrNull(item))
                onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.RESPAWN, type, null, item);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void event(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player))
            return;
        if (event.getWhoClicked().hasMetadata("NPC") || event.getWhoClicked().hasMetadata("BOT"))
            return;
        Player p = (Player) event.getWhoClicked();
        for (EquipmentSlot type : EquipmentSlot.values()) {
            int pos = getSlotPosition(type, p, event.getView());
            if (pos != -1 && event.getNewItems().containsKey(pos)) {
                ItemStack itemOld = event.getView().getItem(pos);
                ItemStack itemNew = event.getNewItems().get(pos);
                if (!isSimilarIgnoreDamage(itemOld, itemNew))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_DRAG, type, itemOld, itemNew);
            }
        }
    }

    @EventHandler
    private void event(PlayerItemBreakEvent e) {
        if (e.getPlayer().hasMetadata("NPC") || e.getPlayer().hasMetadata("BOT"))
            return;
        ArrayList<EquipmentSlot> slots = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (e.getBrokenItem().equals(getEquip(e.getPlayer(), slot)))
                slots.add(slot);
        }
        if (slots.size() == 0)
            throw new IllegalStateException();
        if (slots.size() == 1) {
            onEquipChange(e.getPlayer(), EquipmentChangeEvent.EquipMethod.BROKE, slots.get(0), e.getBrokenItem(), null);
            return;
        }
        new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.BROKE, slots).runTaskLater(ItemTag.get(), 1L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().hasMetadata("NPC") || event.getPlayer().hasMetadata("BOT"))
            return;
        if (isSimilarIgnoreDamage(event.getArmorStandItem(), event.getPlayerItem()))
            return;
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.ARMOR_STAND_MANIPULATE, EquipmentSlot.HAND, event.getPlayerItem(),
                event.getArmorStandItem());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void event(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata("NPC") || event.getPlayer().hasMetadata("BOT"))
            return;
        if (clickDrop.remove(event.getPlayer()))
            return;
        if (event.isCancelled())
            return;
        if (isAirOrNull(getEquip(event.getPlayer(), EquipmentSlot.HAND)))
            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.DROP, EquipmentSlot.HAND, event.getItemDrop().getItemStack(),
                    null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerInteractEntityEvent event) {
        if (event.getPlayer().hasMetadata("NPC") || event.getPlayer().hasMetadata("BOT"))
            return;
        EquipmentSlot slot;
        if (!is1_8)// safe
            slot = event.getHand();
        else
            slot = EquipmentSlot.HAND;
        ItemStack handItem;
        handItem = getEquip(event.getPlayer(), slot);

        if (isAirOrNull(handItem) || handItem.getAmount() > 1
                || (event.getRightClicked().getType() == EntityType.ARMOR_STAND
                && handItem.getType() != Material.NAME_TAG))
            return;
        if (handItem.getType() == Material.NAME_TAG) {
            if (!(event.getRightClicked() instanceof LivingEntity) || (event.getRightClicked() instanceof Player))
                return;
            if (!handItem.hasItemMeta() || !handItem.getItemMeta().hasDisplayName())
                return;

            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.NAMETAG_APPLY, slot, handItem, null);
            return;
        }

        if (event.getRightClicked() instanceof Sheep) {
            Sheep sheep = (Sheep) event.getRightClicked();
            if (sheep.isSheared())
                return;
            if (!handItem.getType().name().endsWith("_DYE"))
                return;
            if (handItem.getType().name().equals(sheep.getColor().name() + "_DYE"))
                return;
            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.SHEEP_COLOR, slot, handItem, null);
            return;
        }
        new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.USE_ON_ENTITY, slot).runTaskLater(ItemTag.get(), 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR) // compability -> !=priority
    private void event(PlayerInteractEvent e) {
        if (isAirOrNull(e.getItem()))
            return;
        if (e.useItemInHand() == Event.Result.DENY)
            return;
        if (e.getPlayer().hasMetadata("NPC") || e.getPlayer().hasMetadata("BOT"))
            return;
        EquipmentSlot slot;
        if (!is1_8)// safe
            slot = e.getHand();
        else
            slot = EquipmentSlot.HAND;
        EquipmentSlot type = guessRightClickSlotType(e.getItem());
        switch (e.getAction()) {
            case RIGHT_CLICK_AIR:
                if (type != null && isAirOrNull(getEquip(e.getPlayer(), type))) {
                    onEquipChange(e.getPlayer(), EquipmentChangeEvent.EquipMethod.RIGHT_CLICK, type, null, e.getItem());
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
                        onEquipChange(e.getPlayer(), EquipmentChangeEvent.EquipMethod.RIGHT_CLICK, slot, e.getItem(), null);
                } else if (e.getItem().getAmount() == 1)
                    new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.USE, slot).runTaskLater(ItemTag.get(), 1L);
                return;
            case RIGHT_CLICK_BLOCK:
                if (e.useItemInHand() == Event.Result.DENY)
                    return;
                if (type != null && isAirOrNull(getEquip(e.getPlayer(), type))) {
                    new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.RIGHT_CLICK, slot, type).runTaskLater(ItemTag.get(), 1L);
                } else if (e.getItem().getAmount() == 1)
                    new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.USE, slot).runTaskLater(ItemTag.get(), 1L);
            default:
                return;
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerItemConsumeEvent event) {
        if (event.getPlayer().hasMetadata("NPC") || event.getPlayer().hasMetadata("BOT"))
            return;
        if (event.getItem().getAmount() != 1)
            return;
        List<EquipmentSlot> slots = new ArrayList<>(1);
        if (event.getItem().equals(getEquip(event.getPlayer(), EquipmentSlot.HAND)))
            slots.add(EquipmentSlot.HAND);
        // safe
        if (!is1_8 && event.getItem().equals(getEquip(event.getPlayer(), EquipmentSlot.OFF_HAND)))
            slots.add(EquipmentSlot.OFF_HAND);
        if (slots.size() == 1)
            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.CONSUME, slots.get(0), event.getItem(),
                    event.getItem().getType() == Material.MILK_BUCKET ? new ItemStack(Material.BUCKET) : null);
        else if (slots.size() > 1)
            new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.CONSUME, slots).runTaskLater(ItemTag.get(), 1L);
        else // 3rd party plugin
            if (!is1_8)// safe
                new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.CONSUME,
                        Arrays.asList(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)).runTaskLater(ItemTag.get(), 1L);
    }

    @EventHandler
    private void event(PlayerItemHeldEvent event) {
        if (event.getPlayer().hasMetadata("NPC") || event.getPlayer().hasMetadata("BOT"))
            return;
        ItemStack i1 = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack i2 = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (i1 == i2)
            return;
        if (i1 != null && i1.isSimilar(i2))
            return;
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.HOTBAR_HAND_CHANGE, EquipmentSlot.HAND, i1, i2);
    }

    /**
     * A utility method to support versions that use null or air ItemStacks.
     */
    public boolean isAirOrNull(ItemStack item) {
        return item == null || item.getType().equals(Material.AIR);
    }

    public abstract boolean isSimilarIgnoreDamage(ItemStack item, ItemStack item2);

    @SuppressWarnings({"incomplete-switch", "deprecation"})
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

    public void onEquipChange(Player p, EquipmentChangeEvent.EquipMethod reason, EquipmentSlot type, ItemStack oldItem,
                              ItemStack newItem) {
        equips.get(p).put(type, isAirOrNull(newItem) ? null : new ItemStack(newItem));
        Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(p, reason, type, oldItem, newItem));
    }

    protected EquipmentSlot guessRightClickSlotType(ItemStack item) {
        if (isAirOrNull(item))
            return null;
        String type = item.getType().name();
        if (type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD"))
            return EquipmentSlot.HEAD;
        else if (type.endsWith("_CHESTPLATE") || type.equals("ELYTRA"))
            return EquipmentSlot.CHEST;
        else if (type.endsWith("_LEGGINGS"))
            return EquipmentSlot.LEGS;
        else if (type.endsWith("_BOOTS"))
            return EquipmentSlot.FEET;
        else
            return null;
    }

    protected EquipmentSlot guessDispenserSlotType(ItemStack item) {
        EquipmentSlot slot = guessRightClickSlotType(item);
        if (slot == null && item != null) {
            if (item.getType().name().endsWith("PUMPKIN"))
                return EquipmentSlot.HEAD;
            else if (!is1_8 && item.getType() == Material.SHIELD)// safe
                return EquipmentSlot.OFF_HAND;
        }
        return slot;
    }

    @SuppressWarnings("incomplete-switch")
    protected int getSlotPosition(EquipmentSlot slot, Player p, InventoryView view) {
        if (view.getTopInventory().getType() == InventoryType.CRAFTING) {
            switch (slot) {
                case HAND:
                    return p.getInventory().getHeldItemSlot() + 36;
                case HEAD:
                    return 5;
                case CHEST:
                    return 6;
                case LEGS:
                    return 7;
                case FEET:
                    return 8;
            }
            if (!is1_8 && slot == EquipmentSlot.OFF_HAND)// safe
                return 45;
            return -1;
        }
        if (slot == EquipmentSlot.HAND)
            return p.getInventory().getHeldItemSlot() + view.getTopInventory().getSize() + 27;

        return -1;
    }

    protected EquipmentSlot getEquipmentSlotAtPosition(int pos, Player p, InventoryView view) {
        if (view.getTopInventory().getType() == InventoryType.CRAFTING)
            switch (pos) {
                case 5:
                    return EquipmentSlot.HEAD;
                case 6:
                    return EquipmentSlot.CHEST;
                case 7:
                    return EquipmentSlot.LEGS;
                case 8:
                    return EquipmentSlot.FEET;
                case 45:
                    if (!is1_8)// safe
                        return EquipmentSlot.OFF_HAND;
                default:
                    return p.getInventory().getHeldItemSlot() + 36 == pos ? EquipmentSlot.HAND : null;
            }
        return pos == p.getInventory().getHeldItemSlot() + view.getTopInventory().getSize() + 27 ? EquipmentSlot.HAND
                : null;
    }

    public boolean trackPlayer(Player p) {
        if (equips.containsKey(p))
            return false;
        EnumMap<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
        equips.put(p, map);
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            map.put(slot, null);
            onEquipChange(p, EquipmentChangeEvent.EquipMethod.JOIN, slot, null, getEquip(p, slot));
        }
        return true;
    }

    public boolean untrackPlayer(Player p) {
        if (!equips.containsKey(p))
            return false;
        EnumMap<EquipmentSlot, ItemStack> map = equips.get(p);
        for (EquipmentSlot slot : map.keySet())
            onEquipChange(p, EquipmentChangeEvent.EquipMethod.QUIT, slot, map.get(slot), null);
        equips.remove(p);
        return true;
    }

    private class TimerCheckTask extends BukkitRunnable {

        private TimerCheckTask.PlayerCheck subTask = null;

        @Override
        public void run() {
            if (subTask == null)// || subTask.isCancelled())
                if (Bukkit.getOnlinePlayers().size() > 0) {
                    subTask = new TimerCheckTask.PlayerCheck();
                    subTask.runTaskTimer(ItemTag.get(), 1L, 1L);
                }
        }

        public void cancel() {
            super.cancel();
            if (subTask != null) {// && !subTask.isCancelled())
                subTask.cancel();
                subTask = null;
            }
        }

        private class PlayerCheck extends BukkitRunnable {
            private final List<Player> players = new ArrayList<>();
            private int index = 0;

            private PlayerCheck() {
                players.addAll(Bukkit.getOnlinePlayers());
            }

            @Override
            public void run() {
                int counter = 0;
                while (counter < maxCheckedPlayerPerTick) {
                    if (index >= players.size()) {
                        this.cancel();
                        if (subTask != null)
                            subTask = null;
                        return;
                    }
                    Player p = players.get(index);
                    index++;
                    if (!p.isOnline())
                        continue;
                    if (p.hasMetadata("BOT"))
                        continue;
                    trackPlayer(p);
                    counter++;
                    for (EquipmentSlot slot : EquipmentSlot.values()) {
                        ItemStack newItem = getEquip(p, slot);
                        ItemStack oldItem = equips.get(p).get(slot);
                        if (!isSimilarIgnoreDamage(oldItem, newItem))
                            onEquipChange(p, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, oldItem, newItem);
                    }

                }

            }

        }

    }

    protected class SlotCheck extends BukkitRunnable {

        private final EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
        private final Player p;
        private final EquipmentChangeEvent.EquipMethod method;

        public SlotCheck(Player p, EquipmentChangeEvent.EquipMethod method, EquipmentSlot... slots) {
            if (slots == null || slots.length == 0 || p == null || method == null)
                throw new IllegalArgumentException();
            this.p = p;
            this.method = method;
            if (trackPlayer(p))
                new IllegalStateException().printStackTrace();
            for (EquipmentSlot slot : slots) {
                if (!isSimilarIgnoreDamage(equips.get(p).get(slot), getEquip(p, slot)))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), getEquip(p, slot));
                this.slots.add(slot);
            }
        }

        public SlotCheck(Player p, EquipmentChangeEvent.EquipMethod method, Collection<EquipmentSlot> slots) {
            if (slots == null || slots.size() == 0 || p == null || method == null)
                throw new IllegalArgumentException();
            this.p = p;
            this.method = method;
            if (trackPlayer(p))
                new IllegalStateException().printStackTrace();
            for (EquipmentSlot slot : slots) {
                ItemStack equip = getEquip(p, slot);
                if (isAirOrNull(equip))
                    equip = null;
                else
                    equip = new ItemStack(equip);
                if (!isSimilarIgnoreDamage(equips.get(p).get(slot), equip))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), equip);
                this.slots.add(slot);
            }
        }

        @Override
        public void run() {
            if (!p.isOnline())
                return;
            if (trackPlayer(p))
                new IllegalStateException().printStackTrace();
            for (EquipmentSlot slot : slots) {
                ItemStack item = getEquip(p, slot);
                if (isAirOrNull(item))
                    item = null;
                if (isSimilarIgnoreDamage(item, equips.get(p).get(slot))) {
                    continue;
                }
                onEquipChange(p, method, slot, equips.get(p).get(slot), item);
            }
        }

    }
}
