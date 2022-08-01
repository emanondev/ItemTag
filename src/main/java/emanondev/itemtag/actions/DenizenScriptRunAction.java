package emanondev.itemtag.actions;

import emanondev.itemtag.denizen.DenizenUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DenizenScriptRunAction extends Action {

    public DenizenScriptRunAction() {
        super("runscript");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty())
            throw new IllegalStateException();
        // TODO in the future check if the script exists
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        String script = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        new DenizenUtils().runDenizenScript(player, script, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        return Collections.emptyList(); // TODO instead return all the scripts
    }

    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("&b" + getID() + " &e<denizen script> [args...]");
        list.add("&e<denizen script> &bscript executed by player");
        list.add("&e[args...] &barguments passed to the script");
        return list;
    }
}
