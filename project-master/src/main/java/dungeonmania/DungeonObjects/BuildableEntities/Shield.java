package dungeonmania.DungeonObjects.BuildableEntities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.CollectableEntities.Key;
import dungeonmania.DungeonObjects.CollectableEntities.SunStone;
import dungeonmania.DungeonObjects.CollectableEntities.Treasure;
import dungeonmania.DungeonObjects.CollectableEntities.Wood;
import dungeonmania.Interfaces.BattleInterfaces.LinearStatIncrease;
import dungeonmania.Interfaces.BattleInterfaces.WeaponInterface;
import dungeonmania.util.Position;

public class Shield extends BuildableEntity implements WeaponInterface, LinearStatIncrease {

    private int durability;
    private double defence;

    public Shield(Position position, String type, JSONObject config) {
        super(position, type);
        this.durability = config.getInt("shield_durability");
        this.defence = config.getDouble("shield_defence");
    }

    @Override
    public void useMaterials(EntitiesManager inventory) {

        List<Entity> materials = new ArrayList<>();

        // Requires 2 wood and (1 key or 1 treasure)
        int numWood = 0;
        int numKeys = 0;
        int numTreasures = 0;

        for (Entity material : inventory.getAllEntities()) {
            if (material instanceof Wood && numWood < 2) {
                materials.add(material);
                numWood += 1;
            }
            // If material is treasure or key only add to materials to delete
            // if no treasure or keys have been seen, otherwise double deletion
            // will occur.
            // When material is sun stone, it is counted as treasure
            // but will not be deleted
            if (material instanceof SunStone && (numKeys == 0 && numTreasures == 0)) {
                numTreasures += 1;
            }
            if (material instanceof Treasure && (numKeys == 0 && numTreasures == 0)) {
                materials.add(material);
                numTreasures += 1;
            }
            if (material instanceof Key && (numKeys == 0 && numTreasures == 0)) {
                materials.add(material);
                numKeys += 1;
            }
        }

        // Remove each material from inventory
        for (Entity material : materials) {
            inventory.removeEntityFromManager(material);
        }
    }

    public static boolean canBeBuilt(EntitiesManager inventory) {

        int numWood = 0;
        int numKeys = 0;
        int numTreasures = 0;

        for (Entity material : inventory.getAllEntities()) {
            if (material instanceof Wood) {
                numWood += 1;
            }
            if (material instanceof Key) {
                numKeys += 1;
            }
            if (material instanceof Treasure) {
                numTreasures += 1;
            }
            if (material instanceof SunStone) {
                numTreasures += 1;
            }

            // Requires 2 wood and (1 key or 1 treasure)
            if (numWood >= 2 && (numKeys >= 0 || numTreasures >= 0)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double increaseAttackLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        return playerAttack;
    }

    @Override
    public double increaseDefenceLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        return this.defence + playerDefence;
    }

    @Override
    public int reduceDurability() {
        this.durability -= 1;
        return durability;
    }

}
