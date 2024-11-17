package emanondev.itemtag.compability;

import emanondev.itemedit.Util;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.command.itemtag.Actions;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * This class will automatically register as a placeholder expansion when a jar
 * including this class is added to the directory
 * {@code /plugins/PlaceholderAPI/expansions} on your server. <br>
 * <br>
 * If you create such a class inside your own plugin, you have to register it
 * manually in your plugins {@code onEnable()} by using
 * {@code new YourExpansionClass().register();}
 */
public class PlaceHolders extends PlaceholderExpansion {
    public PlaceHolders() {

        ItemTag.get().log("Hooked into PlaceHolderAPI:");
        ItemTag.get().log("placeholders:");
        ItemTag.get().log("  &e%itemtag_cooldown_&6<timeunit>&e_&6[cooldownid]&e%");
        ItemTag.get().log("    shows how much cooldown has selected cooldownid for player");
        ItemTag.get().log("    <timeunit> may be &eh&f, &es &for &ems");
        ItemTag.get().log("    [cooldownid] for cooldown type, by default &adefault");
        ItemTag.get().log("    example: %itemtag_cooldown_s_anid%");
        ItemTag.get().log("  &e%itemtag_handcooldown_&6<timeunit>&e%");
        ItemTag.get().log("    shows how much cooldown has player on the item in his hand");
        ItemTag.get().log("    <timeunit> may be &eh&f, &es &for &ems");
        ItemTag.get().log("    example: %itemtag_handcooldown_s%");
    }

    /**
     * The name of the person who created this expansion should go here.
     *
     * @return The name of the author as a String.
     */
    @Override
    public @NotNull String getAuthor() {
        return "emanon";
    }

    /**
     * The placeholder identifier should go here. <br>
     * This is what tells PlaceholderAPI to call our onRequest method to obtain a
     * value if a placeholder starts with our identifier. <br>
     * This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public @NotNull String getIdentifier() {
        return "itemtag";
    }

    /**
     * if the expansion requires another plugin as a dependency, the proper name of
     * the dependency should go here. <br>
     * Set this to {@code null} if your placeholders do not require another plugin
     * to be installed on the server for them to work. <br>
     * <br>
     * This is extremely important to set your plugin here, since if you don't do
     * it, your expansion will throw errors.
     *
     * @return The name of our dependency.
     */
    @Override
    public String getRequiredPlugin() {
        return ItemTag.get().getName();
    }

    /**
     * This is the version of this expansion. <br>
     * You don't have to use numbers, since it is set as a String.
     *
     * @return The version as a String.
     */
    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    /**
     * This is the method called when a placeholder with our identifier is found and
     * needs a value. <br>
     * We specify the value identifier in this method. <br>
     * Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param player A {@link Player Player}.
     * @param value  A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, @NotNull String value) {
        if (player == null) {
            return "";
        }
        try {
            String[] args = value.split("_");
            String id;
            switch (args[0].toLowerCase(Locale.ENGLISH)) {
                case "cooldown": {
                    id = args.length >= 3 ? value.substring(2 + args[0].length() + args[1].length()) : Actions.getDefaultCooldownId();
                    break;
                }
                case "handcooldown": {
                    ItemStack item = player.getInventory().getItemInHand();
                    if (Util.isAirOrNull(item))
                        return "0";
                    id = Actions.getCooldownId(ItemTag.getTagItem(item));
                    break;
                }
                case "usesleft": {
                    ItemStack item = player.getInventory().getItemInHand();
                    if (Util.isAirOrNull(item))
                        return "0";
                    return String.valueOf(Actions.getUses(ItemTag.getTagItem(item)));
                }
                default:
                    throw new IllegalStateException();
            }
            switch (args[1].toLowerCase(Locale.ENGLISH)) {
                case "h":
                    return String.valueOf(ItemTag.get().getCooldownAPI().getCooldownHours(player, id));
                case "s":
                    return String.valueOf(ItemTag.get().getCooldownAPI().getCooldownSeconds(player, id));
                case "ms":
                    return String.valueOf(ItemTag.get().getCooldownAPI().getCooldownMillis(player, id));
                default:
                    throw new IllegalStateException();
            }
        } catch (Exception e) {
            ItemTag.get().log("&c! &fWrong PlaceHolderValue %" + getIdentifier() + "_" + ChatColor.YELLOW + value
                    + ChatColor.WHITE + "%");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }
}