package dungeonmania.DungeonObjects.CollectableEntities.UsableEntity;


import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.Interfaces.BattleInterfaces.LinearStatIncrease;
import dungeonmania.util.Position;

public class InvincibilityPotion extends UsableCollectableEntity implements LinearStatIncrease {

    private int duration;

    public InvincibilityPotion(Position position, String type, JSONObject config) {
        super(position, type);
        this.duration = config.getInt("invincibility_potion_duration");
    }

    @Override
    public void use(EntitiesManager environment, EntitiesManager inventory, Player player) {
        player.addBuff(this, duration);
        inventory.removeEntityFromManager(this);
    }

    @Override
    public double increaseDefenceLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        // Since player is invincible, their defence sohuld 
        // be equal to enemies attack to negate all damage
        return enemyAttack;
    }

    @Override
    public double increaseAttackLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        // An invicible player should instantly win the fight
        // an thus their damage can equivalent to the enemies health
        return enemyHealth * 5;
    }

}
