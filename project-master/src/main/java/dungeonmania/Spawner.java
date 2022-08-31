package dungeonmania;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.StaticEntites.EntitySpawners.EntitySpawner;
import dungeonmania.DungeonObjects.StaticEntites.EntitySpawners.SpiderSpawner;
import dungeonmania.util.Position;

public class Spawner implements Serializable{

    JSONObject config;
    EntitiesManager environment;
    List<EntitySpawner> permanentSpawners = new ArrayList<>();

    Spawner(EntitiesManager environment, JSONObject config) {
        this.environment = environment;

        // Spiders will permanently spawn to spawn spider
        permanentSpawners.add(new SpiderSpawner(new Position(0,0), "SpiderSpawner", config));
    }

    public void spawnEnitites(int currTick, JSONObject config) {
        List<Entity> spawnedEntities = new ArrayList<>();

        // If spawner can spawn at current tick then 
        // spawn the entity

        // There are permanent spawners such as spiders
        // which will always spawn
        for (EntitySpawner permSpawner : permanentSpawners) {
            if (permSpawner.canSpawnAtCurrentTick(currTick)) {
                environment.addEntityToManager(permSpawner.spawn(config));
            }
        }

        // Temporrary spawners exist in the environment like
        // zombie toast spawners which can be destoryed
        for (Entity entity : environment.getAllEntities()) {
            if (entity instanceof EntitySpawner) {
                EntitySpawner spawner = (EntitySpawner) entity;
                if (spawner.canSpawnAtCurrentTick(currTick)) {
                    spawnedEntities.add(spawner.spawn(config));
                }
            }
        }

        for (Entity spawnedEntity : spawnedEntities) {
            environment.addEntityToManager(spawnedEntity);
        }
    }

        
}
    
    



