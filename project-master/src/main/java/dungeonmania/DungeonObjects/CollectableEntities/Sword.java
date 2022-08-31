package dungeonmania.DungeonObjects.CollectableEntities;

import org.json.JSONObject;

import dungeonmania.Interfaces.BattleInterfaces.LinearStatIncrease;
import dungeonmania.Interfaces.BattleInterfaces.WeaponInterface;
import dungeonmania.util.Position;

public class Sword extends CollectableEntity implements WeaponInterface, LinearStatIncrease {

    private int durability;
    private int attack;

    public Sword(Position position, String type, JSONObject config) {
        super(position, type);
        this.attack = config.getInt("sword_attack");
        this.durability = config.getInt("sword_durability");
    }

    @Override
    public double increaseAttackLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        return playerAttack + this.attack;
    }
    @Override
    public double increaseDefenceLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        return playerDefence;
    }

    @Override
    public int reduceDurability() {
        this.durability -= 1;
        return this.durability;
    }

}
