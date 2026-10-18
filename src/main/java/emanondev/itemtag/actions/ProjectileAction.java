package emanondev.itemtag.actions;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemtag.ItemTag;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fires one or more projectiles in the direction the player is looking.
 */
@Slf4j
public class ProjectileAction extends Action {

    private static final double DEFAULT_SPEED = 2.0;
    private static final double DEFAULT_IMPRECISION = 0.0;
    private static final int DEFAULT_REPEAT = 0;
    private static final int DEFAULT_REPEAT_DELAY = 5;

    public ProjectileAction() {
        super("projectile", ItemTag.get());
    }

    @Override
    public void validateInfo(String text) {
        String[] args = arguments(text);
        if (args.length < 1 || args.length > 5) {
            throw new IllegalArgumentException("Expected: <projectile> [speed] [imprecision] [repeat] [repeatdelay]");
        }
        if (resolveProjectile(args[0]) == null) {
            throw new IllegalArgumentException("Entity must be a spawnable projectile");
        }
        if (args.length >= 2) {
            validateNonNegativeFinite(args[1], "Speed");
        }
        if (args.length >= 3) {
            validateNonNegativeFinite(args[2], "Imprecision");
        }
        if (args.length >= 4 && Integer.parseInt(args[3]) < 0) {
            throw new IllegalArgumentException("Repeat must not be negative");
        }
        if (args.length >= 5 && Integer.parseInt(args[4]) < 1) {
            throw new IllegalArgumentException("Repeat delay must be at least 1 tick");
        }
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = arguments(text);
        EntityType type = EntityType.valueOf(args[0].toUpperCase(Locale.ENGLISH));
        double speed = args.length >= 2 ? Double.parseDouble(args[1]) : DEFAULT_SPEED;
        double imprecision = args.length >= 3 ? Double.parseDouble(args[2]) : DEFAULT_IMPRECISION;
        int repeat = args.length >= 4 ? Integer.parseInt(args[3]) : DEFAULT_REPEAT;
        int repeatDelay = args.length >= 5 ? Integer.parseInt(args[4]) : DEFAULT_REPEAT_DELAY;

        fire(player, type, speed, (float) imprecision);
        if (repeat == 0) {
            return;
        }

        new BukkitRunnable() {
            private int repeated;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead()) {
                    cancel();
                    return;
                }
                fire(player, type, speed, (float) imprecision);
                if (++repeated >= repeat) {
                    cancel();
                }
            }
        }.runTaskTimer(ItemTag.get(), repeatDelay, repeatDelay);
    }

    @Override
    public String fixActionInfo(String actionInfo) {
        validateInfo(actionInfo);
        String[] args = arguments(actionInfo);
        args[0] = resolveProjectile(args[0]).name();
        return String.join(" ", args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        int index = params.size() - 1;
        String current = index >= 0 ? params.get(index) : "";
        return switch (index) {
            case -1, 0 -> CompleteUtility.complete(current, Aliases.ENTITY_TYPE,
                    type -> type.isSpawnable() && isProjectile(type));
            case 1 -> CompleteUtility.complete(current, List.of("0.5", "1", "2"));
            case 2 -> CompleteUtility.complete(current, List.of("0", "0.1", "0.25", "0.5"));
            case 3 -> CompleteUtility.complete(current, List.of("0", "1", "2", "3"));
            case 4 -> CompleteUtility.complete(current, List.of("1", "5", "10", "20"));
            default -> List.of();
        };
    }


    private static void fire(Player player, EntityType type, double speed, float imprecision) {
        Location launchLocation = player.getEyeLocation();
        launchLocation.setDirection(player.getLocation().getDirection());

        if (imprecision > 0) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            float yaw = launchLocation.getYaw() + random.nextFloat(-imprecision, imprecision);
            float pitch = launchLocation.getPitch() + random.nextFloat(-imprecision, imprecision);

            launchLocation.setYaw(yaw);
            launchLocation.setPitch(pitch);
        }
        Vector direction = launchLocation.getDirection().normalize();
        Vector velocity = direction.multiply(speed);
        Projectile projectile = player.launchProjectile((Class<? extends Projectile>) type.getEntityClass(), velocity);
        if (projectile instanceof AbstractArrow arrow){
            arrow.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
        }
    }

    private static void validateNonNegativeFinite(String text, String label) {
        double value = Double.parseDouble(text);
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(label + " must be a finite non-negative number");
        }
    }

    private static boolean isProjectile(EntityType type) {
        Class<?> entityClass = type.getEntityClass();
        return entityClass != null && Projectile.class.isAssignableFrom(entityClass);
    }

    private static EntityType resolveProjectile(String input) {
        EntityType type = Aliases.ENTITY_TYPE.convertAlias(input);
        if (type == null || !type.isSpawnable() || !isProjectile(type)) {
            return null;
        }
        return type;
    }

    private static String[] arguments(String text) {
        if (text == null || text.isBlank()) {
            return new String[0];
        }
        return text.trim().split("\\s+");
    }
}
