package emanondev.itemtag.activity.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StringListArgument extends Argument {

    private final List<String> values = new ArrayList<>();
    private final String separator;


    public StringListArgument(Collection<String> values) {
        this(values, ";");
    }

    public StringListArgument(@NotNull Collection<String> values, @NotNull String separator) {
        this.values.addAll(values);
        this.separator = separator;
    }

    public StringListArgument(@Nullable String info) {
        this(info, ";");
    }

    public StringListArgument(@Nullable String info, @NotNull String separator) {
        if (info != null)
            for (String value : info.split(separator))
                if (!value.isEmpty())
                    this.values.add(value);
        this.separator = separator;
    }

    public boolean contains(String value) {
        return values.contains(value);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public List<String> getValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (String arg : values) {
            if (b.length() != 0)
                b.append(separator);
            b.append(arg).append(separator);
        }
        return b.toString();
    }
}
