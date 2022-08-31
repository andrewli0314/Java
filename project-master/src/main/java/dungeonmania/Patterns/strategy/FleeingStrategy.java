package dungeonmania.Patterns.strategy;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.Game;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.Interfaces.MovingInterface;
import dungeonmania.util.Position;

public class FleeingStrategy<E extends Entity & MovingInterface> extends MovementStrategy<E>{

    public FleeingStrategy(Game g) {
        super(g);
        //TODO Auto-generated constructor stub
    }

    @Override
    public Position getNextPosition() {
        int playerX = getPlayer().getPosition().getX();
        int playerY = getPlayer().getPosition().getY();
        int currX = this.getEntity().getPosition().getX();
        int currY = this.getEntity().getPosition().getY();
        List<Position> nextPositions = new ArrayList<>();
        nextPositions.add(new Position(currX, currY));
        nextPositions.add(new Position(currX+1, currY));
        nextPositions.add(new Position(currX-1, currY));
        nextPositions.add(new Position(currX, currY+1));
        nextPositions.add(new Position(currX, currY-1));
        int maxDist = nextPositions.stream().filter(p -> this.isNextPosValid(p)).mapToInt(p -> Math.abs(p.getX()-playerX) + Math.abs(p.getY()-playerY)).max().getAsInt();
        Position newPos = nextPositions.stream().filter(p -> (Math.abs(p.getX()-playerX) + Math.abs(p.getY()-playerY)) >= maxDist).findFirst().get();
        return newPos;
    }
    
}
