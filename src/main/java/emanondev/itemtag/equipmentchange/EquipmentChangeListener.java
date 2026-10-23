package emanondev.itemtag.equipmentchange;

import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.SchedulerUtils;
import emanondev.itemedit.utility.VersionUtils;
import emanondev.itemtag.ItemTag;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import lombok.extern.slf4j.Slf4j;
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
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EquipmentChangeListener implements Listener {

    protected final Map<Player, EnumMap<EquipmentSlot, ItemStack>> equips = new ConcurrentHashMap<>();
    protected final Set<Player> clickDrop = ConcurrentHashMap.newKeySet();
    private int maxCheckedPlayerPerTick = 5;
    private long timerCheckFrequencyTicks = 10L;

    private ScheduledTask timerTask;
    private ScheduledTask playerCheckTask;

    public void reload() {
        SchedulerUtils.run(ItemTag.get(), this::reloadOnGlobalRegion);
    }

    public void onEquipChange(Player p,
                              EquipmentChangeEvent.EquipMethod reason,
                              @NotNull EquipmentSlot type,
                              ItemStack oldItem,
                              ItemStack newItem) {
        equips.get(p).put(type, ItemUtils.isAirOrNull(newItem) ? null : newItem.clone());
        Bukkit.getPluginManager().callEvent(new EquipmentChangeEvent(p, reason, type, oldItem, newItem));
    }

    public boolean trackPlayer(Player p) {
        if (equips.containsKey(p))
            return false;
        EnumMap<EquipmentSlot, ItemStack> map = new EnumMap<>(EquipmentSlot.class);
        equips.put(p, map);
        for (EquipmentSlot slot : InventoryUtils.getPlayerEquipmentSlots()) {
            map.put(slot, null);
            onEquipChange(p, EquipmentChangeEvent.EquipMethod.JOIN, slot, null, getEquip(p, slot));
        }
        return true;
    }

    public boolean untrackPlayer(Player p) {
        if (!equips.containsKey(p))
            return false;
        EnumMap<EquipmentSlot, ItemStack> map = equips.get(p);
        for (EquipmentSlot slot : map.keySet()) {
            onEquipChange(p, EquipmentChangeEvent.EquipMethod.QUIT, slot, map.get(slot), null);
        }
        equips.remove(p);
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void event(InventoryClickEvent event) {
        ItemTag.get().log(event.getAction().name());
        if (!(event.getWhoClicked() instanceof Player p))
            return;
        if (event.getWhoClicked().hasMetadata("NPC"))
            return;
        EquipmentSlot clickedSlot = getEquipmentSlotAtPosition(event.getRawSlot(), p, event.getView());
        switch (event.getAction()) {
            case CLONE_STACK, NOTHING -> {
                return;
            }
            case DROP_ONE_CURSOR, DROP_ALL_CURSOR -> clickDrop.add(p);
            case DROP_ONE_SLOT -> {
                if (!ItemUtils.isAirOrNull(event.getCursor()))
                    return;
                if (clickedSlot != null && event.getCurrentItem().getAmount() == 1)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_DROP, clickedSlot, event.getCurrentItem(), null);
                clickDrop.add(p);
            }
            case DROP_ALL_SLOT -> {
                if (!ItemUtils.isAirOrNull(event.getCursor()))
                    return;
                if (clickedSlot != null)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_DROP, clickedSlot, event.getCurrentItem(), null);
                clickDrop.add(p);
            }
            case PICKUP_ALL -> {
                if (clickedSlot == null)
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
            }
            case PICKUP_HALF -> {
                if (clickedSlot == null || event.getCurrentItem().getAmount() > 1)
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
            }
            case PICKUP_ONE -> {
                if (clickedSlot == null || event.getCurrentItem().getAmount() != 1)
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PICKUP, clickedSlot, event.getCurrentItem(), null);
            }
            case PLACE_ALL, PLACE_ONE, PLACE_SOME -> {
                if (clickedSlot == null)
                    return;
                if (ItemUtils.isAirOrNull(event.getCurrentItem()))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_PLACE, clickedSlot, null, event.getCursor());
            }
            case SWAP_WITH_CURSOR -> {
                if (clickedSlot == null)
                    return;
                if (isSimilarIgnoreDamage(event.getCurrentItem(), event.getCursor()))
                    return;
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_SWAP_WITH_CURSOR, clickedSlot, event.getCurrentItem(),
                        event.getCursor());
            }
            case HOTBAR_SWAP -> {
                ItemStack to = event.getHotbarButton() == -1 ? p.getInventory().getItemInOffHand() : p.getInventory().getItem(event.getHotbarButton());
                if (isSimilarIgnoreDamage(event.getCurrentItem(), to))
                    return;
                if (clickedSlot != null)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_HOTBAR_SWAP, clickedSlot, event.getCurrentItem(), to);
                if (event.getHotbarButton() == -1)
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_HOTBAR_SWAP, EquipmentSlot.OFF_HAND, to, event.getCurrentItem());
                else if (event.getHotbarButton() == p.getInventory().getHeldItemSlot())
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_HOTBAR_SWAP, EquipmentSlot.HAND, to, event.getCurrentItem());
            }
            case MOVE_TO_OTHER_INVENTORY -> {
                EquipmentSlot slot = event.getView().getTopInventory().getType() == InventoryType.CRAFTING
                        ? guessDispenserSlotType(event.getCurrentItem())
                        : null;
                if (slot != null && ItemUtils.isAirOrNull(getEquip(p, slot)))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, slot, null, event.getCurrentItem());
                if (clickedSlot == null || clickedSlot == EquipmentSlot.HAND)
                    scheduleSlotCheck(new SlotCheck(p, EquipmentChangeEvent.EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, EquipmentSlot.HAND));
                else
                    scheduleSlotCheck(new SlotCheck(p, EquipmentChangeEvent.EquipMethod.INVENTORY_MOVE_TO_OTHER_INVENTORY, clickedSlot));
            }
            case COLLECT_TO_CURSOR -> {
                ArrayList<EquipmentSlot> slots = new ArrayList<>();
                if (event.getView().getTopInventory().getType() == InventoryType.CRAFTING)
                    for (EquipmentSlot slot : InventoryUtils.getPlayerEquipmentSlots()) {
                        if (event.getCursor().isSimilar(getEquip(p, slot)))
                            slots.add(slot);
                    }
                else if (event.getCursor().isSimilar(getEquip(p, EquipmentSlot.HAND)))
                    slots.add(EquipmentSlot.HAND);
                if (!slots.isEmpty())
                    scheduleSlotCheck(new SlotCheck(p, EquipmentChangeEvent.EquipMethod.INVENTORY_COLLECT_TO_CURSOR, slots));
            }
            case PICKUP_SOME, UNKNOWN -> {
                return; // ???
            }
        }
    }

    public boolean isSimilarIgnoreDamage(ItemStack item, ItemStack item2) {
        if (ItemUtils.isAirOrNull(item))
            return ItemUtils.isAirOrNull(item2);
        if (ItemUtils.isAirOrNull(item2))
            return false;
        if (item.isSimilar(item2))
            return true;
        if (item.getType() != item2.getType())
            return false;
        ItemMeta meta1 = item.getItemMeta();
        if (!(meta1 instanceof Damageable))
            return false;
        ItemMeta meta2 = item2.getItemMeta();
        if (meta1.isUnbreakable() || meta2.isUnbreakable())
            return false;
        ((Damageable) meta2).setDamage(((Damageable) meta1).getDamage());
        return meta1.equals(meta2);
    }

    protected ItemStack getEquip(Player p, EquipmentSlot slot) {
        return p.getEquipment().getItem(slot);
    }

    protected EquipmentSlot guessRightClickSlotType(ItemStack item) {
        if (ItemUtils.isAirOrNull(item))
            return null;

        if (VersionUtils.isAfter(1, 21, 5)) {
            ItemMeta meta = ItemUtils.getMeta(item);
            if (meta.hasEquippable()) {
                return meta.getEquippable().getSlot();
            }
        }
        String type = item.getType().name();
        if (type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD"))
            return EquipmentSlot.HEAD;
        if (type.endsWith("_CHESTPLATE") || type.equals("ELYTRA"))
            return EquipmentSlot.CHEST;
        if (type.endsWith("_LEGGINGS"))
            return EquipmentSlot.LEGS;
        if (type.endsWith("_BOOTS"))
            return EquipmentSlot.FEET;
        return null;
    }

    protected EquipmentSlot guessDispenserSlotType(ItemStack item) {
        EquipmentSlot slot = guessRightClickSlotType(item);

        if (VersionUtils.isAfter(1, 21, 5)) {
            ItemMeta meta = ItemUtils.getMeta(item);
            if (meta.hasEquippable()) {
                return meta.getEquippable().getSlot();
            }
        }

        if (slot == null && item != null) {
            if (item.getType().name().endsWith("PUMPKIN"))
                return EquipmentSlot.HEAD;
            if (item.getType() == Material.SHIELD)// safe
                return EquipmentSlot.OFF_HAND;
        }
        return slot;
    }

    protected int getSlotPosition(EquipmentSlot slot, Player p, InventoryView view) {
        if (view.getTopInventory().getType() == InventoryType.CRAFTING) {
            return switch (slot) {
                case HAND -> p.getInventory().getHeldItemSlot() + 36;
                case HEAD -> 5;
                case CHEST -> 6;
                case LEGS -> 7;
                case FEET -> 8;
                case OFF_HAND -> 45;
                default -> -1;
            };
        }
        if (slot == EquipmentSlot.HAND)
            return p.getInventory().getHeldItemSlot() + view.getTopInventory().getSize() + 27;

        return -1;
    }

    protected EquipmentSlot getEquipmentSlotAtPosition(int pos, Player p, InventoryView view) {
        if (view.getTopInventory().getType() == InventoryType.CRAFTING)
            return switch (pos) {
                case 5 -> EquipmentSlot.HEAD;
                case 6 -> EquipmentSlot.CHEST;
                case 7 -> EquipmentSlot.LEGS;
                case 8 -> EquipmentSlot.FEET;
                case 45 -> EquipmentSlot.OFF_HAND;
                default -> p.getInventory().getHeldItemSlot() + 36 == pos ? EquipmentSlot.HAND : null;
            };
        return pos == p.getInventory().getHeldItemSlot() + view.getTopInventory().getSize() + 27 ? EquipmentSlot.HAND
                : null;
    }

    private void reloadOnGlobalRegion() {
        if (timerTask != null) {
            timerTask.cancel();
        }
        if (playerCheckTask != null) {
            playerCheckTask.cancel();
        }
        playerCheckTask = null;

        timerCheckFrequencyTicks = Math.max(5, ItemTag.get().getConfig()
                .getInteger("equipment_change.frequency_ticks", 10));
        maxCheckedPlayerPerTick = Math.max(1, ItemTag.get().getConfig()
                .getInteger("equipment_change.max_checked_players_per_tick", 5));

        Set<Player> players = new HashSet<>(equips.keySet());
        players.addAll(Bukkit.getOnlinePlayers());
        for (Player p : players) {
            SchedulerUtils.run(ItemTag.get(), p, () -> {
                if (equips.containsKey(p)) {
                    untrackPlayer(p);
                }
                if (p.isOnline()) {
                    trackPlayer(p);
                }
            });
        }

        timerTask = ItemTag.get().getServer().getGlobalRegionScheduler().runAtFixedRate(
                ItemTag.get(), task -> {
                    if (playerCheckTask == null) {
                        startPlayerCheck();
                    }
                }, timerCheckFrequencyTicks, timerCheckFrequencyTicks);
    }

    private void startPlayerCheck() {
        PlayerCheckTask playerCheck = new PlayerCheckTask();
        playerCheckTask = ItemTag.get().getServer().getGlobalRegionScheduler().runAtFixedRate(
                ItemTag.get(), playerCheck::run, 1L, 1L);
    }

    private void checkPlayer(Player p) {
        if (!p.isOnline() || p.hasMetadata("NPC")) {
            return;
        }
        trackPlayer(p);
        EnumMap<EquipmentSlot, ItemStack> playerEquips = equips.get(p);
        if (playerEquips == null) {
            return;
        }
        for (EquipmentSlot slot : InventoryUtils.getPlayerEquipmentSlots()) {
            ItemStack newItem = getEquip(p, slot);
            ItemStack oldItem = playerEquips.get(slot);
            if (!isSimilarIgnoreDamage(oldItem, newItem)) {
                onEquipChange(p, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, oldItem, newItem);
            }
        }
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
        if (event.getEntity().hasMetadata("NPC"))
            return;
        for (EquipmentSlot type : InventoryUtils.getPlayerEquipmentSlots()) {
            ItemStack item = getEquip(event.getEntity(), type);
            if (!ItemUtils.isAirOrNull(item))
                onEquipChange(event.getEntity(), EquipmentChangeEvent.EquipMethod.DEATH, type, item, null);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerTeleportEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        if (!equips.containsKey(event.getPlayer())) {
            return; // some plugins teleport players just after login, before join this listener
        }
        scheduleSlotCheck(new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.PLUGIN_WORLD_CHANGE,
                InventoryUtils.getPlayerEquipmentSlots()));
    }

    @EventHandler
    private void event(PlayerRespawnEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        for (EquipmentSlot type : InventoryUtils.getPlayerEquipmentSlots()) {
            ItemStack item = getEquip(event.getPlayer(), type);
            if (!ItemUtils.isAirOrNull(item))
                onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.RESPAWN, type, null, item);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void event(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player p))
            return;
        if (event.getWhoClicked().hasMetadata("NPC")) {
            return;
        }
        for (EquipmentSlot type : InventoryUtils.getPlayerEquipmentSlots()) {
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
        if (e.getPlayer().hasMetadata("NPC")) {
            return;
        }
        ArrayList<EquipmentSlot> slots = new ArrayList<>();
        for (EquipmentSlot slot : InventoryUtils.getPlayerEquipmentSlots()) {
            if (e.getBrokenItem().equals(getEquip(e.getPlayer(), slot))) {
                slots.add(slot);
            }
        }
        if (slots.isEmpty()) {
            throw new IllegalStateException();
        }
        if (slots.size() == 1) {
            onEquipChange(e.getPlayer(), EquipmentChangeEvent.EquipMethod.BROKE, slots.getFirst(), e.getBrokenItem(), null);
            return;
        }
        scheduleSlotCheck(new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.BROKE, slots));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerArmorStandManipulateEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        if (isSimilarIgnoreDamage(event.getArmorStandItem(), event.getPlayerItem())) {
            return;
        }
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.ARMOR_STAND_MANIPULATE, EquipmentSlot.HAND, event.getPlayerItem(),
                event.getArmorStandItem());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void event(PlayerDropItemEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        if (clickDrop.remove(event.getPlayer())) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (ItemUtils.isAirOrNull(getEquip(event.getPlayer(), EquipmentSlot.HAND)))
            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.DROP,
                    EquipmentSlot.HAND, event.getItemDrop().getItemStack(), null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerInteractEntityEvent event) {
        if (event.getPlayer().hasMetadata("NPC")) {
            return;
        }
        EquipmentSlot slot = event.getHand();
        ItemStack handItem;
        handItem = getEquip(event.getPlayer(), slot);

        if (ItemUtils.isAirOrNull(handItem) || handItem.getAmount() > 1
                || (event.getRightClicked().getType() == EntityType.ARMOR_STAND
                && handItem.getType() != Material.NAME_TAG)) {
            return;
        }
        if (handItem.getType() == Material.NAME_TAG) {
            if (!(event.getRightClicked() instanceof LivingEntity)
                    || (event.getRightClicked() instanceof Player)) {
                return;
            }
            if (!handItem.hasItemMeta() || !handItem.getItemMeta().hasDisplayName()) {
                return;
            }

            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.NAMETAG_APPLY, slot, handItem, null);
            return;
        }

        if (event.getRightClicked() instanceof Sheep sheep) {
            if (sheep.isSheared())
                return;
            if (!handItem.getType().name().endsWith("_DYE"))
                return;
            if (handItem.getType().name().equals(sheep.getColor().name() + "_DYE"))
                return;
            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.SHEEP_COLOR, slot, handItem, null);
            return;
        }
        scheduleSlotCheck(new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.USE_ON_ENTITY, slot));
    }

    @EventHandler(priority = EventPriority.MONITOR) // compability -> !=priority
    private void event(PlayerInteractEvent e) {
        if (ItemUtils.isAirOrNull(e.getItem()))
            return;
        if (e.useItemInHand() == Event.Result.DENY)
            return;
        if (e.getPlayer().hasMetadata("NPC"))
            return;
        EquipmentSlot slot = e.getHand();
        EquipmentSlot type = guessRightClickSlotType(e.getItem());
        switch (e.getAction()) {
            case RIGHT_CLICK_AIR:
                if (type != null && ItemUtils.isAirOrNull(getEquip(e.getPlayer(), type))) {
                    onEquipChange(e.getPlayer(), EquipmentChangeEvent.EquipMethod.RIGHT_CLICK, type, null, e.getItem());
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE)
                        onEquipChange(e.getPlayer(), EquipmentChangeEvent.EquipMethod.RIGHT_CLICK, slot, e.getItem(), null);
                } else if (e.getItem().getAmount() == 1)
                    scheduleSlotCheck(new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.USE, slot));
                return;
            case RIGHT_CLICK_BLOCK:
                if (e.useItemInHand() == Event.Result.DENY)
                    return;
                if (type != null && ItemUtils.isAirOrNull(getEquip(e.getPlayer(), type))) {
                    scheduleSlotCheck(new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.RIGHT_CLICK, slot, type));
                } else if (e.getItem().getAmount() == 1)
                    scheduleSlotCheck(new SlotCheck(e.getPlayer(), EquipmentChangeEvent.EquipMethod.USE, slot));
                return;
            default:
                return;
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerItemConsumeEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;
        if (event.getItem().getAmount() != 1)
            return;
        List<EquipmentSlot> slots = new ArrayList<>(1);
        if (event.getItem().equals(getEquip(event.getPlayer(), EquipmentSlot.HAND)))
            slots.add(EquipmentSlot.HAND);
        // safe
        if (event.getItem().equals(getEquip(event.getPlayer(), EquipmentSlot.OFF_HAND)))
            slots.add(EquipmentSlot.OFF_HAND);
        if (slots.size() == 1)
            onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.CONSUME, slots.getFirst(), event.getItem(),
                    event.getItem().getType() == Material.MILK_BUCKET ? new ItemStack(Material.BUCKET) : null);
        else if (slots.size() > 1)
            scheduleSlotCheck(new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.CONSUME, slots));
        else // 3rd party plugin
            scheduleSlotCheck(new SlotCheck(event.getPlayer(), EquipmentChangeEvent.EquipMethod.CONSUME,
                    Arrays.asList(EquipmentSlot.HAND, EquipmentSlot.OFF_HAND)));
    }

    @EventHandler
    private void event(PlayerItemHeldEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;
        ItemStack i1 = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        ItemStack i2 = event.getPlayer().getInventory().getItem(event.getNewSlot());
        if (i1 == i2)
            return;
        if (i1 != null && i1.isSimilar(i2))
            return;
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.HOTBAR_HAND_CHANGE, EquipmentSlot.HAND, i1, i2);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(PlayerSwapHandItemsEvent event) {
        if (event.getPlayer().hasMetadata("NPC"))
            return;
        if (isSimilarIgnoreDamage(event.getMainHandItem(), event.getOffHandItem()))
            return;
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.SWAP_HANDS_ITEM, EquipmentSlot.HAND, event.getOffHandItem(),
                event.getMainHandItem());
        onEquipChange(event.getPlayer(), EquipmentChangeEvent.EquipMethod.SWAP_HANDS_ITEM, EquipmentSlot.OFF_HAND, event.getMainHandItem(),
                event.getOffHandItem());
    }

    @EventHandler
    private void event(BlockDispenseArmorEvent event) {
        if (!(event.getTargetEntity() instanceof Player))
            return;
        if (event.getTargetEntity().hasMetadata("NPC"))
            return;
        EquipmentSlot slot = guessDispenserSlotType(event.getItem());
        if (slot == null)
            throw new IllegalStateException(event.getItem().toString());
        onEquipChange((Player) event.getTargetEntity(), EquipmentChangeEvent.EquipMethod.DISPENSER, slot, null, event.getItem());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    private void event(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player p))
            return;
        if (event.getEntity().hasMetadata("NPC"))
            return;

        if (!ItemUtils.isAirOrNull(getEquip(p, EquipmentSlot.HAND)))
            return;
        for (int i = 0; i < p.getInventory().getHeldItemSlot(); i++)
            if (ItemUtils.isAirOrNull(p.getInventory().getItem(i)))
                return;
        scheduleSlotCheck(new SlotCheck(p, EquipmentChangeEvent.EquipMethod.PICKUP, EquipmentSlot.HAND));
    }

    private void scheduleSlotCheck(SlotCheck slotCheck) {
        SchedulerUtils.runLater(ItemTag.get(), slotCheck.p, 1L, slotCheck::run);
    }

    private class PlayerCheckTask {
        private final List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        private int index;

        private void run(ScheduledTask task) {
            int counter = 0;
            while (counter < maxCheckedPlayerPerTick) {
                if (index >= players.size()) {
                    if (playerCheckTask == task) {
                        playerCheckTask = null;
                    }
                    task.cancel();
                    return;
                }
                Player p = players.get(index++);
                counter++;
                SchedulerUtils.run(ItemTag.get(), p, () -> checkPlayer(p));
            }
        }
    }

    protected class SlotCheck {

        private final EnumSet<EquipmentSlot> slots = EnumSet.noneOf(EquipmentSlot.class);
        private final Player p;
        private final EquipmentChangeEvent.EquipMethod method;

        public SlotCheck(Player p, EquipmentChangeEvent.EquipMethod method, EquipmentSlot... slots) {
            if (slots == null || slots.length == 0 || p == null || method == null)
                throw new IllegalArgumentException();
            this.p = p;
            this.method = method;
            if (trackPlayer(p))
                log.warn("Unexpected tracking state in EquipmentChangeListenerBase", new IllegalStateException());
            for (EquipmentSlot slot : slots) {
                if (!isSimilarIgnoreDamage(equips.get(p).get(slot), getEquip(p, slot)))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), getEquip(p, slot));
                this.slots.add(slot);
            }
        }

        public SlotCheck(Player p, EquipmentChangeEvent.EquipMethod method, Collection<EquipmentSlot> slots) {
            if (slots == null || slots.isEmpty() || p == null || method == null)
                throw new IllegalArgumentException();
            this.p = p;
            this.method = method;
            if (trackPlayer(p))
                log.warn("Unexpected tracking state in EquipmentChangeListenerBase", new IllegalStateException());
            for (EquipmentSlot slot : slots) {
                ItemStack equip = getEquip(p, slot);
                if (ItemUtils.isAirOrNull(equip))
                    equip = null;
                else
                    equip = new ItemStack(equip);
                if (!isSimilarIgnoreDamage(equips.get(p).get(slot), equip))
                    onEquipChange(p, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, equips.get(p).get(slot), equip);
                this.slots.add(slot);
            }
        }

        public void run() {
            if (!p.isOnline())
                return;
            if (trackPlayer(p))
                log.warn("Unexpected tracking state in EquipmentChangeListenerBase", new IllegalStateException());
            for (EquipmentSlot slot : slots) {
                ItemStack item = getEquip(p, slot);
                if (ItemUtils.isAirOrNull(item))
                    item = null;
                if (isSimilarIgnoreDamage(item, equips.get(p).get(slot))) {
                    continue;
                }
                onEquipChange(p, method, slot, equips.get(p).get(slot), item);
            }
        }

    }

}
