package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Locale;
import java.util.function.Predicate;

public abstract class StringValueConditionType extends ConditionType {

    private final String separator;
    private final boolean caseInsensitive;
    private final Predicate<String> validator;

    public StringValueConditionType(@NotNull String id, @Nullable Class<? extends Event> clazz, boolean caseInsensitive) {
        this(id, clazz, ";", caseInsensitive, null);
    }

    public StringValueConditionType(@NotNull String id, @Nullable Class<? extends Event> clazz,
                                    @Nullable String separator, boolean caseInsensitive, Predicate<String> validator) {
        super(id, clazz);
        this.separator = separator;
        this.caseInsensitive = caseInsensitive;
        this.validator = validator;
    }

    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new StringCondition(info, reversed);
    }

    protected abstract String getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event);

    private class StringCondition extends Condition {

        private final HashSet<String> values = new HashSet<>();

        public StringCondition(@NotNull String info, boolean reversed) {
            super(info, reversed);
            String valuesRaw = info.split(" ")[0]; //?
            if (separator == null) {
                if (validator != null && !validator.test(valuesRaw))
                    throw new IllegalArgumentException();
                if (caseInsensitive)
                    values.add(valuesRaw.toLowerCase(Locale.ENGLISH));
                else
                    values.add(valuesRaw);
            } else
                for (String value : valuesRaw.split(separator)) {
                    if (validator != null && !validator.test(valuesRaw))
                        throw new IllegalArgumentException();
                    if (caseInsensitive)
                        values.add(value.toLowerCase(Locale.ENGLISH));
                    else
                        values.add(value);
                }
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            return values.contains(caseInsensitive ? getCurrentValue(player, item, event).toLowerCase(Locale.ENGLISH) : getCurrentValue(player, item, event));
        }
    }
}
