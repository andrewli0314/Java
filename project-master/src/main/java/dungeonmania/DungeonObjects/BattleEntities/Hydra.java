package dungeonmania.DungeonObjects.BattleEntities;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.Interfaces.BattleInterfaces.LinearStatIncrease;
import dungeonmania.util.Position;

public class Hydra extends ZombieToast implements LinearStatIncrease {

    private Random random = new Random(1);
    private double increaseRate;
    private double increaseAmount;


    public Hydra(Position position, String type, JSONObject config) {
        super(position, type, config);
        //TODO Auto-generated constructor stub
        super.setAttack(config.getDouble("hydra_attack"));
        super.setHealth(config.getDouble("hydra_health"));

        this.increaseRate = config.getDouble("hydra_health_increase_rate");
        this.increaseAmount = config.getDouble("hydra_health_increase_amount");
    }


    @Override
    public double increaseAttackLinear(double playerAttack, double playerDefence, double playerHealth,
            double enemyAttack, double enemyDefence, double enemyHealth) {
        return enemyAttack;
    }


    @Override
    public double increaseDefenceLinear(double playerAttack, double playerDefence, double playerHealth,
            double enemyAttack, double enemyDefence, double enemyHealth) {

        double randomNum = random.nextDouble();
        // If random number is <= increase rate then hydra gains
        // "another head" which can be the same as the player doing 
        // negative damage equal to the increase amount
        if ((randomNum <= increaseRate) && increaseRate != 0) {
            return playerAttack + (increaseAmount * 5);
        }
        return enemyDefence;
    }
    
}
