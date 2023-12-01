package emanondev.itemtag.activity;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;


public abstract class ConditionType {

    private final String id;
    private final Class<? extends Event> clazz;

    public ConditionType(@NotNull String id){
        this(id,null);
    }

    public ConditionType(@NotNull String id, @Nullable Class<? extends Event> clazz){
        if (!Pattern.compile("[a-z][_a-z0-9]*").matcher(id).matches())
            throw new IllegalArgumentException();
        this.id = id;
        this.clazz = clazz;
    }

    public final @NotNull Condition read(@NotNull String info){
        return read(info,false);
    }

    public abstract @NotNull Condition read(@NotNull String info,boolean reversed);

    public final @NotNull String getId() {
        return this.id;
    }

    public abstract class Condition {

        private final String info;
        private final boolean reversed;

        public Condition(@NotNull String info,boolean reversed){
            this.info = info;
            this.reversed = reversed;
        }

        public final @NotNull String getInfo() {
            return info;
        }

        public final @NotNull String getId(){
            return ConditionType.this.getId();
        }

        public @NotNull String toString(){
            return (reversed?"":"!")+getId()+(getInfo().isEmpty()?"":(" "+getInfo()));
        }

        public boolean evaluate(@NotNull Player player,@NotNull ItemStack item, @Nullable Event event){
            return reversed != evaluateImpl(player,item, event);
        }

        protected abstract boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, @Nullable Event event);

        public boolean isCompatible(@Nullable Event event){
            if (clazz == null)
                return true;
            return event!=null && clazz.isAssignableFrom(event.getClass());
        }
    }
}
