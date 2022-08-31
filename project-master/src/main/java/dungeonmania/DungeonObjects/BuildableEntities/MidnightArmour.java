package dungeonmania.DungeonObjects.BuildableEntities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.CollectableEntities.SunStone;
import dungeonmania.DungeonObjects.CollectableEntities.Sword;
import dungeonmania.Interfaces.BattleInterfaces.LinearStatIncrease;
import dungeonmania.Interfaces.BattleInterfaces.WeaponInterface;
import dungeonmania.util.Position;

public class MidnightArmour extends BuildableEntity implements WeaponInterface, LinearStatIncrease {

    private int durability = 1;
    private double attack;
    private double defence;


    public MidnightArmour(Position position, String type, JSONObject config) {
        super(position, type);
        this.attack = config.getDouble("midnight_armour_attack");
        this.defence = config.getDouble("midnight_armour_defence");
    }

    @Override
    public void useMaterials(EntitiesManager inventory) {
        List<Entity> materials = new ArrayList<>();
        // Midnight armour requires 1 sword and 1 sun stone
        int numSword = 0;
        int numSunStone = 0;

        for (Entity material : inventory.getAllEntities()) {
            if (material instanceof Sword && numSword < 1) {
                materials.add(material);
                numSword += 1;
            }
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

    public static boolean canBeBuilt(EntitiesManager inventory, EntitiesManager environment) {
        // Check if any zombie_toasts in environment
        for (Entity entity : environment.getAllEntities()) {
            if (entity.getType().equals("zombie_toast")) {
                return false;
            }
        }

        // 1 Sword and 1 sun stone
        int numSword = 0;
        int numSunStone = 0;

        for (Entity material : inventory.getAllEntities()) {
            if (material instanceof Sword) {
                numSword += 1;
            }
            if (material instanceof SunStone) {
                numSunStone += 1;
            }
            
            if (numSword > 0 && numSunStone > 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double increaseAttackLinear(double playerAttack, double playerDefence, double playerHealth,
            double enemyAttack, double enemyDefence, double enemyHealth) {
        return playerAttack + this.attack;
    }

    @Override
    public double increaseDefenceLinear(double playerAttack, double playerDefence, double playerHealth,
            double enemyAttack, double enemyDefence, double enemyHealth) {
        return playerDefence + this.defence;
    }

    @Override
    public int reduceDurability() {
        // Durability never decreases
        return 1;
    }
    
}
