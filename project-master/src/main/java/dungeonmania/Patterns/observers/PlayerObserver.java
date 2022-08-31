package dungeonmania.Patterns.observers;

import dungeonmania.Game;
import dungeonmania.Subject;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.Mercenary;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.InvincibilityPotion;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.InvisibilityPotion;
import dungeonmania.Interfaces.MovingInterface;
import dungeonmania.Patterns.strategy.FleeingStrategy;
import dungeonmania.Patterns.strategy.MovementStrategy;
import dungeonmania.Patterns.strategy.RandomStrategy;

public class PlayerObserver implements Observer{

    @Override
    public void update(Subject g) {
        // TODO Auto-generated method stub
        if (!(g instanceof Game))
            return;
        this.updateMovementStrategyOnInvisibility((Game) g);
        this.updateMovementStrategyOnInvincibility((Game) g);
        
    }
    private Player getPlayer(Game g){
        return g.getPlayer();
    }
    private void updateMovementStrategyOnInvisibility(Game g){
        if (getPlayer(g).getCurrentBuff() == null)
            return;
        if (!(getPlayer(g).getCurrentBuff().getBuffType() instanceof InvisibilityPotion))
            return;
        this.updateStrategy(g, "mercenary", new RandomStrategy<Mercenary>(g));
        
    }
    private void updateMovementStrategyOnInvincibility(Game g){
        if (getPlayer(g).getCurrentBuff() == null)
            return;
        if (!(getPlayer(g).getCurrentBuff().getBuffType() instanceof InvincibilityPotion))
            return;
        this.updateStrategy(g, "mercenary", new FleeingStrategy<Mercenary>(g));
        this.updateStrategy(g, "zombie_toast", new FleeingStrategy<Mercenary>(g));
    }
    private <E extends Entity & MovingInterface> void updateStrategy(Game g, String entity, MovementStrategy<E> movementStrategy){
        for (Entity e : g.getEnvironment().getAllEntities()){
            if (!e.getType().equals(entity) || !(e instanceof MovingInterface))
                return;
            if (e instanceof Mercenary && ((Mercenary) e).getIsAlly() && movementStrategy instanceof FleeingStrategy)
                return;
            E movingEntity = (E) e;
            movingEntity.setMovementStrategy(movementStrategy);
            movementStrategy.setEntity(movingEntity);
        }
    }
}
