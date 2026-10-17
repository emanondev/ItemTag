package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import emanondev.itemedit.utility.ItemUtils;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.command.itemtag.Flag;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomFlag implements Listener, Comparable<CustomFlag> {
    private final String ID;
    private final String key;
    private final Flag subCommand;
    private final String PATH;
    private final YMLConfig config;

    public CustomFlag(@NotNull String id, @NotNull String key, @NotNull Flag subCommand) {
        if (id.isEmpty() || id.contains(" ")) {
            throw new IllegalArgumentException();
        }
        this.ID = id;
        this.key = key;
        this.subCommand = subCommand;
        config = this.getPlugin().getConfig("commands.yml");
        PATH = subCommand.getCommand().getName() + "." + subCommand.getId() + "." + this.ID + ".";
    }

    @Override
    public int compareTo(CustomFlag flag) {
        return this.getId().compareTo(flag.getId());
    }

    public APlugin getPlugin() {
        return subCommand.getPlugin();
    }

    protected void sendLanguageString(String path, CommandSender sender, String... holders) {
        Util.sendComponent(sender, getPlugin().getTranslator().translateOrEmpty(sender, this.PATH + path, holders));
    }

    protected List<Component> getLanguageComponentList(String path, CommandSender sender, String... holders) {
        return getPlugin().getTranslator().translateList(sender, this.PATH + path, holders);
    }

    protected String getConfigString(String path, String... holders) {
        return config.loadMessage(this.PATH + path, "", null, true, holders);
    }

    protected int getConfigInt(String path) {
        return config.loadInteger(this.PATH + path, 0);
    }

    protected long getConfigLong(String path) {
        return config.loadLong(this.PATH + path, 0L);
    }

    protected boolean getConfigBoolean(String path) {
        return config.loadBoolean(this.PATH + path, true);
    }

    protected List<String> getConfigStringList(String path, String... holders) {
        return config.loadMultiMessage(this.PATH + path, new ArrayList<>(), null, true, holders);
    }

    public final String getId() {
        return ID;
    }

    public boolean getValue(TagItem item) {
        Boolean value = item.getBoolean(key);
        return value == null ? this.defaultValue() : value;
    }

    public void setValue(TagItem item, boolean val) {
        if (val == defaultValue()) {
            item.removeTag(getKey());
        } else {
            item.setTag(getKey(), val);
        }
    }

    public boolean defaultValue() {
        return true;
    }

    public abstract ItemStack getGuiItem();

    public final String getKey() {
        return key;
    }

    public void reload() {
    }

    protected ItemStack getItemInHand(Player p) {
        return ItemUtils.getHandMainItem(p);
    }

}
