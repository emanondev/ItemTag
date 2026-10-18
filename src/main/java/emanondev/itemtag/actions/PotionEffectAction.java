package emanondev.itemtag.actions;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Gives the player a potion effect when the action is triggered.
 */
public class PotionEffectAction extends Action {

    public PotionEffectAction() {
        super("potion");
    }

    @Override
    public void validateInfo(String text) {
        String[] args = arguments(text);
        if (args.length < 3 || args.length > 6) {
            throw new IllegalArgumentException("Expected: <type> <duration_ticks|infinite> <level> [ambient] [particles] [icon]");
        }
        if (resolveType(args[0]) == null) {
            throw new IllegalArgumentException("Unknown potion effect");
        }
        parseDuration(args[1]);
        int level = Integer.parseInt(args[2]);
        if (level < 1 || level > 256) {
            throw new IllegalArgumentException("Level must be between 1 and 256");
        }
        for (int i = 3; i < args.length; i++) {
            if (Aliases.BOOLEAN.convertAlias(args[i]) == null) {
                throw new IllegalArgumentException("Expected true or false");
            }
        }
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = arguments(text);
        PotionEffectType type = Objects.requireNonNull(resolveType(args[0]), "Unknown stored potion effect: " + args[0]);
        int duration = parseDuration(args[1]);
        int amplifier = Integer.parseInt(args[2]) - 1;
        boolean ambient = args.length >= 4 && Boolean.parseBoolean(args[3]);
        boolean particles = args.length < 5 || Boolean.parseBoolean(args[4]);
        boolean icon = args.length < 6 || Boolean.parseBoolean(args[5]);

        player.addPotionEffect(new PotionEffect(type, duration, amplifier, ambient, particles, icon), true);
    }

    @Override
    public String fixActionInfo(String actionInfo) {
        validateInfo(actionInfo);
        String[] args = arguments(actionInfo);
        PotionEffectType type = resolveType(args[0]);
        args[0] = type.getKey().toString();
        for (int i = 3; i < args.length; i++) {
            args[i] = Boolean.toString(Aliases.BOOLEAN.convertAlias(args[i]));
        }
        return String.join(" ", args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        int index = params.size() - 1;
        String current = index >= 0 ? params.get(index) : "";
        return switch (index) {
            case -1, 0 -> CompleteUtility.complete(current, Aliases.POTION_EFFECT);
            case 1 -> CompleteUtility.complete(current, List.of("20", "100", "200", "infinite"));
            case 2 -> CompleteUtility.complete(current, List.of("1", "2", "3", "4"));
            case 3, 4, 5 -> CompleteUtility.complete(current, List.of("true", "false"));
            default -> List.of();
        };
    }

    @Override
    public List<String> getInfo() {
        List<String> info = new ArrayList<>();
        info.add("<aqua>" + getID() + " <yellow><type> <duration_ticks|infinite> <level> [ambient] [particles] [icon]");
        info.add("<yellow><type> <aqua>the potion effect to apply");
        info.add("<yellow><duration_ticks|infinite> <aqua>effect duration in ticks, or infinite");
        info.add("<yellow><level> <aqua>effect level from 1 to 256");
        info.add("<yellow>[ambient] [particles] [icon] <aqua>booleans; defaults are false, true, true");
        return info;
    }

    private static String[] arguments(String text) {
        if (text == null || text.isBlank()) {
            return new String[0];
        }
        return text.trim().split("\\s+");
    }

    private static int parseDuration(String text) {
        if ("infinite".equalsIgnoreCase(text)) {
            return PotionEffect.INFINITE_DURATION;
        }
        int duration = Integer.parseInt(text);
        if (duration <= 0) {
            throw new IllegalArgumentException("Duration must be positive");
        }
        return duration;
    }

    private static PotionEffectType resolveType(String input) {
        NamespacedKey key = NamespacedKey.fromString(input);
        PotionEffectType type = key == null ? null : Registry.EFFECT.get(key);
        return type != null ? type : Aliases.POTION_EFFECT.convertAlias(input);
    }
}
