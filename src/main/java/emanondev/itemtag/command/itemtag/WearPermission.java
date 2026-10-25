package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.CooldownAPI;
import emanondev.itemedit.Util;
import emanondev.itemedit.UtilsString;
import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemedit.utility.InventoryUtils;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemedit.utility.SchedulerUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.command.ListenerSubCmd;
import emanondev.itemtag.equipmentchange.EquipmentChangeEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
        ItemStack item = this.getItemInMainHand(p);
        TagItem tagItem = ItemTag.getTagItem(item);
        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "setpermission" -> {
                if (args.length > 3) {
                    onFail(p, alias);
                    return;
                }
                String permission = args.length == 2 ? null : args[2].toLowerCase(Locale.ENGLISH);
                setUseKey(tagItem, permission);
                Component feedback;
                if (permission != null) {
                    feedback = translate("setpermission.feedback", p, "%value%", permission);
                } else {
                    feedback = translate("setpermission.feedback-reset", p);
                }
                Util.sendComponent(p, feedback);
            }
            case "setmessage" -> {
                if (args.length == 2) {
                    setUseMsgKey(tagItem, null);
                    Component feedback = translate("setmessage.feedback-reset", p);
                    Util.sendComponent(p, feedback);
                    return;
                }
                String msg = UtilsString.fixToString(String.join(" ", Arrays.asList(args).subList(2, args.length)), null, true);
                setUseMsgKey(tagItem, msg);
                Component feedback = translate("setmessage.feedback", p, "%value%", msg);
                Util.sendComponent(p, feedback);
            }
        }


    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        if (args.length == 2)
            return CompleteUtility.complete(args[1], "setpermission", "setmessage");
        return List.of();
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
        EquipmentSlot slot = event.getSlotType();

        SchedulerUtils.runLater(getPlugin(), player, 1L, () -> {
            if (!player.isOnline())
                return;
            ItemStack originalItem = player.getInventory().getItem(slot);
            if (ItemUtils.isAirOrNull(originalItem)) {
                return;
            }
            ItemStack item = originalItem.clone();
            if (canWear(player, originalItem)) {
                return;
            }
            player.getInventory().setItem(slot, null);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1F, 1F);
            InventoryUtils.giveAmount(player, item, item.getAmount(), InventoryUtils.ExcessMode.DROP_EXCESS);
            ItemTag.get().getEquipChangeListener().onEquipChange(
                    player, EquipmentChangeEvent.EquipMethod.UNKNOWN, slot, item, null);
            TagItem tagItem = ItemTag.getTagItem(item);
            String perm = tagItem.getString(WEAR_KEY);
            if (tagItem.hasStringTag(WEARMSG_KEY)) {
                CooldownAPI api = ItemTag.get().getCooldownAPI();
                String cooldownKey = "wear_" + perm.replace(".", "_");
                if (api.hasCooldown(player, cooldownKey)) {
                    return;
                }
                api.setCooldown(player, cooldownKey, 1, TimeUnit.SECONDS);
                Util.sendComponent(player, UtilsString.fix(tagItem.getString(WEARMSG_KEY), player, true, "%permission%", perm));
            }
        });

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
        return player.hasPermission(perm);
    }
}
