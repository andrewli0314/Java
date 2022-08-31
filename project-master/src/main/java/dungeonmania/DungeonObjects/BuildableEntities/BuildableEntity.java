package dungeonmania.DungeonObjects.BuildableEntities;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.util.Position;

public abstract class BuildableEntity extends Entity {
    
    public BuildableEntity(Position position, String type) {
        super(position, type);
    }

    public abstract void useMaterials(EntitiesManager inventory);
    
}
