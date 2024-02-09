package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public abstract class EnumValueConditionType<E extends Enum<E>> extends StringValueConditionType {

    public EnumValueConditionType(@NotNull String id, @Nullable Class<? extends Event> clazz, @NotNull Class<E> enumClass) {
        super(id, clazz, ";", true, (String test) -> {
            try {
                Enum.valueOf(enumClass, test.toUpperCase(Locale.ENGLISH));
                return true;
            } catch (Exception ignored) {
            }
            return false;
        });
    }

    protected abstract E getCurrentEnumValue(@NotNull Player player, @NotNull ItemStack item, Event event);

    protected String getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event) {
        return getCurrentEnumValue(player, item, event).toString();
    }

}
