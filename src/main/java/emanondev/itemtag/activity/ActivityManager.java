package emanondev.itemtag.activity;

import emanondev.itemedit.YMLConfig;
import emanondev.itemtag.ItemTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

public class ActivityManager {


    private static final HashMap<String,Activity> activities = new HashMap<>();

    public static void reload(){
        YMLConfig config = ItemTag.get().getConfig("activity" + File.separator + "config.yml");
        for (String key:config.getKeys(false)){
            try{
                if (!Pattern.compile("[a-z][_a-z0-9]*").matcher(key).matches()){
                    ItemTag.get().log("Unable to load activity &e"+key+" &ffor invalid id, skipping it, id must start with a letter and may be followed by letters, numbers or underscores (regex &e[a-z][_a-z0-9]*&f)");
                    continue;
                }
                Activity activity = new Activity(key);
                activities.put(activity.getId(), activity);

            }catch (Exception e){
                ItemTag.get().log("Unable to load activity &e"+key+" &ffor unknown error");
                e.printStackTrace();
            }
        }
    }

    public static @Nullable Activity getActivity(@NotNull String id){
        return activities.get(id.toLowerCase(Locale.ENGLISH));
    }
}
