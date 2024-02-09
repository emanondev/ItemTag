package emanondev.itemtag.activity.condition;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsPvpConditionType extends BooleanValueConditionType {

    public IsPvpConditionType() {
        super("is_pvp", EntityDamageEvent.class);
    }

    @Override
    public boolean getCurrentValue(@NotNull Player player, @NotNull ItemStack item, Event event) {
        if (!(event instanceof EntityDamageByEntityEvent))
            return false;
        EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event;
        if (evt.getEntity() instanceof Player)
            return false;
        if (evt.getDamager() instanceof Projectile) {
            Projectile prj = (Projectile) evt.getDamager();
            return prj.getShooter() instanceof Player;
        }
        if (evt.getDamager() instanceof TNTPrimed) {
            TNTPrimed tnt = (TNTPrimed) evt.getDamager();
            return tnt.getSource() instanceof Player;
        }
        return evt.getDamager() instanceof Player;
    }
}
