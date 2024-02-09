package emanondev.itemtag.activity;

import emanondev.itemtag.ItemTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public abstract class Manager<T extends Factory, S extends Factory.Element> {

    private final String name;
    private final HashMap<String, T> types = new HashMap<>();

    public Manager(String name) {
        this.name = name;
    }

    public void register(@NotNull T factory) {
        String id = factory.getId();
        if (types.containsKey(id))
            throw new IllegalArgumentException();
        types.put(id, factory);
        ItemTag.get().log(name + "Manager registered " + name + " Type &e" + factory.getId());
    }

    public void unregister(@NotNull T factory) {
        String id = factory.getId();
        types.remove(id);
        ItemTag.get().log(name + "Manager unregistered " + name + " Type &e" + factory.getId());
    }

    public abstract void load();

    /**
     * accepts reversed id (like both permission and !permission
     */
    public @Nullable T getType(@NotNull String id) {
        return types.get(id.toLowerCase(Locale.ENGLISH));
    }

    public @NotNull Collection<String> getIds() {
        return Collections.unmodifiableSet(types.keySet());
    }
}
