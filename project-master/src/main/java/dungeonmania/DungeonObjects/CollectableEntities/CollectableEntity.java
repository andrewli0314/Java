package dungeonmania.DungeonObjects.CollectableEntities;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.util.Position;

public abstract class CollectableEntity extends Entity {

    public CollectableEntity(Position position, String type) {
        super(position, type);
    }
    

    @Override
    public void update(EntitiesManager entitiesManager, Player player) {
        if (player.getPosition().equals(super.getPosition())) {
            // If item on same position as player add to inventory
            player.addToInventory(this);
        }
    }
}
