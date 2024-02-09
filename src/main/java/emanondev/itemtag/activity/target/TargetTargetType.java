package emanondev.itemtag.activity.target;


import emanondev.itemtag.ItemTag;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TargetTargetType extends TargetType {

    public TargetTargetType() {
        super(TargetManager.TARGET_TARGET);
    }

    @Override
    public @NotNull Target read(@Nullable String info) {
        return new Target(info);
    }


    //@subtarget <range> [entitytype]
    @Override
    protected @NotNull List<Object> defaultGetTargets(@Nullable String info, @NotNull HashMap<String, Target> baseTargets) {
        ArrayList<Object> values = new ArrayList<>();
        double radius = 32;
        EnumSet<EntityType> valid = EnumSet.allOf(EntityType.class);
        Target targets = null;
        if (info != null) {
            String target = TargetManager.extractTarget(info);
            if (target != null) {
                info = info.replace(target, "");
                if (info.startsWith(" "))
                    info = info.substring(1);
                targets = ItemTag.get().getTargetManager().read(target, baseTargets);
            }
        }
        if (targets == null)
            targets = getFirstAvailable(baseTargets, Arrays.asList(TargetManager.ENTITY_TARGET, TargetManager.PLAYER_TARGET));
        if (targets == null)
            throw new IllegalArgumentException();
        if (info != null && !info.isEmpty()) {
            String[] arguments = info.split(" ");
            if (arguments.length > 0)
                radius = Double.parseDouble(arguments[0]);
            if (arguments.length > 1) {
                valid = EnumSet.complementOf(valid);

            }
        }
        for (Object target : targets.getTargets(baseTargets)) {
            if (target instanceof Player) {
                EnumSet<EntityType> finalValid = valid;
                ((Player) target).getWorld().rayTraceEntities(((Player) target).getEyeLocation(),
                        ((Player) target).getEyeLocation().getDirection(), radius, (e) -> (e != target && finalValid.contains(e.getType())));
                continue;
            }
            if (target instanceof Mob) {
                values.add(((Mob) target).getTarget());
            }
        }
        return values;
    }
}
