package emanondev.itemtag.activity.target;

import emanondev.itemtag.activity.Factory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public abstract class TargetType extends Factory {

    public TargetType(String id) {
        super(id);
    }

    public abstract @NotNull Target read(@Nullable String info);

    protected abstract @NotNull List<Object> defaultGetTargets(@Nullable String info, @NotNull HashMap<String, Target> baseTargets);

    protected @Nullable Target getFirstAvailable(@NotNull HashMap<String, Target> baseTargets, @NotNull List<String> values) {
        for (String key : values) {
            if (baseTargets.containsKey(key))
                return baseTargets.get(key);
        }
        return null;
    }

    public class Target extends Factory.Element {

        public Target(@Nullable String info) {
            super(info);
        }

        public @NotNull List<Object> getTargets(@NotNull HashMap<String, Target> baseTargets) {
            return defaultGetTargets(getInfo(), baseTargets);
        }

        public @NotNull String toString() {
            return "@" + getId() + (getInfo() == null ? "" : ("(" + getInfo() + ")"));
        }
    }
}
