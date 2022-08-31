package dungeonmania.DungeonObjects.BuildableEntities;

import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.dsig.keyinfo.RetrievalMethod;

import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.CollectableEntities.Arrow;
import dungeonmania.DungeonObjects.CollectableEntities.Key;
import dungeonmania.DungeonObjects.CollectableEntities.SunStone;
import dungeonmania.DungeonObjects.CollectableEntities.Treasure;
import dungeonmania.DungeonObjects.CollectableEntities.Wood;
import dungeonmania.util.Position;

public class Sceptre extends BuildableEntity {

    private int mindControlDuration;

    public Sceptre(Position position, String type, JSONObject config) {
        super(position, type);

        this.mindControlDuration = config.getInt("mind_control_duration");
    }

    public int getMindControlDuration() {
        return mindControlDuration;
    }

    @Override
    public void useMaterials(EntitiesManager inventory) {
        List<Entity> materials = new ArrayList<>();

        // 1 wood OR 2 arraows
        int numWood = 0;
        int numArrows = 0;

        // 1 key OR 1 Treasure
        int numKeys = 0;
        int numTreasure = 0;

        // 1 sunstone
        int numSunStone = 0;

        for (Entity material : inventory.getAllEntities()) {

            // 1 Wood or 2 arrows
            if (material instanceof Wood && numWood < 1) {
                // Remove arrow if it is already in materials list
                // for removal and use wood as material instead
                for (int i = materials.size() - 1; i >= 0; --i) {
                    if (materials.get(i).getType().equals("arrow")) {
                        materials.remove(i);
                    }
                }
                materials.add(material);
                numWood += 1;
            }
            if (material instanceof Arrow && (numArrows < 2 && numWood < 1)) {
                materials.add(material);
                numArrows += 1;
            }

            // 1 Key or 1 Treasure/Sun Stone
            if (material instanceof Key && (numKeys < 1 && numTreasure < 1)) {
                materials.add(material);
                numKeys += 1;
            }
            if (material instanceof Treasure && (numKeys < 1 && numTreasure < 1)) {
                materials.add(material);
                numTreasure += 1;
            }
            if (material instanceof SunStone && numSunStone >= 1) {
                // If sun stone is already being used, it can 
                // be used in place in treasure and will not be consumed
                numTreasure += 1;
            }
            // 1 Sun stone if none have been used so far
            if (material instanceof SunStone && numSunStone < 1) {
                materials.add(material);
                numSunStone += 1;
            } 
        }
        // Remove each material from inventory
        for (Entity material : materials) {
            inventory.removeEntityFromManager(material);
        }

    }

    public static boolean canBeBuilt(EntitiesManager inventory) {
        // 1 wood OR 2 arraows
        int numWood = 0;
        int numArrows = 0;

        // 1 key OR 1 Treasure
        int numKeys = 0;
        int numTreasure = 0;

        // 1 sunstone
        int numSunStone = 0;

        for (Entity material : inventory.getAllEntities()) {
            
            if (material instanceof Wood) {
                numWood += 1;
            }
            if (material instanceof Arrow) {
                numArrows += 1;
            }
            if (material instanceof Key) {
                numKeys += 1;
            }
            if (material instanceof Treasure || (material instanceof SunStone && numSunStone >= 1)) {
                // Sunstone should only be counted as treasure
                // if a sunstone is not already being used
                numTreasure += 1;
            }
            else if (material instanceof SunStone) {
                numSunStone += 1;
            }

            if ((numWood >= 1 || numArrows >= 2) && (numKeys >= 1 || numTreasure >= 1) && numSunStone >= 1) {
                return true;
            }
        }
        return false;
    }
}
