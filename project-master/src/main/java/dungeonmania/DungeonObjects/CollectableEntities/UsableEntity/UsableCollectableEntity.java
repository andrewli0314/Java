package dungeonmania.DungeonObjects.CollectableEntities.UsableEntity;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.CollectableEntities.CollectableEntity;
import dungeonmania.Interfaces.UseItemInterface;
import dungeonmania.util.Position;

public abstract class UsableCollectableEntity extends CollectableEntity implements UseItemInterface {

    public UsableCollectableEntity(Position position, String type) {
        super(position, type);
    }

    public abstract void use(EntitiesManager environment, EntitiesManager inventory, Player player);

}
