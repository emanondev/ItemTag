package emanondev.itemtag.activity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public abstract class Factory {

    private final String id;

    public Factory(String id) {
        if (!Pattern.compile("[a-z][_a-z0-9]*").matcher(id).matches())
            throw new IllegalArgumentException();
        this.id = id;
    }

    public final @NotNull String getId() {
        return this.id;
    }

    public class Element {
        private final String info;

        public Element(@Nullable String info) {
            this.info = info;
        }

        public final @Nullable String getInfo() {
            return info;
        }

        public final @NotNull String getId() {
            return Factory.this.getId();
        }
    }
}
