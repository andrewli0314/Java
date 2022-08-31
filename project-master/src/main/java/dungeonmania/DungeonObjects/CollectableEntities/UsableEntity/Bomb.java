package dungeonmania.DungeonObjects.CollectableEntities.UsableEntity;


import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.util.Position;

public class Bomb extends UsableCollectableEntity { 

    private int radius;
    private boolean isDetonatable;

    public Bomb(Position position, String type, JSONObject config) {
        super(position, type);
        this.radius = config.getInt("bomb_radius");
        isDetonatable = false;
    }

    @Override
    public void use(EntitiesManager environment, EntitiesManager inventory, Player player) {
        // Set position to player's position since player
        // will activate the bomb
        super.setPosition(player.getPosition());
        this.isDetonatable = true;

        inventory.removeEntityFromManager(this);
        // Add back to environment but player can no longer pick up
        environment.addEntityToManager(this);
    }

    @Override
    public void update(EntitiesManager entitiesManager, Player player) {
        // If bomb is currently detonatable then player cannot pick up
        // bomb
        if (!isDetonatable) {
            super.update(entitiesManager, player);
        }

    }
 
    public boolean isDetonatable(){
        return this.isDetonatable;
    }
    public int getRadius(){
        return this.radius;
    }
}
