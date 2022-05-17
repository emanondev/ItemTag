package emanondev.itemtag.command.itemtag;

import emanondev.itemedit.APlugin;
import emanondev.itemedit.ItemEdit;
import emanondev.itemedit.Util;
import emanondev.itemedit.aliases.AliasSet;
import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.command.SubCmd;
import emanondev.itemedit.gui.Gui;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.TagItem;
import emanondev.itemtag.command.ItemTagCommand;
import emanondev.itemtag.command.itemtag.customflags.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Flag extends SubCmd {

    private final TreeSet<CustomFlag> flags = new TreeSet<>();
    private final AliasSet<CustomFlag> FLAG_ALIASES;

    public Flag(ItemTagCommand cmd) {
        super("flag", cmd, true, true);
        FLAG_ALIASES = new AliasSet<CustomFlag>("custom_flags") {
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
        this.registerFlag(new EntityFood(this));
        if (ItemEdit.GAME_VERSION > 8)
            this.registerFlag(new Renamable(this));
        if (ItemEdit.GAME_VERSION > 13)
            this.registerFlag(new Grindable(this));
        this.registerFlag(new EquipmentFlag(this));
        //aliases.reload();
        Aliases.registerAliasType(FLAG_ALIASES, true);
    }

    public void reload() {
        for (CustomFlag flag : flags)
            flag.reload();
    }

    //it flag <name> [value]
    @Override
    public void onCommand(CommandSender sender, String alias, String[] args) {
        Player p = (Player) sender;
        ItemStack item = this.getItemInHand(p);
        try {
            if (args.length == 1) {
                p.openInventory(new FlagGui(p, item).getInventory());
                return;
            }
            TagItem tagItem = ItemTag.getTagItem(item);

            CustomFlag flag = FLAG_ALIASES.convertAlias(args[1]);
            if (flag == null) {
                onWrongAlias("wrong-flag", p, FLAG_ALIASES);
                onFail(p, alias);
                return;
            }
            boolean value = args.length >= 3 ? Aliases.BOOLEAN.convertAlias(args[2])
                    : !flag.getValue(tagItem);
            flag.setValue(tagItem, value);
            sendLanguageString(flag.getId() + ".feedback." +
                            (value == flag.defaultValue() ? "standard" : "custom")
                    , null, p);
        } catch (Exception e) {
            e.printStackTrace();
            onFail(p, alias);
        }

    }

    @Override
    public List<String> onComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return Util.complete(args[1], FLAG_ALIASES);
        if (args.length == 3)
            return Util.complete(args[2], Aliases.BOOLEAN);
        return Collections.emptyList();
    }

    public void registerFlag(CustomFlag flag) {
        for (CustomFlag f : flags)
            if (f.getId().equals(flag.getId()))
                throw new IllegalStateException("Id already used");
        getPlugin().registerListener(flag);
        flags.add(flag);
        FLAG_ALIASES.reload();
    }

    private class FlagGui implements Gui {
        private final TagItem tagItem;
        private final Inventory inventory;
        private final Player target;

        public FlagGui(Player target, ItemStack item) {
            String title = this.getLanguageMessage("gui.actions.title",
                    "%player_name%", target.getName());
            this.inventory = Bukkit.createInventory(this, 2 * 9, title);
            this.target = target;
            tagItem = ItemTag.getTagItem(item);

            updateInventory();
        }

        @Override
        public void onClose(InventoryCloseEvent event) {
        }

        @Override
        public void onClick(InventoryClickEvent event) {
            if (!event.getWhoClicked().equals(target))
                return;
            if (!inventory.equals(event.getClickedInventory()))
                return;
            if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
                return;
            CustomFlag flag = new ArrayList<>(flags).get(event.getSlot());
            if (flag == null)
                return;
            flag.setValue(tagItem, !flag.getValue(tagItem));
            updateInventory();
        }

        @Override
        public void onDrag(InventoryDragEvent inventoryDragEvent) {
        }

        @Override
        public void onOpen(InventoryOpenEvent inventoryOpenEvent) {
            updateInventory();
        }

        private void updateInventory() {
            List<CustomFlag> flags = new ArrayList<>(Flag.this.flags);
            for (int i = 0; i < flags.size(); i++) {
                CustomFlag flag = flags.get(i);
                ItemStack item = flag.getGuiItem();
                ItemMeta meta = item.getItemMeta();
                boolean value = flag.getValue(tagItem);
                this.loadLanguageDescription(meta, "gui.flags." + flag.getId(), "%value%"
                        , Aliases.BOOLEAN.getName(value));
                if (flag.defaultValue() != value)
                    meta.addEnchant(Enchantment.DURABILITY, 1, true);
                meta.addItemFlags(ItemFlag.values());
                item.setItemMeta(meta);
                this.inventory.setItem(i, item);
            }
        }

        @Override
        public @NotNull Inventory getInventory() {
            return this.inventory;
        }

        @Override
        public Player getTargetPlayer() {
            return target;
        }

        @Override
        public @NotNull APlugin getPlugin() {
            return ItemTag.get();
        }
    }
}