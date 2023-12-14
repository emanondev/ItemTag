package emanondev.itemtag.activity;

import emanondev.itemedit.YMLConfig;
import emanondev.itemtag.ItemTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class ActivityManager {


    private static final HashMap<String, Activity> activities = new HashMap<>();

    public static void reload() {
        activities.clear();
        YMLConfig config = ItemTag.get().getConfig("activity" + File.separator + "config.yml");
        int counter = 0;
        for (String key : config.getKeys(false)) {
            try {
                if (!Pattern.compile("[a-z][_a-z0-9]*").matcher(key).matches()) {
                    ItemTag.get().log("Unable to load activity &e" + key + " &ffor invalid id, skipping it, id must start with a letter and may be followed by letters, numbers or underscores (regex &e[a-z][_a-z0-9]*&f)");
                    continue;
                }
                Activity activity = new Activity(key);
                activities.put(activity.getId(), activity);
                counter++;
            } catch (Exception e) {
                ItemTag.get().log("Unable to load activity &e" + key + " &ffor unknown error");
                e.printStackTrace();
            }
        }
        ItemTag.get().log("Loaded &e" + counter + " &factivities");
    }

    public static void registerActivity(@NotNull Activity activity) {
        if (activities.containsKey(activity.getId()))
            throw new IllegalArgumentException();
        activities.put(activity.getId(), activity);
        activity.save();
    }

    public static void deleteActivity(@NotNull Activity activity) {
        if (!activities.containsKey(activity.getId()))
            throw new IllegalArgumentException();
        activities.remove(activity.getId());
        activity.delete();
    }

    public static @Nullable Activity getActivity(@NotNull String id) {
        return activities.get(id.toLowerCase(Locale.ENGLISH));
    }

    public static Collection<String> getActivityIds() {
        return Collections.unmodifiableSet(activities.keySet());
    }

    public static Activity clone(Activity activity, String newId) {
        if (activities.containsKey(newId))
            throw new IllegalArgumentException();
        Activity act = activity.clone(newId);
        registerActivity(act);
        return act;
    }

    public static void rename(Activity activity, String newId) {
        Activity act = clone(activity, newId);
        deleteActivity(activity);
        registerActivity(act);
    }
}
