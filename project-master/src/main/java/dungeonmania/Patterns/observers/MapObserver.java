package dungeonmania.Patterns.observers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dungeonmania.Game;
import dungeonmania.Subject;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.Bomb;
import dungeonmania.DungeonObjects.StaticEntites.FloorSwitch;
import static dungeonmania.util.Position.isWithinRadius;

public class MapObserver implements Observer{

    @Override
    public void update(Subject g) {
        // TODO Auto-generated method stub
        if (!(g instanceof Game))
            return;
        this.updateFloorSwitches((Game) g);
        this.updateBomb((Game) g);
    }
    private void updateFloorSwitches(Game g){
        for (Entity f : g.getEnvironment().getAllEntities()){
            if (!f.getType().equals("switch"))
                continue;
            FloorSwitch floorSwitch = (FloorSwitch) f;
            if (this.isOnObstacle(g, floorSwitch, "boulder") != null)
                floorSwitch.down();
            else
                floorSwitch.up();
        }

    }
    private boolean isAdjacentToActiveSwitch(Game g, Bomb b){
        for (Entity e : g.getEnvironment().getAllEntities()){
            if (!e.getType().equals("switch"))
                continue;
            FloorSwitch floorSwitch = (FloorSwitch) e;
            if (Math.abs(floorSwitch.getPosition().getX() - b.getPosition().getX()) + Math.abs(floorSwitch.getPosition().getY() - b.getPosition().getY()) <= 1){
                return true;
            }
        }
        return false;
    }
    private void toDetonate(Game g, Bomb b, List<Entity> toDetonate){
        Iterator<Entity> i = g.getEnvironment().getAllEntities().iterator();
        while(i.hasNext()){
            Entity e = i.next();
            if (e instanceof Player)
                continue;
            // If bomb is within radius of entity, that entity
            // is removed from environment
            if (isWithinRadius(e.getPosition(), b.getPosition(), b.getRadius())){
                toDetonate.add(e);
            }
        }
    }

    private void detonate(Game g, List<Entity> toDetonate){
        for (Entity destroyedEntity : toDetonate) {
            g.getEnvironment().removeEntityFromManager(destroyedEntity);
            g.getPlayer().getAllies().remove(destroyedEntity);
        }
    }

    private void updateBomb(Game g){
        Iterator<Entity> i = g.getEnvironment().getAllEntities().iterator();
        List<Entity> toDetonate = new ArrayList<>();
        while(i.hasNext()){
            Entity e = i.next();
            if (!e.getType().equals("bomb"))
                continue;
            Bomb b = (Bomb) e;
            if (this.isAdjacentToActiveSwitch(g, b) && b.isDetonatable()){
                this.toDetonate(g, b, toDetonate);
            }
        }
        this.detonate(g, toDetonate);
    }

}
