package emanondev.itemtag.actions;

import emanondev.itemedit.aliases.Aliases;
import emanondev.itemedit.utility.CompleteUtility;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SoundAction extends Action {

    public SoundAction() {
        super("sound");
    }

    @Override
    public void validateInfo(String text) {
        if (text.isEmpty()) {
            throw new IllegalStateException();
        }
        String[] args = text.split(" ");
        if (args.length > 4) {
            throw new IllegalStateException();
        }
        resolveSound(args[0]);
        if (args.length >= 2) {
            if (Float.parseFloat(args[1]) <= 0) {
                throw new IllegalStateException();
            }
        }
        if (args.length >= 3) {
            if (Float.parseFloat(args[2]) <= 0) {
                throw new IllegalStateException();
            }
        }
        if (args.length == 4) {
            switch (args[3].toLowerCase(Locale.ENGLISH)) {
                case "true":
                case "false":
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
    }

    @Override
    public void execute(Player player, String text) {
        String[] args = text.split(" ");
        boolean self = false;
        float volume = 1;
        float pitch = 1;
        // Actions are stored with the canonical enum name; do not resolve aliases at execution time.
        Sound sound = Sound.valueOf(args[0].toUpperCase(Locale.ENGLISH));
        if (args.length >= 2) {
            volume = Float.parseFloat(args[1]);
        }
        if (args.length >= 3) {
            pitch = Float.parseFloat(args[2]);
        }
        if (args.length >= 4) {
            self = Boolean.parseBoolean(args[3]);
        }
        if (self) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        } else {
            player.getLocation().getWorld().playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    @Override
    public String fixActionInfo(String actionInfo) {
        validateInfo(actionInfo);
        String[] args = actionInfo.split(" ");
        args[0] = resolveSound(args[0]).name();
        return String.join(" ", args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, List<String> params) {
        return switch (params.size()) {
            case 1 -> {
                if (Aliases.SOUND != null) {
                    yield CompleteUtility.complete(params.getFirst(), Aliases.SOUND);
                }
                yield List.of();
            }
            case 2 -> CompleteUtility.complete(params.get(1), Arrays.asList("0.5", "1"));
            case 3 -> CompleteUtility.complete(params.get(2), Arrays.asList("1", "2", "5", "10"));
            case 4 -> CompleteUtility.complete(params.get(3), Arrays.asList("true", "false"));
            default -> List.of();
        };
    }

    @Override
    public List<String> getInfo() {
        ArrayList<String> list = new ArrayList<>();
        list.add("<aqua>" + getID() + " <yellow><sound> [volume] [pitch] [self]");
        list.add("<yellow><sound> <aqua>the sound to play");
        list.add("<yellow>[volume] <aqua>the volume of the sound, default 1");
        list.add("<yellow>[pitch] <aqua>the pitch of the sound, default 1");
        list.add("<yellow>[self] <aqua>only player sould heard?, by default false");
        return list;
    }

    private static Sound resolveSound(String input) {
        Sound sound = Aliases.SOUND.convertAlias(input);
        if (sound != null) {
            return sound;
        }

        return Sound.valueOf(input.toUpperCase(Locale.ENGLISH));
    }
}
