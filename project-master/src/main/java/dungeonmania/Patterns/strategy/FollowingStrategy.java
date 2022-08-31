package dungeonmania.Patterns.strategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dungeonmania.Game;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.Interfaces.MovingInterface;
import dungeonmania.util.Position;

public class FollowingStrategy<E extends Entity & MovingInterface> extends MovementStrategy<E>{

    private Map<Position, Position> result = new HashMap<>();
    public FollowingStrategy(Game g) {
        super(g);
        //TODO Auto-generated constructor stub
    }
    private boolean flood(Position currPos, Position targetPos){
        List<Position> queue = new ArrayList<>();
        List<Position> visited = new ArrayList<>();
        queue.add(currPos);
        while(!queue.isEmpty()){
            Position pos = queue.get(0);
            queue.remove(0);
            visited.add(pos);
            if (pos.equals(targetPos))
                return true;
            Position up = new Position(pos.getX(), pos.getY()-1);
            Position down = new Position(pos.getX(), pos.getY()+1);
            Position left = new Position(pos.getX()-1, pos.getY());
            Position right = new Position(pos.getX()+1, pos.getY());
            Position positions [] = {up, down, left, right};
            for (Position p : positions){
                if(this.isNextPosValid(p) && !visited.contains(p)){
                    result.put(p, pos);
                    queue.add(p);
                }
            }
        }
        return false;
    }
    private void reset(){
        this.result.clear();
    }
    private Position getClosestSquareToPlayer(Position currPos, Position playerPos){
        int playerX = getPlayer().getPosition().getX();
        int playerY = getPlayer().getPosition().getY();
        Position up = new Position(currPos.getX(), currPos.getY()-1);
        Position down = new Position(currPos.getX(), currPos.getY()+1);
        Position left = new Position(currPos.getX()-1, currPos.getY());
        Position right = new Position(currPos.getX()+1, currPos.getY());
        List<Position> nextPositions = new ArrayList<>();
        nextPositions.add(up);
        nextPositions.add(down);
        nextPositions.add(left);
        nextPositions.add(right);
        nextPositions.add(currPos);
        int minDist = nextPositions.stream().filter(p -> this.isNextPosValid(p)).mapToInt(p -> Math.abs(p.getX()-playerX) + Math.abs(p.getY()-playerY)).min().getAsInt();
        Position newPos = nextPositions.stream().filter(p -> (Math.abs(p.getX()-playerX) + Math.abs(p.getY()-playerY)) <= minDist).findFirst().get();
        return newPos;
    }
    @Override
    public Position getNextPosition() {
        this.reset();
        boolean found = this.flood(getPlayer().getPrevPosition(), this.getEntity().getPosition());
        Position nextPosition = null;
        if (found && !this.result.isEmpty())
            nextPosition =  this.result.get(this.getEntity().getPosition());
        else if (found && this.result.isEmpty())
            nextPosition = this.getEntity().getPosition();
        else
            nextPosition = this.getClosestSquareToPlayer(this.getEntity().getPosition(), getPlayer().getPosition());
        if (nextPosition.equals(getPlayer().getPosition()))
            nextPosition = this.getEntity().getPosition();
        return nextPosition;
    }
    
}
