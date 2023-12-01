package emanondev.itemtag.activity;

import emanondev.itemedit.YMLConfig;
import emanondev.itemtag.ItemTag;
import emanondev.itemtag.activity.action.EmptyActionType;
import emanondev.itemtag.activity.condition.EmptyConditionType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class Activity {

    private final List<ActionType.Action> actions = new ArrayList<>();
    private final List<ActionType.Action> alternativeActions = new ArrayList<>();
    private final List<ActionType.Action> noConsumesActions = new ArrayList<>();
    private final List<ConditionType.Condition> conditions = new ArrayList<>();
    private int consumes;
    //private int alternativeConsumes;
    private final String id;
    private static final YMLConfig config = ItemTag.get().getConfig("activity"+ File.separator+"config.yml");


    public Activity(@NotNull String id){
        if (!Pattern.compile("[a-z][_a-z0-9]*").matcher(id).matches())
            throw new IllegalArgumentException();
        this.id = id;
        consumes = config.loadInteger(getId()+".consumes",1);
        //alternativeConsumes = config.loadInteger(getId()+".alternativeConsumes",0);
        List<String> rawList = config.getStringList(getId()+".conditions");
        for (String line:rawList){
            try{
                conditions.add(ConditionManager.read(line));
            } catch(Exception e){
                conditions.add(EmptyConditionType.INST.read(line));
            }
        }
        rawList = config.getStringList(getId()+".actions");
        for (String line:rawList){
            try{
                actions.add(ActionManager.read(line));
            } catch(Exception e){
                actions.add(EmptyActionType.INST.read(line));
            }
        }
        rawList = config.getStringList(getId()+".alternativeActions");
        for (String line:rawList){
            try{
                alternativeActions.add(ActionManager.read(line));
            } catch(Exception e){
                alternativeActions.add(EmptyActionType.INST.read(line));
            }
        }
        rawList = config.getStringList(getId()+".noConsumesActions");
        for (String line:rawList){
            try{
                noConsumesActions.add(ActionManager.read(line));
            } catch(Exception e){
                noConsumesActions.add(EmptyActionType.INST.read(line));
            }
        }
    }
    public int getConsumes() {
        return consumes;
    }

    public void setConsumes(int consumes) {
        this.consumes = Math.max(consumes, 0);
        save();
    }

/*
    public int getAlternativeConsumes() {
        return alternativeConsumes;
    }

    public void setAlternativeConsumes(int alternativeConsumes) {
        this.alternativeConsumes = Math.max(alternativeConsumes, 0);
        save();
    }*/

    public List<ActionType.Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public List<ActionType.Action> getAlternativeActions() {
        return Collections.unmodifiableList(alternativeActions);
    }

    public List<ActionType.Action> getNoConsumesActions() {
        return Collections.unmodifiableList(noConsumesActions);
    }

    public List<ConditionType.Condition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    public void addCondition(@NotNull ConditionType.Condition cond){
        conditions.add(cond);
        save();
    }
    public void addCondition(int place,@NotNull ConditionType.Condition cond){
        conditions.add(place, cond);
        save();
    }
    public void setCondition(int place,@NotNull ConditionType.Condition cond){
        conditions.set(place,cond);
        save();
    }
    public void removeCondition(int place){
        conditions.remove(place);
        save();
    }

    public void addAction(@NotNull ActionType.Action action){
        actions.add(action);
        save();
    }
    public void addAction(int place,@NotNull ActionType.Action action){
        actions.add(place, action);
        save();
    }
    public void setAction(int place,@NotNull ActionType.Action action){
        actions.set(place,action);
        save();
    }
    public void removeAction(int place){
        actions.remove(place);
        save();
    }


    public void addAlternativeAction(@NotNull ActionType.Action action){
        alternativeActions.add(action);
        save();
    }
    public void addAlternativeAction(int place,@NotNull ActionType.Action action){
        alternativeActions.add(place, action);
        save();
    }
    public void setAlternativeAction(int place,@NotNull ActionType.Action action){
        alternativeActions.set(place,action);
        save();
    }
    public void removeAlternativeAction(int place){
        alternativeActions.remove(place);
        save();
    }


    public void addNoConsumesAction(@NotNull ActionType.Action action){
        noConsumesActions.add(action);
        save();
    }
    public void addNoConsumesAction(int place,@NotNull ActionType.Action action){
        noConsumesActions.add(place, action);
        save();
    }
    public void setNoConsumesAction(int place,@NotNull ActionType.Action action){
        noConsumesActions.set(place,action);
        save();
    }
    public void removeNoConsumesAction(int place){
        noConsumesActions.remove(place);
        save();
    }

    private void save() {
        config.set(getId()+".consumes",consumes);
        //config.set(getId()+".alternativeConsumes",alternativeConsumes);
        List<String> rawList1 = new ArrayList<>();
        conditions.forEach(condition -> rawList1.add(condition.toString()));
        config.set(getId()+".conditions",rawList1);

        List<String> rawList2 = new ArrayList<>();
        actions.forEach(action -> rawList2.add(action.toString()));
        config.set(getId()+".actions",rawList2);

        List<String> rawList3 = new ArrayList<>();
        alternativeActions.forEach(action -> rawList3.add(action.toString()));
        config.set(getId()+".alternativeActions",rawList3);

        List<String> rawList4 = new ArrayList<>();
        noConsumesActions.forEach(action -> rawList4.add(action.toString()));
        config.set(getId()+".noConsumesActions",rawList4);
    }

    public String getId() {
        return id;
    }
}
