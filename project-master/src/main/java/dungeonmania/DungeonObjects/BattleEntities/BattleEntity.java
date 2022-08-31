package dungeonmania.DungeonObjects.BattleEntities;

import com.google.gson.annotations.SerializedName;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.Interfaces.MovingInterface;
import dungeonmania.Patterns.strategy.MovementStrategy;
import dungeonmania.util.Position;

public abstract class BattleEntity extends Entity implements MovingInterface {

    private double attack;
    private double health;
    private double defence;
    private boolean isAlly;
    private transient MovementStrategy movementStrategy = null;

    @SerializedName("class")
    private String className;


    public BattleEntity(Position position, String type) {
        super(position, type);
        this.isAlly = false;

        className = getClass().getName();
    }

    public double getAttack() {
        return this.attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public double getHealth() {
        return this.health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getDefence() {
        return this.defence;
    }

    public void setDefence(double defence) {
        this.defence = defence;
    }

    public boolean getIsAlly() {
        return this.isAlly;
    }

    public void setIsAlly(boolean isAlly) {
        this.isAlly = isAlly;
    }
    
    @Override
    public MovementStrategy getMovementStrategy(){
        return this.movementStrategy;
    }

    @Override
    public void setMovementStrategy(MovementStrategy m){
        this.movementStrategy = m;
    }

    @Override
    public void move(EntitiesManager environment) {
        Position nextPos = getNextPosition();
        if (canMoveToNextPosition(environment, nextPos)) {
            this.setPosition(nextPos);
        }
    }

    @Override
    public boolean canMoveToNextPosition(EntitiesManager entitiesManager, Position nextPosition) {
        // Stubs
        return true;
    }

    @Override
    public void update(EntitiesManager entitiesManager, Player player) {
        move(entitiesManager);
    }

}
