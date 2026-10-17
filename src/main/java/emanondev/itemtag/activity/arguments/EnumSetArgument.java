package emanondev.itemtag.activity.arguments;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Locale;

@Slf4j
public class EnumSetArgument<E extends Enum<E>> extends Argument {
    private final EnumSet<E> values;
    private final String separator;
    private final Class<E> clazz;


    public EnumSetArgument(String info, @NotNull Class<E> clazz) {
        this(info, ";", clazz);
    }

    public EnumSetArgument(String info, String separator, @NotNull Class<E> clazz) {
        boolean reversed = info.startsWith("!");
        this.separator = separator;
        this.clazz = clazz;
        if (reversed)
            info = info.substring(1);
        if (reversed)
            values = EnumSet.allOf(clazz);
        else
            values = EnumSet.noneOf(clazz);
        for (String arg : info.split(separator))
            try {
                values.add(Enum.valueOf(clazz, arg));
            } catch (Exception e) {
                try {
                    values.add(Enum.valueOf(clazz, arg.toUpperCase(Locale.ENGLISH)));
                } catch (Exception e2) {
                    log.warn("Error in EnumSetArgument", e2);
                }
            }
    }

    public boolean contains(E value) {
        return values.contains(value);
    }

    public String toString() {
        StringBuilder standard = new StringBuilder();
        StringBuilder reversed = new StringBuilder("!");
        for (E e : clazz.getEnumConstants()) {
            if (values.contains(e)) {
                if (!standard.isEmpty()) {
                    standard.append(separator);
                }
                standard.append(e.name());
            } else {
                if (reversed.length() != 1) {
                    reversed.append(separator);
                }
                reversed.append(e.name());
            }
        }
        return (standard.length() <= reversed.length() ? standard : reversed).toString();
    }
}
