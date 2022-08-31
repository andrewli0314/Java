package dungeonmania.DungeonObjects.CollectableEntities;


import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.util.Position;

public class Key extends CollectableEntity {

    private int key;

    public Key(Position position, String type, int key) {
        super(position, type);
        this.key = key;
    }


    @Override
    public void update(EntitiesManager entitiesManager, Player player) {
        if (!player.hasKey()) {
            // If player only has one key, add item to inventory
            super.update(entitiesManager, player);
        }
    }

    public int getKeyCode(){
        return this.key;
    }
}
