package emanondev.itemtag.actions;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.CompleteUtility;
import emanondev.itemtag.ItemTag;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Spawns one or more entities in front of the player when the action is triggered. */
public class SummonAction extends Action {

    private static final double DEFAULT_SPREAD = 1.5;

    public SummonAction() {
        super("summon", ItemTag.get());
    }

    @Override
    public void validateInfo(String text) {
        String[] args = arguments(text);
        if (args.length < 1 || args.length > 3) {
            throw new IllegalArgumentException("Expected: <entity> [quantity] [spread]");
        }
        if (resolveEntity(args[0]) == null) {
            throw new IllegalArgumentException("Entity must be spawnable");
        }
        if (args.length >= 2 && Integer.parseInt(args[1]) < 1) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (args.length == 3) {
            double spread = Double.parseDouble(args[2]);
            if (!Double.isFinite(spread) || spread < 0) {
                throw new IllegalArgumentException("Spread must be a finite non-negative number");
            }
        }
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = arguments(text);
        EntityType type = EntityType.valueOf(args[0].toUpperCase(Locale.ENGLISH));
        int quantity = args.length >= 2 ? Integer.parseInt(args[1]) : 1;
        double spread = args.length >= 3 ? Double.parseDouble(args[2]) : DEFAULT_SPREAD;
        Location origin = player.getLocation();
        World world = player.getWorld();
        double yaw = Math.toRadians(origin.getYaw());

        for (int i = 0; i < quantity; i++) {
            double angle;
            if (quantity == 2) {
                angle = i == 0 ? -60 : 60;
            } else if (quantity == 1) {
                angle = 0;
            } else {
                angle = 360.0 * i / quantity;
            }

            double direction = yaw + Math.toRadians(angle);
            Location spawnLocation = origin.clone().add(
                    -Math.sin(direction) * spread,
                    0,
                    Math.cos(direction) * spread);
            world.spawnEntity(spawnLocation, type);
        }
    }

    @Override
    public String fixActionInfo(String actionInfo) {
        validateInfo(actionInfo);
        String[] args = arguments(actionInfo);
        args[0] = resolveEntity(args[0]).name();
        return String.join(" ", args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        int index = params.size() - 1;
        String current = index >= 0 ? params.get(index) : "";
        return switch (index) {
            case -1, 0 -> CompleteUtility.complete(current, Aliases.ENTITY_TYPE, EntityType::isSpawnable);
            case 1 -> CompleteUtility.complete(current, List.of("1", "2", "3", "4", "5"));
            case 2 -> CompleteUtility.complete(current, List.of("1", "1.5", "2", "3", "5"));
            default -> List.of();
        };
    }


    private static String[] arguments(String text) {
        if (text == null || text.isBlank()) {
            return new String[0];
        }
        return text.trim().split("\\s+");
    }

    private static EntityType resolveEntity(String input) {
        EntityType type = Aliases.ENTITY_TYPE.convertAlias(input);
        if (type == null || !type.isSpawnable()) {
            return null;
        }
        return type;
    }
}
