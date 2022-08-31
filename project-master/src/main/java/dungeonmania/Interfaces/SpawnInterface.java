package dungeonmania.Interfaces;

import org.json.JSONObject;

import dungeonmania.DungeonObjects.Entity;

public interface SpawnInterface {
    
    public Entity spawn(JSONObject config);
    public boolean canSpawnAtCurrentTick(int currTick);

}
