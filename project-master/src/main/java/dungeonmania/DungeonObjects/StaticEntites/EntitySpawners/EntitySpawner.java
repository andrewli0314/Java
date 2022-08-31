package dungeonmania.DungeonObjects.StaticEntites.EntitySpawners;

import org.json.JSONObject;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.StaticEntites.StaticEntity;
import dungeonmania.Interfaces.SpawnInterface;
import dungeonmania.util.Position;

public abstract class EntitySpawner extends StaticEntity implements SpawnInterface {

    int spawnRate;

    public EntitySpawner(Position pos, String type) {
        super(pos, type);
    }


    public abstract Entity spawn(JSONObject config);

    public boolean canSpawnAtCurrentTick(int currTick) {
        if (spawnRate == 0) {
            return false;
        }
        if (currTick % spawnRate == 0) {
            return true;
        }
        return false;
    }

    public int getSpawnRate() {
        return this.spawnRate;
    }

    public void setSpawnRate(int spawnRate) {
        this.spawnRate = spawnRate;
    }


    
}
