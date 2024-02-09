package emanondev.itemtag.activity.target;

import emanondev.itemtag.ItemTag;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class LocationTargetType extends TargetType {

    public LocationTargetType() {
        super(TargetManager.LOCATION_TARGET);
    }

    @Override
    public TargetType.@NotNull Target read(@Nullable String info) {
        return new Target(info);
    }

    @Override
    protected @NotNull List<Object> defaultGetTargets(@Nullable String info, @NotNull HashMap<String, Target> baseTargets) {
        ArrayList<Object> values = new ArrayList<>();
        Target targets;
        if (info != null)
            targets = ItemTag.get().getTargetManager().read(info, baseTargets);
        else
            targets = getFirstAvailable(baseTargets, Arrays.asList(getId(), TargetManager.ENTITY_TARGET, TargetManager.PROJECTILE_TARGET,
                    TargetManager.BLOCK_TARGET, TargetManager.PLAYER_TARGET));
        if (targets == null)
            throw new IllegalArgumentException();
        for (Object target : targets.getTargets(baseTargets)) {
            if (target instanceof Location) {
                values.add(target);
                continue;
            }
            if (target instanceof Entity) {
                values.add(((Entity) target).getLocation());
                continue;
            }
            if (target instanceof Block) {
                values.add(((Block) target).getLocation().add(0.5, 0, 0.5));
                continue;
            }
        }
        return values;
    }
}
