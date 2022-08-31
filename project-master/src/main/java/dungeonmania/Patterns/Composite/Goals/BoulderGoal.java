package dungeonmania.Patterns.Composite.Goals;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.StaticEntites.FloorSwitch;

public class BoulderGoal implements DefaultGoal {
    private EntitiesManager environment;
    private String goalName = ":boulders";

    /**
     * Boulder Goal checks conditions of boulder goal completion
     * 
     * @param allEntities
     */
    public BoulderGoal(EntitiesManager environment) {
        this.environment = environment;
    }

    /**
     * Checks if the boulderGoal is completed by ensuring that the total number of
     * switches matches the number of switches that have been toggled by a boulder
     * in the current tick state
     */
    @Override
    public boolean checkGoalCompleted() {
        // If any floor switches are not toggle by boulder
        // return false
        for (Entity entity : environment.getAllEntities()) {
            if (entity.getType().equals("switch")) {
                FloorSwitch floorSwitch = (FloorSwitch) entity;
                if (!floorSwitch.isToggledByBoulder()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String getGoalString() {
        if (checkGoalCompleted()) {
            return "";
        }
        return goalName;
    }

}

