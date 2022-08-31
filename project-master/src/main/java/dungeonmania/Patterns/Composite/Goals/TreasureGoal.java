package dungeonmania.Patterns.Composite.Goals;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;

public class TreasureGoal implements DefaultGoal {
    private EntitiesManager environment;
    private String goalName = ":treasure";

    /**
     * Treasure goal class checks for treausre goal completion
     * 
     * @param environment
     */
    public TreasureGoal(EntitiesManager environment) {
        this.environment = environment;
    }

    /**
     * Checks if the treasureGoal is completed by enssuring that no more treasure
     * exist in the list entity array in the current tick state
     */
    @Override
    public boolean checkGoalCompleted() {
        for (Entity entity : environment.getAllEntities()) {
            if (entity.getType().equals("treasure")) {
                return false;
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
