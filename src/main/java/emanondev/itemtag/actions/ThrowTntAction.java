package emanondev.itemtag.actions;

import emanondev.itemtag.ItemTag;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/** Throws primed TNT in the direction the player is looking. */
public class ThrowTntAction extends Action {

    private static final double DEFAULT_SPEED = 2.0;
    private static final double DEFAULT_IMPRECISION = 0.0;
    private static final int DEFAULT_FUSE_TICKS = 60;
    private static final int DEFAULT_REPEAT = 0;
    private static final int DEFAULT_REPEAT_DELAY = 5;

    public ThrowTntAction() {
        super("throwtnt", ItemTag.get());
    }

    @Override
    public void validateInfo(String text) {
        String[] args = arguments(text);
        if (args.length > 5) {
            throw new IllegalArgumentException("Expected: [speed] [imprecision] [fuseTicks] [repeat] [repeatdelay]");
        }
        if (args.length >= 1) {
            validateNonNegativeFinite(args[0], "Speed");
        }
        if (args.length >= 2) {
            validateNonNegativeFinite(args[1], "Imprecision");
        }
        if (args.length >= 3 && Integer.parseInt(args[2]) < 1) {
            throw new IllegalArgumentException("Fuse ticks must be at least 1");
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
        double speed = args.length >= 1 ? Double.parseDouble(args[0]) : DEFAULT_SPEED;
        float imprecision = (float) (args.length >= 2 ? Double.parseDouble(args[1]) : DEFAULT_IMPRECISION);
        int fuseTicks = args.length >= 3 ? Integer.parseInt(args[2]) : DEFAULT_FUSE_TICKS;
        int repeat = args.length >= 4 ? Integer.parseInt(args[3]) : DEFAULT_REPEAT;
        int repeatDelay = args.length >= 5 ? Integer.parseInt(args[4]) : DEFAULT_REPEAT_DELAY;

        throwTnt(player, speed, imprecision, fuseTicks);
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
                throwTnt(player, speed, imprecision, fuseTicks);
                if (++repeated >= repeat) {
                    cancel();
                }
            }
        }.runTaskTimer(ItemTag.get(), repeatDelay, repeatDelay);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        int index = params.size() - 1;
        String current = index >= 0 ? params.get(index) : "";
        return switch (index) {
            case 0 -> CompleteUtility.complete(current, List.of("0.5", "1", "2"));
            case 1 -> CompleteUtility.complete(current, List.of("0", "0.1", "0.25", "0.5"));
            case 2 -> CompleteUtility.complete(current, List.of("20", "40", "60", "80"));
            case 3 -> CompleteUtility.complete(current, List.of("0", "1", "2", "3"));
            case 4 -> CompleteUtility.complete(current, List.of("1", "5", "10", "20"));
            default -> List.of();
        };
    }


    private static void throwTnt(Player player, double speed, float imprecision, int fuseTicks) {
        Location launchLocation = player.getEyeLocation();
        launchLocation.setDirection(player.getLocation().getDirection());
        if (imprecision > 0) {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            launchLocation.setYaw(launchLocation.getYaw() + random.nextFloat(-imprecision, imprecision));
            launchLocation.setPitch(launchLocation.getPitch() + random.nextFloat(-imprecision, imprecision));
        }
        Vector velocity = launchLocation.getDirection().normalize().multiply(speed);
        player.getWorld().spawnEntity(launchLocation, EntityType.TNT, CreatureSpawnEvent.SpawnReason.CUSTOM,
                entity -> {
                    TNTPrimed tnt = (TNTPrimed) entity;
                    tnt.setVelocity(velocity);
                    tnt.setFuseTicks(fuseTicks);
                    tnt.setSource(player);
                });
    }

    private static void validateNonNegativeFinite(String text, String label) {
        double value = Double.parseDouble(text);
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(label + " must be a finite non-negative number");
        }
    }

    private static String[] arguments(String text) {
        if (text == null || text.isBlank()) {
            return new String[0];
        }
        return text.trim().split("\\s+");
    }
}
