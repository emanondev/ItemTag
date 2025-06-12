package emanondev.itemtag.command;

import emanondev.itemedit.command.AbstractCommand;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ItemTagCommand extends AbstractCommand {
    public static ItemTagCommand instance;

    public ItemTagCommand() {
        super("itemtag", ItemTag.get());
        instance = this;
        this.registerSubCommand(new Effects(this));
        this.registerSubCommand(new Actions(this));
        this.registerSubCommand(new ConsumeActions(this));
        this.registerSubCommand(new UsePermission(this));
        this.registerSubCommand(new WearPermission(this));
        this.registerSubCommand(new Flag(this));
        //TODO this.registerSubCommand(new ActivitySubCommand(this));
        //TODO this.registerSubCommand(new TriggerAction(this));
    }

    public static ItemTagCommand get() {
        return instance;
    }

    public void registerSubCommand(@NotNull SubCmd cmd) {
        super.registerSubCommand(cmd);
        if (cmd instanceof ListenerSubCmd)
            Bukkit.getPluginManager().registerEvents((Listener) cmd, ItemTag.get());
    }

}
