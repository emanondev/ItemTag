package emanondev.itemtag.command;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public class ItemTagCommand extends AbstractCommand {
    public static ItemTagCommand instance;

    public static ItemTagCommand get() {
        return instance;
    }

    public ItemTagCommand() {
        super("itemtag", ItemTag.get());
        instance = this;
        this.registerSubCommand(new Effects(this));
        this.registerSubCommand(new Actions(this));
        this.registerSubCommand(new ConsumeActions(this));
        this.registerSubCommand(new UsePermission(this));
        this.registerSubCommand(new Flag(this));
    }

    public void registerSubCommand(SubCmd cmd) {
        super.registerSubCommand(cmd);
        if (cmd instanceof ListenerSubCmd)
            Bukkit.getPluginManager().registerEvents((Listener) cmd, ItemTag.get());
    }

}
