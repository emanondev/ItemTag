package emanondev.itemtag.actions;

import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemtag.ItemTag;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PermissionAction extends Action {

    public PermissionAction() {
        super("permission", ItemTag.get());
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty()) {
            throw new IllegalStateException();
        }
        String[] args = text.split(" ");
        if (args.length < 3) {
            throw new IllegalStateException();
        }

        ActionHandler.validateActionType(args[1]);
        ActionHandler.validateActionInfo(args[1], text.substring(args[0].length() + args[1].length() + 2));
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        if (args[0].startsWith("-")) {
            if (player.hasPermission(args[0].substring(1))) {
                return;
            }
        } else if (!player.hasPermission(args[0])) {
            return;
        }
        ActionHandler.handleAction(player, args[1], text.substring(args[0].length() + args[1].length() + 2));

    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        return switch (params.size()) {
            case 1 -> List.of();
            case 2 -> CompleteUtility.complete(params.get(1), ActionHandler.getTypes());
            default -> {
                Action sub = ActionHandler.getAction(params.get(1));
                if (sub == null) {
                    yield List.of();
                }
                params.removeFirst();
                params.removeFirst();
                yield sub.tabComplete(sender, params);
            }
        };
    }

    @Override
    public String fixActionInfo(String actionInfo) {
        //permission <perm> <actiontype> <actioninfo>
        String[] args = actionInfo.split(" ");
        return args[0] + " " + args[1] + " " + ActionHandler.getAction(args[1])
                .fixActionInfo(actionInfo.substring(args[0].length() + args[1].length() + 2));
    }


    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("<aqua>" + getId() + " <yellow><permission> <action>");
        list.add("<yellow><permission> <aqua>permission required or <yellow>-permission <aqua>for reversed check");
        list.add("<yellow><action> <aqua>action to execute, example: '<yellow>sound entity_bat_hurt 1 1<aqua>'");
        return list;
    }
}