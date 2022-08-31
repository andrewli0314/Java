package dungeonmania.DungeonObjects.StaticEntites.EntitySpawners;


import java.util.List;

import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.ZombieToast;
import dungeonmania.Interfaces.InteractableInterface;
import dungeonmania.Interfaces.BattleInterfaces.WeaponInterface;
import dungeonmania.util.Position;

public class ZombieToastSpawner extends EntitySpawner implements InteractableInterface {

    public ZombieToastSpawner(Position position, String type, JSONObject config) {
        super(position, type);
        //TODO Auto-generated constructor stub
        super.setIsInteractable(true);
        super.setSpawnRate(config.getInt("zombie_spawn_rate"));
    }  


    @Override  
    public Entity spawn(JSONObject config) {
        Position currPosition = super.getPosition();
        List<Position> adjacentPositions = currPosition.getAdjacentPositions();

        ////////////////////////////
        // NEED TO CHECK IF ADJACENT POSITION CAN SPAWN ENTITY
        ////////////////////////////
        
        Position spawnPosition = adjacentPositions.get(1);
        return new ZombieToast(spawnPosition, "zombie_toast", config);
    }


    @Override
    public boolean canInteract(Player player) {
        List<Position> cardinallyAdjacentPositions = super.getPosition().getCardinallyAdjacentPosition();
        if (!cardinallyAdjacentPositions.contains(player.getPosition())) {
            // If player isn't cardinally adjacent to zombie toast spawner
            // player cannot interact with it
            return false;
        }
        // If player has a weapon then they can interact with spawner
        // otherwise false is returned
        for (Entity item : player.getInventoryEntities()) {
            if (item instanceof WeaponInterface) {
                return true;
            } 
        }

        return false;
    }


    @Override
    public void interact(Player player, EntitiesManager environment) {
        // TODO Auto-generated method stub
        environment.removeEntityFromManager(this);
    }

}
