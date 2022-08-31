package dungeonmania.DungeonObjects.BattleEntities;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {

    private double bribeFailRate;
    private int reconRadius;
    private Random rand = new Random(1);

    public Assassin(Position position, String type, JSONObject config) {
        super(position, type, config);

        super.setAttack(config.getDouble("assassin_attack"));
        super.setHealth(config.getDouble("assassin_health"));
        super.setBribeAmount(config.getInt("assassin_bribe_amount"));

        this.bribeFailRate = config.getDouble("assassin_bribe_fail_rate");
        this.reconRadius = config.getInt("assassin_recon_radius");


    }

    @Override
    public void interact(Player player, EntitiesManager environment) {
        if (player.hasSceptre()) {
            super.interact(player, environment);
            return;
        }
        double randomNum = rand.nextDouble();
        // If random num is greater than the fail rate
        // bribe is a success
        if (randomNum > bribeFailRate) {
            super.interact(player, environment);
        }
        // Otherwise bribe has failed and player loses their 
        // treasure
        else {
            super.takeTreasure(player);
        }
    }

}
