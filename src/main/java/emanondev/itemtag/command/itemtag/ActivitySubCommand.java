package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.Util;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemtag.activity.*;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.gui.ActivityGui;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ActivitySubCommand extends SubCmd {
    public ActivitySubCommand(@NotNull ItemTagCommand cmd) {
        super("activity", cmd, true, false);
    }

    @Override
    public void onCommand(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        if (args.length == 1) {
            help(player, label, args);
            return;
        }

        switch (args[1].toLowerCase(Locale.ENGLISH)) {
            case "create":
                create(player, label, args);
                return;
            case "delete":
                delete(player, label, args);
                return;
            case "clone":
                clone(player, label, args);
                return;
            case "rename":
                rename(player, label, args);
                return;
            case "open":
                open(player, label, args);
                return;
            case "setconsumes":
                setconsumes(player, label, args);
                return;
            case "addcondition":
                addcondition(player, label, args);
                return;
            case "insertcondition":
                insertcondition(player, label, args);
                return;
            case "setcondition":
                setcondition(player, label, args);
                return;
            case "removecondition":
                removecondition(player, label, args);
                return;
            case "addaction":
                addaction(player, label, args);
                return;
            case "insertaction":
                insertaction(player, label, args);
                return;
            case "setaction":
                setaction(player, label, args);
                return;
            case "removeaction":
                removeaction(player, label, args);
                return;
            case "addalternativeaction":
                addalternativeaction(player, label, args);
                return;
            case "insertalternativeaction":
                insertalternativeaction(player, label, args);
                return;
            case "setalternativeaction":
                setalternativeaction(player, label, args);
                return;
            case "removealternativeaction":
                removealternativeaction(player, label, args);
                return;
            case "addnoconsumesaction":
                addnoconsumesaction(player, label, args);
                return;
            case "insertnoconsumesaction":
                insertnoconsumesaction(player, label, args);
                return;
            case "setnoconsumesaction":
                setnoconsumesaction(player, label, args);
                return;
            case "removenoconsumesaction":
                removenoconsumesaction(player, label, args);
                return;
        }

    }

    private void help(Player player, String label, String[] args) {
    }

    private void create(Player player, String label, String[] args) {
        if (args.length != 3) {
            Util.sendMessage(player, this.craftFailFeedback(getLanguageString("create.params", null, player),
                    getLanguageStringList("create.description", null, player)));
            return;
        }
        Activity activity = ActivityManager.getActivity(args[2]);
        if (activity != null) {
            sendLanguageString("feedback.already_used_activity_id", null, player, "%id%", args[2]);
            return;
        }
        activity = new Activity(args[2]);
        ActivityManager.registerActivity(activity);
        //TODO open gui?
        sendLanguageString("create.feedback", null, player, "%id%", args[2]);
    }

    //activity rename <id> <newId>
    private void rename(Player player, String label, String[] args) {
        if (args.length != 4) {
            //TODO args
            return;
        }
        Activity activity = ActivityManager.getActivity(args[2]);
        if (activity == null) {
            sendLanguageString("feedback.invalid_activity_id", null, player, "%id%", args[2]);
            return;
        }
        Activity newActivity = ActivityManager.getActivity(args[3]);
        if (newActivity != null) {
            sendLanguageString("feedback.already_used_activity_id", null, player, "%id%", args[3]);
            return;
        }
        ActivityManager.rename(activity, args[3]);
        //TODO feedback
    }

    //open <action id>
    private void open(Player player, String label, String[] args) {
        if (args.length != 3) {
            //TODO args
            return;
        }
        Activity activity = ActivityManager.getActivity(args[2]);
        if (activity == null) {
            sendLanguageString("feedback.invalid_activity_id", null, player, "%id%", args[2]);
            return;
        }
        player.openInventory(new ActivityGui(activity, player, null).getInventory());
    }

    private void delete(Player player, String label, String[] args) {
        if (args.length != 3) {
            //TODO args
            return;
        }
        Activity activity = ActivityManager.getActivity(args[2]);
        if (activity == null) {
            sendLanguageString("feedback.invalid_activity_id", null, player, "%id%", args[2]);
            return;
        }
        ActivityManager.deleteActivity(activity);
        //TODO feedback
    }

    private void clone(Player player, String label, String[] args) {
        if (args.length != 4) {
            //TODO args
            return;
        }
        Activity activity = ActivityManager.getActivity(args[2]);
        if (activity == null) {
            sendLanguageString("feedback.invalid_activity_id", null, player, "%id%", args[2]);
            return;
        }
        Activity newActivity = ActivityManager.getActivity(args[3]);
        if (newActivity != null) {
            sendLanguageString("feedback.already_used_activity_id", null, player, "%id%", args[3]);
            return;
        }
        ActivityManager.clone(activity, args[3]);
        //TODO feedback
    }

    //setconsumes <activity> <amount>
    private void setconsumes(Player player, String label, String[] args) {
        if (args.length != 4) {
            //TODO args
            return;
        }
        Activity activity = ActivityManager.getActivity(args[2]);
        if (activity == null) {
            sendLanguageString("feedback.invalid_activity_id", null, player, "%id%", args[2]);
            return;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[3]);
        } catch (Exception e) {
            //TODO NaN
            return;
        }
        //TODO optional error if <0

        activity.setConsumes(amount);
        //TODO
    }

    //addcondition <activity> <conditiontype> <condition arguments>
    private void addcondition(Player player, String label, String[] args) {
        if (args.length < 4) {
            //TODO args
            return;
        }
        Activity activity = ActivityManager.getActivity(args[2]);
        if (activity == null) {
            sendLanguageString("feedback.invalid_activity_id", null, player, "%id%", args[2]);
            return;
        }
        ConditionType type = ConditionManager.getConditionType(args[3]);
        if (type == null) {
            //TODO
            return;
        }
        StringBuilder b = new StringBuilder(args[3]);
        for (int i = 4; i < args.length; i++)
            b.append(" ").append(args[i]);
        ConditionType.Condition condition;
        try {
            condition = ConditionManager.read(b.toString());
        } catch (Exception e) {
            //TODO bad format
            return;
        }
        if (condition == null) {
            //TODO invalid condition type
            return;
        }
        activity.addCondition(condition);
        //TODO
    }

    private void insertcondition(Player player, String label, String[] args) {
    }

    private void setcondition(Player player, String label, String[] args) {
    }

    private void removecondition(Player player, String label, String[] args) {
    }

    private void addaction(Player player, String label, String[] args) {
    }

    private void insertaction(Player player, String label, String[] args) {
    }

    private void setaction(Player player, String label, String[] args) {
    }

    private void removeaction(Player player, String label, String[] args) {
    }

    private void addalternativeaction(Player player, String label, String[] args) {
    }

    private void insertalternativeaction(Player player, String label, String[] args) {
    }

    private void setalternativeaction(Player player, String label, String[] args) {
    }

    private void removealternativeaction(Player player, String label, String[] args) {
    }

    private void addnoconsumesaction(Player player, String label, String[] args) {
    }

    private void insertnoconsumesaction(Player player, String label, String[] args) {
    }

    private void setnoconsumesaction(Player player, String label, String[] args) {
    }

    private void removenoconsumesaction(Player player, String label, String[] args) {
    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        switch (args.length) {
            case 2:
                return Util.complete(args[1], "create", "delete", "clone", "rename", "open", "setconsumes",
                        "addcondition", "insertcondition", "setcondition", "removecondition",
                        "addaction", "insertaction", "setaction", "removeaction",
                        "addalternativeaction", "insertalternativeaction", "setalternativeaction", "removealternativeaction",
                        "addnoconsumesaction", "insertnoconsumesaction", "setnoconsumesaction", "removenoconsumesaction");
            case 3:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    case "delete":
                    case "clone":
                    case "rename":
                    case "open":
                    case "setconsumes":
                    case "addcondition":
                    case "insertcondition":
                    case "setcondition":
                    case "removecondition":
                    case "addaction":
                    case "insertaction":
                    case "setaction":
                    case "removeaction":
                    case "addalternativeaction":
                    case "insertalternativeaction":
                    case "setalternativeaction":
                    case "removealternativeaction":
                    case "addnoconsumesaction":
                    case "insertnoconsumesaction":
                    case "setnoconsumesaction":
                    case "removenoconsumesaction":
                        return Util.complete(args[1], ActivityManager.getActivityIds());
                    default:
                        return Collections.emptyList();
                }
            case 4:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    //? <number>
                    case "setconsumes":
                    case "insertcondition":
                    case "setcondition":
                    case "removecondition":
                    case "insertaction":
                    case "setaction":
                    case "removeaction":
                    case "insertalternativeaction":
                    case "setalternativeaction":
                    case "removealternativeaction":
                    case "insertnoconsumesaction":
                    case "setnoconsumesaction":
                    case "removenoconsumesaction":
                        return Collections.emptyList();
                    //? <condition id>
                    case "addcondition":
                        return Util.complete(args[3], ConditionManager.getConditionTypeIds());
                    //? <action id>
                    case "addaction":
                    case "addalternativeaction":
                    case "addnoconsumesaction":
                        return Util.complete(args[3], ActionManager.getActionTypeIds());
                    default:
                        return Collections.emptyList();
                }
            case 5:
                switch (args[1].toLowerCase(Locale.ENGLISH)) {
                    //? <conditiontype>
                    case "insertcondition":
                    case "setcondition":
                    case "removecondition":
                        return Util.complete(args[4], ConditionManager.getConditionTypeIds());
                    //? <actiontype>
                    case "insertaction":
                    case "setaction":
                    case "removeaction":
                    case "insertalternativeaction":
                    case "setalternativeaction":
                    case "removealternativeaction":
                    case "insertnoconsumesaction":
                    case "setnoconsumesaction":
                    case "removenoconsumesaction":
                        return Util.complete(args[4], ActionManager.getActionTypeIds());
                    default:
                        return Collections.emptyList();
                }
            default:
                return Collections.emptyList();
        }
    }
    /*
    create <id>
    delete <id>
    clone <id> <newid>
    rename <id> <newid>
    open <id>

    setconsumes <amount>
    setalternativeconsumes <amount>

    //wiki

    addcondition <id> <condition>
    insertcondition <id> <slot> <condition>
    setcondition <id> <slot> <condition>
    removecondition <id> <slot>

    addaction <id> <action>
    insertaction <id> <slot> <action>
    setaction <id> <slot> <action>
    removeaction <id> <slot>

    addalternativeaction <id> <action>
    addalternativeaction <id> <slot> <action>
    setalternativeaction <id> <slot> <action>
    removealternativeaction <id> <slot>

    addalternativeaction <id> <action>
    addalternativeaction <id> <slot> <action>
    setalternativeaction <id> <slot> <action>
    removealternativeaction <id> <slot>

    addnoconsumesaction <id> <action>
    addnoconsumesaction <id> <slot> <action>
    setnoconsumesaction <id> <slot> <action>
    removenoconsumesaction <id> <slot>

     */
}
