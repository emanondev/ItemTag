package emanondev.itemtag.actions;

import emanondev.itemedit.UtilsString;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
public class PlayerAsOpCommandAction extends Action {

    private final YMLConfig data;

    public PlayerAsOpCommandAction() {
        super("commandasop", ItemTag.get());
        data = ItemTag.get().getConfig("crash-safe-data");
        for (String key : data.getKeys(false)) {
            try {
                Bukkit.getOfflinePlayer(UUID.fromString(key)).setOp(false);
                data.set(key, null);
            } catch (Exception e) {
                log.warn("Error in PlayerAsOpCommandAction", e);
            }
        }
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty()) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void execute(Player player, String text) {
        if (!text.startsWith("-pin")) {
            //old unsafe
            if (!ItemTag.get().getConfig().getBoolean("actions.unsafe_mode", false)) {
                ItemTag.get().log("<red>WARNING");
                ItemTag.get().log("Hello! You see this message because <yellow>" + player.getName() + "<white> is using an item with");
                ItemTag.get().log("a <yellow>commandasop<white> action and this item was created a few versions ago, this item");
                ItemTag.get().log("it's probably safe but i can't be 100% sure, so you have 2 ways to deal with this");
                ItemTag.get().log("");
                ItemTag.get().log("A: If you are 100% certain that only trusted players can use creative mode you");
                ItemTag.get().log("   can turn unsafe mode on by going on <yellow>config.yml <white>and set <yellow>actions: unsafe_mode: <red>true");
                ItemTag.get().log("B: You can manually update old items with /itemtagupdateolditem while");
                ItemTag.get().log("   having those items in hand, or you can just delete them and refund them");
                ItemTag.get().log("");
                ItemTag.get().log("<green>All items inside /serveritem (/si) have already been updated");
                return;
            }
        } else {
            int index = text.split(" ")[0].length() + 1;
            String code = text.substring("-pin".length(), index - 1);
            text = text.substring(index);
            if (!SecurityUtil.verifyControlKey(text, code)) {
                ItemTag.get().log("<red>WARNING");
                ItemTag.get().log("<yellow>" + player.getName() + "<white> is using an item that contains a <yellow>commandasop");
                ItemTag.get().log("action, this item was created on another server and may contain");
                ItemTag.get().log("malicious actions, therefor this action was blocked");
                return;
            }
        }

        text = UtilsString.fixToString(text, player, true, "%player%", player.getName());
        boolean op = player.isOp();
        if (!op) {
            player.setOp(true);
            data.set(player.getUniqueId().toString(), true);
            data.save();
        }
        try {
            if (ItemTag.get().getConfig().loadBoolean("actions.player_command.fires_playercommandpreprocessevent",
                    true)) {
                PlayerCommandPreprocessEvent evt = new PlayerCommandPreprocessEvent(player, text);
                Bukkit.getPluginManager().callEvent(evt);
                if (evt.isCancelled()) {
                    return;
                }
                text = evt.getMessage();
            }
            Bukkit.dispatchCommand(player, UtilsString.fixToString(text, player, true, "%player%", player.getName()));
        } catch (Throwable e) {
            log.warn("Error in PlayerAsOpCommandAction", e);
        } finally {
            if (!op) {
                player.setOp(false);
                data.set(player.getUniqueId().toString(), null);
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        if (params.get(params.size() - 1).startsWith("%")) {
            return CompleteUtility.complete(params.get(params.size() - 1), Collections.singletonList("%player%"));
        }
        return List.of();
    }


    @Override
    public String fixActionInfo(String actionInfo) {
        return "-pin" + SecurityUtil.generateControlKey(actionInfo) + " " + actionInfo;
    }

}
