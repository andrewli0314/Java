package dungeonmania.DungeonObjects.StaticEntites.EntitySpawners;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.BattleEntities.Spider;
import dungeonmania.util.Position;

public class SpiderSpawner extends EntitySpawner {

    public SpiderSpawner(Position pos, String type, JSONObject config) {
        super(pos, type);
        super.setSpawnRate(config.getInt("spider_spawn_rate"));
    }

    @Override
    public Entity spawn(JSONObject config) {
        Position spawnPosition;
        Random rand = new Random();

        boolean isOnBoulder = true;
        int attemptedSpawn = 0;

        // Attemps to spawn from 100 random locations
        // If after 100 random attempts it cannot spawn, null is returned
        while (isOnBoulder && attemptedSpawn < 100) {
            int randomX = rand.nextInt(30);
            int randomY = rand.nextInt(30);

            spawnPosition = new Position(randomX, randomY);

            // 
            // CHECK IF SPAWN POSITION IS ON BOULDER IF SO GENERATE NEW RANDOM POSITION
            //
            attemptedSpawn += 1;

            isOnBoulder = false;

            return new Spider(spawnPosition, "spider", config);
        }
        return null;
    }
    
}
