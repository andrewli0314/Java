package dungeonmania.Patterns.Composite.Goals;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.BattleEntity;
import dungeonmania.DungeonObjects.StaticEntites.EntitySpawners.EntitySpawner;

public class EnemiesGoal implements DefaultGoal {
    private EntitiesManager environment;
    private String goalName = ":enemies";

    /**
     * Enemies goal is a class that checks for enemy goal completion
     * 
     * @param allEntities
     */
    public EnemiesGoal(EntitiesManager environment) {
        this.environment = environment;
    }

    /**
     * Checks if enemiesGoal is completed by ensuring that no more enemies exist in
     * the enitity array in the current tick state
     */
    @Override
    public boolean checkGoalCompleted() {
        Player player = environment.getPlayer();
        if (player == null) {
            return false;
        }
        for (Entity entity : environment.getAllEntities()) {
            if (entity instanceof BattleEntity || entity instanceof EntitySpawner) {
                if (!player.getAllies().contains(entity)){
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
