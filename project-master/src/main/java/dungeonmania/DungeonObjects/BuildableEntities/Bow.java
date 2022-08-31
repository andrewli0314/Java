package dungeonmania.DungeonObjects.BuildableEntities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.CollectableEntities.Arrow;
import dungeonmania.DungeonObjects.CollectableEntities.Wood;
import dungeonmania.Interfaces.BattleInterfaces.MultiplyStatIncrease;
import dungeonmania.Interfaces.BattleInterfaces.WeaponInterface;
import dungeonmania.util.Position;

public class Bow extends BuildableEntity implements WeaponInterface, MultiplyStatIncrease {

    private int durability;

    public Bow(Position position, String type, JSONObject config) {
        super(position, type);
        this.durability = config.getInt("bow_durability");
    }

    @Override
    public void useMaterials(EntitiesManager inventory) {
        List<Entity> materials = new ArrayList<>();

        // Requires 1 wood and 3 arrows
        int numWood = 0;
        int numArrows = 0;
        for (Entity material : inventory.getAllEntities()) {
            if (material instanceof Wood && numWood < 1) {
                materials.add(material);
                numWood += 1;
            }
            if (material instanceof Arrow && numArrows < 3) {
                materials.add(material);
                numArrows += 1;
            }
        }
        // Remove each material from inventory
        for (Entity material : materials) {
            inventory.removeEntityFromManager(material);
        }
    }


    public static boolean canBeBuilt(EntitiesManager inventory) {

        int numWood = 0;
        int numArrows = 0;

        for (Entity material : inventory.getAllEntities()) {
            if (material instanceof Wood) {
                numWood += 1;
            }
            if (material instanceof Arrow) {
                numArrows += 1;
            }

            // Requires 1 woord and 3 arrows
            if (numWood >= 1 && numArrows >= 3) {
                return true;
            }
        }
        return false;
    }


    @Override
    public double increaseAttackMulitply(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        return 2 * playerAttack;
    }

    @Override
    public int reduceDurability() {
        this.durability -= 1;
        return durability;
    }

}
