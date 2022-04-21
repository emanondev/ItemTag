package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.AliasSet;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.itemtag.customflags.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Flag extends SubCmd {

    private final HashSet<CustomFlag> flags = new HashSet<>();
    private final AliasSet<CustomFlag> aliases;

    public Flag(ItemTagCommand cmd) {
        super("flag", cmd, true, true);
        aliases = new AliasSet<CustomFlag>("custom_flags") {
            @Override
            public String getName(CustomFlag customFlag) {
                return customFlag.getId();
            }

            @Override
            public Collection<CustomFlag> getValues() {
                return flags;
            }

        };
        this.registerFlag(new Placeable(this));
        this.registerFlag(new Usable(this));
        this.registerFlag(new CraftRecipeIngredient(this));
        this.registerFlag(new Smelt(this));
        this.registerFlag(new FurnaceFuel(this));
        this.registerFlag(new Enchantable(this));
        if (ItemEdit.GAME_VERSION > 8)
            this.registerFlag(new Renamable(this));
        if (ItemEdit.GAME_VERSION > 13)
            this.registerFlag(new Grindable(this));
        aliases.reload();
        Aliases.registerAliasType(aliases);
    }

    public void reload() {
        for (CustomFlag flag : flags)
            flag.reload();
    }

    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        TagItem item = ItemTag.getTagItem(this.getItemInHand(p));
        try {
            if (args.length == 1) {
                //TODO error
                return;
            }

            CustomFlag flag = aliases.convertAlias(args[1]);
            if (flag == null) {
                //TODO wrong flag name

                return;
            }
            boolean value = args.length == 3 ? Aliases.BOOLEAN.convertAlias(args[2])
                    : !flag.getValue(item);
            flag.setValue(item, value);
            sendLanguageString(flag.getId()+"."+(value==flag.defaultValue()?"standard":"custom")
                ,null,p);//TODO feedback


        } catch (Exception e) {
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], aliases);
        if (args.length == 3)
            return Util.complete(args[1], Aliases.BOOLEAN);
        return Collections.emptyList();
    }

    public void registerFlag(CustomFlag flag) {
        for (CustomFlag f : flags)
            if (f.getId().equals(flag.getId()))
                throw new IllegalStateException("Id already used");
        getPlugin().registerListener(flag);
        flags.add(flag);
        aliases.reload();
    }

}