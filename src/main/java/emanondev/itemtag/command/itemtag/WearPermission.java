package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.CooldownAPI;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.command.ListenerSubCmd;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class WearPermission extends ListenerSubCmd {

    private final static String WEAR_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":wearperm";
    private final static String WEARMSG_KEY = ItemTag.get().getName().toLowerCase(Locale.ENGLISH) + ":wearpermmsg";

    public WearPermission(AbstractCommand cmd) {
        super("wearpermission", cmd, true, true);
    }

    public static void setUseKey(@NotNull TagItem item, @Nullable String value) {
        if (value != null && !value.isEmpty()) {//default
            item.setTag(WEAR_KEY, value);
        } else {
            item.removeTag(WEAR_KEY);
        }
    }

    public static void setUseMsgKey(@NotNull TagItem item, @Nullable String value) {
        if (value != null && !value.isEmpty()) { //default
            item.setTag(WEARMSG_KEY, value);
        } else {
            item.removeTag(WEARMSG_KEY);
        }
    }

    /*
        it wearperm setpermission ...
        it wearperm setmessage ...
     */
    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String alias, String[] args) {
        Player p = (Player) sender;
        if (args.length == 1) {
            onFail(p, alias);
            return;
        }
        ItemStack item = this.getItemInHand(p);
        TagItem tagItem = ItemTag.getTagItem(item);
        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "setpermission": {
                if (args.length > 3) {
                    onFail(p, alias);
                    //TODO args amount
                    return;
                }
                String permission = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
                setUseKey(tagItem, permission);
                //TODO feedback
                return;
            }
            case "setmessage": {
                if (args.length == 2) {
                    //TODO feedback
                    setUseMsgKey(tagItem, null);
                    return;
                }
                String msg = UtilsString.fix(String.join(" ", Arrays.asList(args).subList(2, args.length)), null, true);
                setUseMsgKey(tagItem, msg);
                //TODO feedback
                return;
            }
        }


    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], "setpermission", "setmessage");
        return Collections.emptyList();
    }

    @EventHandler
    public void event(final EquipmentChangeEvent event) {
        if (Objects.requireNonNull(event.getSlotType()) == EquipmentSlot.HAND) {
            return;
        }
        Player player = event.getPlayer();
        if (canWear(player, event.getTo())) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
            if (!player.isOnline())
                return;
            ItemStack originalItem = InventoryUtils.getItem(player, event.getSlotType());
            if (ItemUtils.isAirOrNull(originalItem)) {
                return;
            }
            ItemStack item = originalItem.clone();
            if (canWear(player, originalItem)) {
                return;
            }
            player.getInventory().setItem(event.getSlotType(), null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            InventoryUtils.giveAmount(player, item, item.getAmount(), InventoryUtils.ExcessMode.DROP_EXCESS);
            ItemTag.get().getEquipChangeListener().onEquipChange(player, EquipmentChangeEvent.EquipMethod.UNKNOWN
                    , event.getSlotType(), item, null);
            TagItem tagItem = ItemTag.getTagItem(item);
            String perm = tagItem.getString(WEAR_KEY);
            if (tagItem.hasStringTag(WEARMSG_KEY)) {
                CooldownAPI api = ItemTag.get().getCooldownAPI();
                String cooldownKey = "wear_" + perm.replace(".", "_");
                if (api.hasCooldown(player,cooldownKey)){
                    return;
                }
                api.setCooldown(player,cooldownKey,1, TimeUnit.SECONDS);
                Util.sendMessage(player, UtilsString.fix(tagItem.getString(WEARMSG_KEY), player, true, "%permission%", perm));
            }
        }, 1L);

    }

    private boolean canWear(Player player, ItemStack itemStack) {
        if (ItemUtils.isAirOrNull(itemStack)) {
            return true;
        }
        TagItem tagItem = ItemTag.getTagItem(itemStack);
        if (!tagItem.hasStringTag(WEAR_KEY)) {
            return true;
        }
        String perm = tagItem.getString(WEAR_KEY);
        if (player.hasPermission(perm)) {
            return true;
        }
        return false;
    }
}
