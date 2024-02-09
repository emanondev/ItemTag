package emanondev.itemtag.activity.target;


import emanondev.itemtag.ItemTag;
import org.bukkit.entity.*;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NearTargetType extends TargetType {

    public NearTargetType() {
        super(TargetManager.OWNER_TARGET);
    }

    @Override
    public @NotNull Target read(@Nullable String info) {
        return new Target(info);
    }

    @Override
    protected @NotNull List<Object> defaultGetTargets(@Nullable String info, @NotNull HashMap<String, Target> baseTargets) {
        ArrayList<Object> values = new ArrayList<>();
        Target targets;
        if (info != null)
            targets = ItemTag.get().getTargetManager().read(info, baseTargets);
        else
            targets = getFirstAvailable(baseTargets, Arrays.asList(getId(), TargetManager.ENTITY_TARGET, TargetManager.PROJECTILE_TARGET));
        if (targets == null)
            throw new IllegalArgumentException();
        for (Object target : targets.getTargets(baseTargets)) {
            if (target instanceof Tameable) {
                AnimalTamer owner = ((Tameable) target).getOwner();
                if (owner instanceof Entity) {
                    if (owner instanceof Player && !((Player) owner).isOnline())
                        continue;
                    values.add(owner);
                }
                continue;
            }
            if (target instanceof Projectile) {
                ProjectileSource shooter = ((Projectile) target).getShooter();
                if (shooter instanceof LivingEntity) {
                    if (shooter instanceof Player && !((Player) shooter).isOnline())
                        continue;
                    values.add(shooter);
                }
                if (shooter instanceof BlockProjectileSource)
                    values.add(((BlockProjectileSource) shooter).getBlock());
            }
        }
        return values;
    }
}
