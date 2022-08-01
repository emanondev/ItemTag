package emanondev.itemtag.denizen;

import com.denizenscript.denizen.objects.NPCTag;
import com.denizenscript.denizen.objects.PlayerTag;
import com.denizenscript.denizen.utilities.depends.Depends;
import com.denizenscript.denizen.utilities.implementation.BukkitScriptEntryData;
import com.denizenscript.denizencore.scripts.ScriptBuilder;
import com.denizenscript.denizencore.scripts.ScriptEntry;
import com.denizenscript.denizencore.scripts.queues.core.InstantQueue;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class DenizenUtils {

    public void runDenizenScript(CommandSender sender, String denizenScript, String[] args) {
        List<Object> entries = new ArrayList<>();

        args = (String[]) ArrayUtils.add(args, 0, denizenScript);
        args = (String[]) ArrayUtils.add(args, 0, "run");

        // format "<denizen script command> [arguments...]"
        String entry = String.join(" ", args);

        entries.add(entry);
        InstantQueue queue = new InstantQueue("EXCOMMAND");
        NPCTag npc = null;
        if (Depends.citizens != null && Depends.citizens.getNPCSelector().getSelected(sender) != null) {
            npc = new NPCTag(Depends.citizens.getNPCSelector().getSelected(sender));
        }
        List<ScriptEntry> scriptEntries = ScriptBuilder.buildScriptEntries(entries, null,
                new BukkitScriptEntryData(sender instanceof Player ? new PlayerTag((Player) sender) : null, npc));
        queue.addEntries(scriptEntries);

        queue.start();
    }
}
