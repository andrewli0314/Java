package dungeonmania.Patterns.Factories;

import org.json.JSONObject;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.Assassin;
import dungeonmania.DungeonObjects.BattleEntities.Hydra;
import dungeonmania.DungeonObjects.BattleEntities.Mercenary;
import dungeonmania.DungeonObjects.BattleEntities.Spider;
import dungeonmania.DungeonObjects.BattleEntities.ZombieToast;
import dungeonmania.DungeonObjects.BuildableEntities.Bow;
import dungeonmania.DungeonObjects.BuildableEntities.MidnightArmour;
import dungeonmania.DungeonObjects.BuildableEntities.Sceptre;
import dungeonmania.DungeonObjects.BuildableEntities.Shield;
import dungeonmania.DungeonObjects.CollectableEntities.Arrow;
import dungeonmania.DungeonObjects.CollectableEntities.Key;
import dungeonmania.DungeonObjects.CollectableEntities.SunStone;
import dungeonmania.DungeonObjects.CollectableEntities.Sword;
import dungeonmania.DungeonObjects.CollectableEntities.TimeTurner;
import dungeonmania.DungeonObjects.CollectableEntities.Treasure;
import dungeonmania.DungeonObjects.CollectableEntities.Wood;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.Bomb;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.InvincibilityPotion;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.InvisibilityPotion;
import dungeonmania.DungeonObjects.StaticEntites.Boulder;
import dungeonmania.DungeonObjects.StaticEntites.Door;
import dungeonmania.DungeonObjects.StaticEntites.Exit;
import dungeonmania.DungeonObjects.StaticEntites.FloorSwitch;
import dungeonmania.DungeonObjects.StaticEntites.Portal;
import dungeonmania.DungeonObjects.StaticEntites.TimeTravellingPortal;
import dungeonmania.DungeonObjects.StaticEntites.Wall;
import dungeonmania.DungeonObjects.StaticEntites.EntitySpawners.ZombieToastSpawner;
import dungeonmania.util.Position;

public class EntityFactory {

    public static Entity buildEntityFromJSON(JSONObject entityBlueprint, JSONObject config) {
        
        int x = entityBlueprint.getInt("x");
        int y = entityBlueprint.getInt("y");
        Position pos = new Position(x, y);
        String type = entityBlueprint.getString("type");

        int key;
        String colour;

        switch (type) {
            case "door":
                key = entityBlueprint.getInt("key");
                return new Door(pos, type, key);
            case "key":
                key = entityBlueprint.getInt("key");
                return new Key(pos, type, key);
            case "portal":
                colour = entityBlueprint.getString("colour");
                return new Portal(pos, type, colour);
            case "time_travelling_portal":
                colour = entityBlueprint.getString("colour");
                return new TimeTravellingPortal(pos, type, colour);
        }
        
        // Return one from generic factory
        return buildEntity(pos, type, config);

    }

    public static Entity buildEntity(Position pos, String type, JSONObject config) {

        switch (type) {
            // Player
            case "player":
                return new Player(pos, type, config);
            // Static Entities
            case "wall":
                return new Wall(pos, type);
            case "exit":
                return new Exit(pos, type);
            case "boulder":
                return new Boulder(pos, type);
            case "switch":
                return new FloorSwitch(pos, type);
            case "zombie_toast_spawner":
                return new ZombieToastSpawner(pos, type, config);

            // Battle Entites
            case "spider":
                return new Spider(pos, type, config);
            case "zombie_toast":
                return new ZombieToast(pos, type, config);
            case "mercenary":
                return new Mercenary(pos, type, config);

            // Bosses
            case "hydra":
                return new Hydra(pos, type, config);
            case "assassin":
                return new Assassin(pos, type, config);

            // Collectables
            case "treasure":
                return new Treasure(pos, type);
            case "invincibility_potion":
                return new InvincibilityPotion(pos, type, config);
            case "invisibility_potion":
                return new InvisibilityPotion(pos, type, config);
            case "wood":
                return new Wood(pos, type);
            case "arrow":
                return new Arrow(pos, type);
            case "bomb":
                return new Bomb(pos, type, config);
            case "sword":
                return new Sword(pos, type, config);
            case "sun_stone":
                return new SunStone(pos, type);
            case "time_turner":
                return new TimeTurner(pos, type);


            // Buldables
            case "bow":
                return new Bow(pos, type, config);
            case "shield":
                return new Shield(pos, type, config);
            case "sceptre":
                return new Sceptre(pos, type, config);
            case "midnight_armour":
                return new MidnightArmour(pos, type, config);
            
        }
        return null;
    }
}
