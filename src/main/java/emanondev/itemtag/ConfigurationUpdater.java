package emanondev.itemtag;

import emanondev.itemedit.YMLConfig;

import java.util.Arrays;

class ConfigurationUpdater {
    private static final int CURRENT_VERSION = 4;

    static void update() {
        ItemTag plugin = ItemTag.get();
        int version = plugin.getConfig().getInt("config-version", 1);
        if (version >= CURRENT_VERSION)
            return;

        if (version <= 1) {
            YMLConfig lang = ItemTag.get().getLanguageConfig(null);
            lang.set("gui.flags.entityfood", Arrays.asList(
                    "&6Sets whether the item can be used as animal's food",
                    "&bValue: &e%value%", "",
                    "&7If set to false deny vanilla breed behavior",
                    "&7 and also baby fast grow behavior",
                    "&7 on this item", "",
                    "&7[&fClick&7]&b Toggle"));
            lang.set("itemtag.flag.entityfood.feedback.standard",
                    "&9[&fItemTag&9] &aThe item can be used as animal's food.");
            lang.set("itemtag.flag.entityfood.feedback.custom",
                    "&9[&fItemTag&9] &aThe item can''t be used as animal's food.");
        }
        if (version <= 2) {
            YMLConfig lang = ItemTag.get().getLanguageConfig(null);
            lang.set("gui.middleclick.creative", "Middle Click");
            lang.set("gui.middleclick.other", "Press 1");
            if (Arrays.asList("&6&lNext Page", "", "&7[&fClick&7]&b Go to page &e%target_page%")
                    .equals(lang.get("gui.previous-page")))
                lang.set("gui.previous-page", Arrays.asList("&6&lPrevious Page", "",
                        "&7[&fClick&7]&b Go to page &e%target_page%"));
            if (Arrays.asList("&6&lPrevious Page", "", "&7[&fClick&7]&b Go to page &e%target_page%")
                    .equals(lang.get("gui.next-page")))
                lang.set("gui.previous-page", Arrays.asList("&6&lNext Page", "",
                        "&7[&fClick&7]&b Go to page &e%target_page%"));
            lang.set("gui.effects.potion", Arrays.asList("&6&l%effect%", "",
                    "&bLevel: &e%level% &7[Left/Right Click Change]",
                    "&bParticles: &e%particles% &7[ShiftLeft Click Toggle]",
                    "&bAmbient: &e%ambient% &7[ShiftRight Click Toggle]",
                    "&bIcon: &e%icon% &7[%middle_click% Toggle]", "&bDuration: &e%duration%"));
        }
        if (version <= 3) {
            YMLConfig lang = ItemTag.get().getLanguageConfig(null);
            lang.set("gui.flags.vanishcurse", Arrays.asList("&6Sets whether the item disappear on death",
                    "&bValue: &e%value%", "", "&7Works exactly as the enchantment Vanish Curse",
                    "&7But it's not an enchantment", "", "&7[&fClick&7]&b Toggle"));
        }


        plugin.log("Updating configuration version (" + version + " -> " + CURRENT_VERSION + ")");
        plugin.getConfig().set("config-version", CURRENT_VERSION);
        plugin.getConfig().save();
    }
}
