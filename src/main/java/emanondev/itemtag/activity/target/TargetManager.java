package emanondev.itemtag.activity.target;

import emanondev.itemtag.activity.Manager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Locale;

public class TargetManager extends Manager<TargetType, TargetType.Target> {

    public static final String OWNER_TARGET = "owner";
    public static final String TARGET_TARGET = "target";
    public static final String ENTITY_TARGET = "entity";
    public static final String BLOCK_TARGET = "block";
    public static final String EVENT_TARGET = "event";
    public static final String PROJECTILE_TARGET = "projectile";
    public static final String PLAYER_TARGET = "player";
    public static final String LOCATION_TARGET = "location";

    //private final HashMap<String, PrimitiveTargetType> primitives = new HashMap<>();

    public TargetManager() {
        super("Target");
    }

    /**
     * @return target ready to be read containing the @ and (arguments) if present
     * @throws IllegalArgumentException if the target is wrongly formatted
     * @see #read(String, HashMap)
     */
    public static @Nullable String extractTarget(@NotNull String line) throws IllegalArgumentException {
        if (!line.contains("@"))
            return null;
        int start = line.indexOf("@");
        int parenthesis = 0;
        int end = -1;
        for (int i = start + 1; i < line.length(); i++) {
            switch (line.charAt(i)) {
                case '(':
                    parenthesis++;
                    continue;
                case ')':
                    parenthesis--;
                    if (parenthesis < 0)
                        throw new IllegalArgumentException();
                    if (parenthesis == 0) {
                        end = i;
                        break;
                    }
                    continue;
                case ' ':
                    if (parenthesis == 0) {
                        if (i == start + 1)
                            throw new IllegalArgumentException();
                        end = i;
                        break;
                    }
                    continue;
                default:
                    continue;
            }
            break;
        }
        if (end == -1)
            throw new IllegalArgumentException();
        return line.substring(start, end + 1);
    }

    public void load() {
        /*register(new PrimitiveTargetType(ENTITY_TARGET));
        register(new PrimitiveTargetType(BLOCK_TARGET));
        register(new PrimitiveTargetType(EVENT_TARGET));
        register(new PrimitiveTargetType(PROJECTILE_TARGET));
        register(new PrimitiveTargetType(PLAYER_TARGET));*/
        register(new LocationTargetType());
        register(new OwnerTargetType());
    }

    /**
     * accepts both:
     * <ul><li>type</li>
     * <li>@type</li>
     * </ul>
     */
    public @Nullable TargetType getType(@NotNull String id) {
        return id.startsWith("@") ? super.getType(id.substring(1)) : super.getType(id);
    }

    /**
     * Accepts all the legal formats
     * examples:<br>
     * <ul>
     * <li>type</li>
     * <li>@type</li>
     * <li>type()</li>
     * <li>@type()</li>
     * <li>type(arguments)</li>
     * <li>@type(arguments)</li>
     * </ul>
     *
     * @see #extractTarget(String)
     */
    public @NotNull TargetType.Target read(@NotNull String target, @NotNull HashMap<String, TargetType.Target> baseTargets) throws IllegalArgumentException {
        if (target.startsWith("@"))
            target = target.substring(1);
        while (target.endsWith(" "))
            target = target.substring(0, target.length() - 1);
        if (target.contains("(")) {
            int parenthesis = 1;
            int finalIndex = -1;
            int startIndex = target.indexOf("(");
            for (int i = startIndex + 1; i < target.length(); i++) {
                if (target.charAt(i) == ('('))
                    parenthesis++;
                if (target.charAt(i) == (')'))
                    parenthesis--;
                if (parenthesis == 0) {
                    finalIndex = i;
                    break;
                }
            }
            if (finalIndex == -1)
                throw new IllegalArgumentException();
            String name = target.substring(0, startIndex);
            String info = target.substring(startIndex + 1, finalIndex);
            target = target.substring(startIndex + 1, finalIndex);
            TargetType type = getType(name);
            if (type != null)
                return type.read(info);
            return baseTargets.get(target.toLowerCase(Locale.ENGLISH));
        }
        if (target.contains(" "))
            throw new IllegalArgumentException();
        TargetType type = getType(target);
        if (type != null)
            return type.read(null);
        return baseTargets.get(target.toLowerCase(Locale.ENGLISH));
    }
}
