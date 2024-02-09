package emanondev.itemtag.activity.target;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class PrimitiveTargetType extends TargetType {


    public PrimitiveTargetType(String id) {
        super(id);
    }

    @Override
    protected @NotNull List<Object> defaultGetTargets(@Nullable String info, @NotNull HashMap<String, Target> baseTargets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TargetType.@NotNull Target read(@Nullable String info) {
        return new PrimitiveTarget();
    }

    public PrimitiveTargetType.@NotNull PrimitiveTarget create(@NotNull Object value) {
        return new PrimitiveTarget(value);
    }

    public PrimitiveTargetType.@NotNull PrimitiveTarget create(@NotNull Collection<Object> values) {
        return new PrimitiveTarget(values);
    }

    private final class PrimitiveTarget extends Target {

        private final List<Object> values;

        private PrimitiveTarget() {
            super(null);
            values = null;
        }

        private PrimitiveTarget(Object value) {
            super(null);
            values = new ArrayList<>();
            if (value != null)
                this.values.add(value);
        }

        private PrimitiveTarget(Collection<Object> values) {
            super(null);
            this.values = new ArrayList<>();
            for (Object value : values)
                if (value != null)
                    this.values.add(value);
        }

        @Override
        public @NotNull List<Object> getTargets(@NotNull HashMap<String, Target> baseTargets) {
            if (values == null) {
                if (baseTargets.containsKey(getId()))
                    return baseTargets.get(getId()).getTargets(baseTargets);
                throw new IllegalArgumentException();
            }
            return Collections.unmodifiableList(values);
        }
    }
}