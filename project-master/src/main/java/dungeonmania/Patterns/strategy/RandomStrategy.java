package dungeonmania.Patterns.strategy;

import java.util.Random;

import dungeonmania.Game;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.Interfaces.MovingInterface;
import dungeonmania.util.Position;

public class RandomStrategy<E extends Entity & MovingInterface> extends MovementStrategy<E>{

    private long seed = System.currentTimeMillis();
    public RandomStrategy(Game g) {
        super(g);
        //TODO Auto-generated constructor stub
    }
    public void setSeed(long seed){
        this.seed = seed;
    }
    @Override
    public Position getNextPosition() {
        // TODO Auto-generated method stub
        int currX = this.getEntity().getPosition().getX();
        int currY = this.getEntity().getPosition().getY();

        Random rand = new Random(this.seed);
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
    }
    
}
