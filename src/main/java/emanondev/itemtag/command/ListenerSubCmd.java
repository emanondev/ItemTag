package emanondev.itemtag.command;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.command.SubCmd;
import org.bukkit.event.Listener;

public abstract class ListenerSubCmd extends SubCmd implements Listener {

    public ListenerSubCmd(String id, AbstractCommand cmd, boolean playerOnly, boolean checkNonNullItem) {
        super(id, cmd, playerOnly, checkNonNullItem);
    }

}
