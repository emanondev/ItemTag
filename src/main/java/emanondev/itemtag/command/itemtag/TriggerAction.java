package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.activity.*;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.gui.TriggerGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;

public class TriggerAction extends SubCmd {

    public TriggerAction(@NotNull ItemTagCommand cmd) {
        super("trigger", cmd, true, true);
    }

    @Override
    public void onCommand(@NotNull CommandSender sender, @NotNull String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            player.openInventory(new TriggerGui(player, player.getItemInHand()).getInventory());
            return;
        }
        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "set":
                set(player, label, args);
                return;
            case "delete":
                delete(player, label, args);
                return;
            case "setuses":
                setuses(player, label, args);
                return;
            case "setallowedslots":
                setallowedslots(player, label, args);
                return;
            case "consumeatusesend":
                consumeatusesend(player, label, args);
                return;
            case "setmaxuses":
                maxuses(player, label, args);
                return;
            case "visualcooldown":
                visualcooldown(player, label, args);
                return;
            case "cooldownamount":
                cooldownamount(player, label, args);
                return;
            default:
                help(player, label, args);
        }
    }

    //trigger cooldownamount <trigger> <qt> [ms/s]
    private void cooldownamount(Player player, String label, String[] args) {
        if (args.length != 4 && args.length != 5) {
            //TODO params
            return;
        }
        TriggerType type = TriggerManager.getTriggerType(args[2]);
        if (type == null) {
            //TODO
            return;
        }
        boolean seconds = args.length == 5 && args[4].equalsIgnoreCase("s");
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (Exception e) {
            //TODO invalid number
            return;
        }
        amount = Math.max(0, amount * (seconds ? 1000 : 1));
        TriggerHandler.setCooldownAmountMs(type, ItemTag.getTagItem(getItemInHand(player)), amount);
        //TODO feedback
    }
/*
    private void cooldownid(Player player, String label, String[] args) {
    }*/


    //trigger visualcooldown <value>
    private void visualcooldown(Player player, String label, String[] args) {
        if (args.length != 3) {
            //TODO params
            return;
        }
        Boolean value = Aliases.BOOLEAN.convertAlias(args[2]);
        if (value == null) {
            //TODO
            return;
        }
        TriggerHandler.setVisualCooldown(ItemTag.getTagItem(getItemInHand(player)), value);
        //TODO feedback
    }

    //trigger maxuses <amount>
    private void maxuses(Player player, String label, String[] args) {
        if (args.length != 3) {
            //TODO params
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            //TODO invalid number
            return;
        }
        TriggerHandler.setMaxUses(ItemTag.getTagItem(getItemInHand(player)), amount);
        //TODO feedback
    }

    private void consumeatusesend(Player player, String label, String[] args) {
        if (args.length != 3) {
            //TODO params
            return;
        }
        Boolean value = Aliases.BOOLEAN.convertAlias(args[2]);
        if (value == null) {
            //TODO
            return;
        }
        TriggerHandler.setConsumeAtUsesEnd(ItemTag.getTagItem(getItemInHand(player)), value);
        //TODO feedback
    }

    //trigger setallowedslots <triggertype> [slot1] [slot2] etc...
    private void setallowedslots(Player player, String label, String[] args) {
        EnumSet<EquipmentSlot> set = EnumSet.noneOf(EquipmentSlot.class);

        TriggerType type = TriggerManager.getTriggerType(args[2]);
        if (type == null) {
            //TODO
            return;
        }
        for (int i = 3; i < args.length; i++) {
            EquipmentSlot value = Aliases.EQUIPMENT_SLOTS.convertAlias(args[i]);
            if (value == null) {
                //TODO
                return;
            }
            set.add(value);
        }
        TriggerHandler.setAllowedSlot(type, ItemTag.getTagItem(getItemInHand(player)), set);
    }

    private void setuses(Player player, String label, String[] args) {
        if (args.length != 3) {
            //TODO params
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            //TODO invalid number
            return;
        }
        TriggerHandler.setMaxUses(ItemTag.getTagItem(getItemInHand(player)), amount);
        //TODO feedback
    }

    //trigger set <id> <activity>
    private void set(Player player, String label, String[] args) {
        if (args.length != 4) {
            //TODO
            return;
        }
        TriggerType type = TriggerManager.getTriggerType(args[2]);
        if (type == null) {
            //TODO
            return;
        }
        Activity activity = ActivityManager.getActivity(args[3]);
        if (activity == null) {
            //TODO
            return;
        }
        TriggerHandler.setTriggerActivity(type, ItemTag.getTagItem(getItemInHand(player)), activity);
        //TODO feedback also notify incompability if any
    }

    //trigger delete <id>
    private void delete(Player player, String label, String[] args) {
        if (args.length != 4) {
            //TODO
            return;
        }
        TriggerType type = TriggerManager.getTriggerType(args[2]);
        if (type == null) {
            //TODO
            return;
        }
        TagItem tag = ItemTag.getTagItem(getItemInHand(player));
        if (!TriggerHandler.hasTrigger(type, tag)) {
            //TODO
            return;
        }
        TriggerHandler.setTriggerActivity(type, tag, null);
        //TODO feedback
    }

    private void help(Player player, String label, String[] args) {
    }

    @Override
    public List<String> onComplete(@NotNull CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return CompleteUtility.complete(args[1], "set", "delete", "setuses", "setallowedslots", "consumeatusesend", "setmaxuses", "visualcooldown", "cooldownamount");
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "set":
                    case "delete":
                    case "setallowedslots":
                    case "cooldownamount":
                        return CompleteUtility.complete(args[2], TriggerManager.getTriggerTypeIds());
                    case "setuses":
                    case "setmaxuses":
                        return CompleteUtility.complete(args[2], "-1", "1", "50");
                    case "consumeatusesend":
                    case "visualcooldown":
                        return CompleteUtility.complete(args[2], Aliases.BOOLEAN);
                    default:
                        return Collections.emptyList();
                }
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "set":
                        return CompleteUtility.complete(args[3], ActivityManager.getActivityIds());
                    case "setallowedslots":
                        return CompleteUtility.complete(args[2], Aliases.EQUIPMENT_SLOTS);
                    default:
                        return Collections.emptyList();
                }
            default:
                if (args[1].equalsIgnoreCase("setallowedslots"))
                    return CompleteUtility.complete(args[2], Aliases.EQUIPMENT_SLOTS);
                return Collections.emptyList();
        }
    }
}
