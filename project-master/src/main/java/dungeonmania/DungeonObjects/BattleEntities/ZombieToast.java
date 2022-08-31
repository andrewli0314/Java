package dungeonmania.DungeonObjects.BattleEntities;

import java.util.Random;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class ZombieToast extends BattleEntity {

    public ZombieToast(Position position, String type, JSONObject config) {
        super(position, type);

        super.setAttack(config.getDouble("zombie_attack"));
        super.setHealth(config.getDouble("zombie_health"));
    }

    @Override 
    public Position getNextPosition() {
        // Zombies move in random directions
        if (this.getMovementStrategy() == null){
            int currX = super.getPosition().getX();
            int currY = super.getPosition().getY();

            Random rand = new Random();
            int randomInt = rand.nextInt(4);

            if (randomInt == 0) {
                currX += 1;
            } 
            if (randomInt == 1) {
                currX -= 1;
            }
            if (randomInt == 2) {
                currY += 1;
            }
            if (randomInt == 3) {
                currY -= 1;
            }
            return new Position(currX, currY);
        } else {
            return this.getMovementStrategy().getNextPosition();
        }
    }

}
