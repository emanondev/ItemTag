package emanondev.itemtag.command.itemtag.customflags;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.Util;
import emanondev.itemedit.YMLConfig;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.command.itemtag.Flag;
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
        if (id.equals("") || id.contains(" "))
            throw new IllegalArgumentException();
        this.ID = id;
        this.key = key;
        this.subCommand = subCommand;
        config = this.getPlugin().getConfig("commands.yml");
        PATH = subCommand.getCommand().getName() + "." + subCommand.ID + "." + this.ID + ".";
    }

    @Override
    public int compareTo(CustomFlag flag) {
        return this.getId().compareTo(flag.getId());
    }

    public APlugin getPlugin() {
        return subCommand.getPlugin();
    }

    protected String getLanguageString(String path, String def, CommandSender sender, String... holders) {
        return getPlugin().getLanguageConfig(sender).loadMessage(this.PATH + path, def == null ? "" : def,
                sender instanceof Player ? (Player) sender : null, true, holders);
    }

    protected void sendLanguageString(String path, String def, CommandSender sender, String... holders) {
        Util.sendMessage(sender, getLanguageString(path, def, sender, holders));
    }

    protected List<String> getLanguageStringList(String path, List<String> def, CommandSender sender, String... holders) {
        return getPlugin().getLanguageConfig(sender).loadMultiMessage(this.PATH + path,
                def == null ? new ArrayList<>() : def, sender instanceof Player ? (Player) sender : null, true, holders);
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
        return item.hasBooleanTag(key) ? item.getBoolean(key) : this.defaultValue();
    }

    public void setValue(TagItem item, boolean val) {
        if (val == defaultValue())
            item.removeTag(getKey());
        else
            item.setTag(getKey(), val);
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
        return p.getInventory().getItemInHand();
    }

}
