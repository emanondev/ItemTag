package emanondev.itemtag.actions;

import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemtag.ItemTag;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DelayedAction extends Action {

    public DelayedAction() {
        super("delay", ItemTag.get());
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

        long ticks = Long.parseLong(args[0]);
        if (ticks <= 0) {
            throw new IllegalStateException();
        }

        ActionHandler.validateActionType(args[1]);
        ActionHandler.validateActionInfo(args[1], text.substring(args[0].length() + args[1].length() + 2));
    }


    @Override
    public String fixActionInfo(String actionInfo) {
        //delay <ticks> <actiontype> <actioninfo>
        String[] args = actionInfo.split(" ");
        return args[0] + " " + args[1] + " " + ActionHandler.getAction(args[1])
                .fixActionInfo(actionInfo.substring(args[0].length() + args[1].length() + 2));
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        new BukkitRunnable() {
            public void run() {
                ActionHandler.handleAction(player, args[1], text.substring(args[0].length() + args[1].length() + 2));
            }
        }.runTaskLater(ItemTag.get(), Long.parseLong(args[0]));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        return switch (params.size()) {
            case 1 -> CompleteUtility.complete(params.getFirst(), Arrays.asList("20", "100", "200"));
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
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("<aqua>" + getId() + " <yellow><delay ticks> <action>");
        list.add("<yellow><delay ticks> <aqua>how much you want to delay the action, 20ticks = 1s");
        list.add("<yellow><action> <aqua>action to execute, example: '<yellow>sound entity_bat_hurt 1 1<aqua>'");
        return list;
    }
}
