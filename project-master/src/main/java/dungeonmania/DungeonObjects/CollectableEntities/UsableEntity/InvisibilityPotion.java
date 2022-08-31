package dungeonmania.DungeonObjects.CollectableEntities.UsableEntity;


import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.util.Position;

public class InvisibilityPotion extends UsableCollectableEntity {

    private int duration;

    public InvisibilityPotion(Position position, String type, JSONObject config) {
        super(position, type);
        this.duration = config.getInt("invisibility_potion_duration");
    }

    @Override
    public void use(EntitiesManager environment, EntitiesManager inventory, Player player) {
        player.addBuff(this, duration);
        inventory.removeEntityFromManager(this);
    }
}
