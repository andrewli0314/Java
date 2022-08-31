package dungeonmania.Patterns.strategy;

import java.io.Serializable;

import dungeonmania.Game;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.Interfaces.MovingInterface;
import dungeonmania.util.Position;

public abstract class MovementStrategy<E extends Entity & MovingInterface> implements Serializable{

    private E entity;
    private Game g;
    public MovementStrategy(Game g){
        this.g = g;
    }
    public void setEntity(E e){
        this.entity = e;
    }
    public E getEntity(){
        return this.entity;
    }
    public abstract Position getNextPosition();
    public Player getPlayer(){
        return this.g.getPlayer();
    }
    public Game getGame(){
        return this.g;
    }
    public boolean isNextPosValid(Position position){
        Position oldPos = this.entity.getPosition();
        if (oldPos.equals(position))
            return true;
        this.entity.setPosition(position);
        getGame().getObstacleObserver().update(getGame());
        if(this.entity.getPosition().equals(oldPos)){
            return false;
        } else {
            this.entity.rollback();
            return true;
        }
    }
}
