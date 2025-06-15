package emanondev.itemtag.activity;

import emanondev.itemedit.utility.VersionUtils;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.action.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

public class ActionManager {
    private static final HashMap<String, ActionType> actionTypes = new HashMap<>();

    public static @NotNull ActionType.Action read(@NotNull String line) {
        int index = line.indexOf(" ");
        String id = (index == -1 ? line : line.substring(0, index)).toLowerCase(Locale.ENGLISH);
        String info = index == -1 ? "" : line.substring(index + 1);
        return actionTypes.get(id).read(info);
    }

    public static void register(@NotNull ActionType action) {
        String id = action.getId();
        if (actionTypes.containsKey(id))
            throw new IllegalArgumentException();
        actionTypes.put(id, action);
        ItemTag.get().log("ActionManager registered Action Type &e" + action.getId());
    }

    public static void load() {
        register(new CommandActionType());
        register(new CommandAsOpActionType());
        register(new ServerCommandActionType());
        register(new MessageActionType());
        if (VersionUtils.isVersionAfter(1, 11)) {
            register(new TitleActionType());
        }
        if (VersionUtils.isVersionAfter(1, 9, 2)) {
            register(new ActionBarActionType());
        }
        register(new DelayedActionType());
        register(new ConditionalActionType());
        register(new RandomActionType());
        register(new PlaySoundActionType());
        //TODO event
    }


    public static Collection<String> getActionTypeIds() {
        return Collections.unmodifiableSet(actionTypes.keySet());
    }


}
