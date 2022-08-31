package dungeonmania.Patterns.Composite.Goals;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.StaticEntites.Exit;

public class ExitGoal implements DefaultGoal {
    private EntitiesManager environment;
    private String goalName = ":exit";

    /**
     * Exit Goal checks for exit goal completetion
     * 
     * @param allEntities
     */
    public ExitGoal(EntitiesManager environment) {
        this.environment = environment;
    }

    /**
     * Checks if exitGoal is completed by ensuring that the player's postion and
     * exit position is the same, in the current tick state.
     */
    @Override
    public boolean checkGoalCompleted() {
        Player player = environment.getPlayer();
        if (player == null) {
            return false;
        }
        // If player is on exit then true is returned
        for (Entity entity : environment.getAllEntities()) {
            if (entity.getType().equals("exit")) {
                Exit exit = (Exit) entity;
                if (exit.hasPlayerOnSelf(player)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String getGoalString() {
        if (checkGoalCompleted()) {
            return "";
        }
        return goalName;
    }

}
