package emanondev.itemtag.activity;

import emanondev.itemtag.activity.action.*;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Locale;

public class ActionManager {
    private static final HashMap<String,ActionType> actionTypes = new HashMap<>();

    public static @NotNull ActionType.Action read(@NotNull String line) {
        int index = line.indexOf(" ");
        String id = (index==-1?line:line.substring(0,index)).toLowerCase(Locale.ENGLISH);
        String info = index==-1?"":line.substring(index+1);
        return actionTypes.get(id).read(info);
    }

    public static void register(@NotNull ActionType action){
        String id = action.getId();
        if (actionTypes.containsKey(id))
            throw  new IllegalArgumentException();
        actionTypes.put(id,action);
    }

    public static void load(){
        register(new CommandActionType());
        register(new CommandAsOpActionType());
        register(new ServerCommandActionType());
        register(new MessageActionType());
        register(new TitleActionType());//since version?
        register(new ActionBarActionType());//since version?
        register(new DelayedActionType());
        register(new ConditionalActionType());
        register(new RandomActionType());
        register(new ConsumeUsesActionType());
        //TODO event
    }


}
