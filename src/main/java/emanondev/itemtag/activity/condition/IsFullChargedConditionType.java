package emanondev.itemtag.activity.condition;

import emanondev.itemtag.activity.ConditionType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class IsFullChargedConditionType extends ConditionType{

    public IsFullChargedConditionType() {
        super("is_full_charged", EntityDamageByEntityEvent.class);
    }

    @Override
    public @NotNull Condition read(@NotNull String info, boolean reversed) {
        return new PvpCondition(info,reversed);
    }

    private class PvpCondition extends Condition {

        public PvpCondition(@NotNull String info, boolean reversed) {
            super(info, reversed);
            if (!info.isEmpty() && !info.equals(" "))
                throw new IllegalArgumentException("Invalid format: '"+getInfo()+"' must be empty");
        }

        @Override
        protected boolean evaluateImpl(@NotNull Player player, @NotNull ItemStack item, Event event) {
            if (!(event instanceof EntityDamageByEntityEvent))
                return false;
            EntityDamageByEntityEvent evt = (EntityDamageByEntityEvent) event;
            if (!(evt.getDamager() instanceof HumanEntity))
                return true;

            HumanEntity human = (HumanEntity) evt.getDamager();
            return human.getAttackCooldown()==1;
        }
    }
}
