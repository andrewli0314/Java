package dungeonmania.Interfaces;


import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.Patterns.strategy.MovementStrategy;
import dungeonmania.util.Position;

public interface MovingInterface {
    public <E extends Entity & MovingInterface> MovementStrategy<E> getMovementStrategy();
    public <E extends Entity & MovingInterface> void setMovementStrategy(MovementStrategy<E> m);
    public Position getNextPosition();
    public boolean canMoveToNextPosition(EntitiesManager entitiesManager, Position nextPosition);
    public void move(EntitiesManager entitiesManager);
}
