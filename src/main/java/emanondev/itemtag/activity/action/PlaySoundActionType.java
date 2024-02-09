package emanondev.itemtag.activity.action;

import emanondev.itemtag.activity.ActionType;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class PlaySoundActionType extends ActionType {
    public PlaySoundActionType() {
        super("playsound");
    }

    @Override
    public @NotNull ActionType.Action read(@NotNull String info) {
        return new PlaySoundActionType.Action(info);
    }

    private class Action extends ActionType.Action {
        private final Sound sound;
        private final float volume;
        private final float pitch;
        private final boolean self;
        private final SoundCategory category;


        public Action(@NotNull String info) {
            super(info);
            String[] values = info.split(" ");
            if (values.length == 0)
                throw new IllegalArgumentException("Invalid format '" + info + "' must be '<sound> [volume] [pitch] [category]'");
            try {
                sound = Sound.valueOf(values[0].toUpperCase(Locale.ENGLISH));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid sound '" + values[0] + "' must be '<sound> [volume] [pitch] [category]' you can find sound here https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html");
            }
            try {
                volume = values.length <= 1 ? 1 : Float.parseFloat(values[1]);
                if (volume <= 0F)
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid volume '" + values[1] + "' must be '<sound> [volume] [pitch] [category]' volume must be bigger than 0");
            }
            try {
                pitch = values.length <= 2 ? 1 : Float.parseFloat(values[2]);
                if (pitch <= 0F)
                    throw new IllegalArgumentException();
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid pitch '" + values[2] + "' must be '<sound> [volume] [pitch] [category]' pitch must be bigger than 0.2 and lower than 2");
            }
            self = values.length > 3 && Boolean.parseBoolean(values[3]); //TODO notify value error
            try {
                category = values.length <= 4 ? SoundCategory.MASTER : SoundCategory.valueOf(values[4].toLowerCase(Locale.ENGLISH));
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid sound category '" + values[4] + "' must be '<sound> [volume] [pitch] [category]' you can find sound here https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/SoundCategory.html");
            }
        }

        @Override
        public boolean execute(@NotNull Player player, @NotNull ItemStack item, Event event) {
            if (self)
                player.playSound(player.getLocation(), sound, category, volume, pitch);
            else
                player.getWorld().playSound(player.getLocation(), sound, category, volume, pitch);
            return true;
        }
    }
}
