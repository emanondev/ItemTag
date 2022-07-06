package emanondev.itemtag;

import emanondev.itemedit.YMLConfig;

import java.util.Arrays;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 2;

    static void update() {
        ItemTag plugin = ItemTag.get();
        int version = plugin.getConfig().getInt("version", 1);
        if (version >= CURRENT_VERSION)
            return;

        if (version <= 1) {
            YMLConfig lang = ItemTag.get().getLanguageConfig(null);
            lang.set("gui.flags.entityfood", Arrays.asList(
                            "&6Sets whether the item can be used as animal's food",
                            "&bValue: &e%value%", "",
                            "&7If set to false deny vanilla breed behavior",
                            "&7 and also baby fast grow behavior",
                            "&7 on this item","",
                            "&7[&fClick&7]&b Toggle"));
            lang.set("itemtag.flag.entityfood.feedback.standard",
                    "&9[&fItemTag&9] &aThe item can be used as animal's food.");
            lang.set("itemtag.flag.entityfood.feedback.custom",
                    "&9[&fItemTag&9] &aThe item can''t be used as animal's food.");

        }

        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);
        plugin.getConfig().save();
    }
}
