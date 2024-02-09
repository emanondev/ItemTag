package emanondev.itemtag.activity.action;

import emanondev.itemedit.UtilsString;
import emanondev.itemtag.activity.ActionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TitleActionType extends ActionType {
    public TitleActionType() {
        super("title");
    }

    @Override
    public @NotNull Action read(@NotNull String info) {
        return new TitleAction(info);
    }

    public class TitleAction extends Action {

        private final String message;
        private final String sub;
        private final int fadeIn;
        private final int fadeOut;
        private final int stay;

        public TitleAction(@NotNull String info) {
            super(info);
            String[] args = info.split(";");
            if (args.length != 5)
                throw new IllegalArgumentException("Invalid format: '" + getInfo() + "' must be '[title];[subtitle];<fadein ticks>;<stay ticks>;<fadeout ticks>'");
            message = args[0];
            sub = args[1];
            fadeIn = Integer.parseInt(args[2]);
            stay = Integer.parseInt(args[3]);
            fadeOut = Integer.parseInt(args[4]);
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            player.sendTitle(UtilsString.fix(message, player, true),
                    UtilsString.fix(sub, player, true),
                    fadeIn, stay, fadeOut);
            return true;
        }
    }
}
