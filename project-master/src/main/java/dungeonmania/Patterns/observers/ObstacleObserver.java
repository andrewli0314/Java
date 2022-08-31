package dungeonmania.Patterns.observers;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

import dungeonmania.Game;
import dungeonmania.Subject;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.Spider;
import dungeonmania.DungeonObjects.CollectableEntities.CollectableEntity;
import dungeonmania.DungeonObjects.CollectableEntities.Key;
import dungeonmania.DungeonObjects.StaticEntites.Boulder;
import dungeonmania.DungeonObjects.StaticEntites.Door;
import dungeonmania.DungeonObjects.StaticEntites.FloorSwitch;
import dungeonmania.DungeonObjects.StaticEntites.Portal;
import dungeonmania.DungeonObjects.StaticEntites.TimeTravellingPortal;
import dungeonmania.util.Position;

/*
 * Assumptions:
 * Boulder cannot be pushed through portals
 * Only Player can push boulders
 */

public class ObstacleObserver implements Observer, Serializable{

    @Override
    public void update(Subject g) {
        if (!(g instanceof Game))
            return;
        this.teleportThroughPortal((Game) g);
        this.teleportThroughTimePortal((Game) g);
        this.handlePlayerDoorInteraction((Game) g);
        this.handlePlayerMovingThroughWall((Game) g);
        this.handlePlayerBoulderInteraction((Game) g);
        this.rollbackEntityOnObstacle((Game) g, "mercenary", "wall", null);
        this.rollbackEntityOnObstacle((Game) g, "mercenary", "boulder", null);
        this.rollbackEntityOnObstacle((Game) g, "mercenary", "door", null);
        this.rollbackEntityOnObstacle((Game) g, "zombie_toast", "wall", null);
        this.rollbackEntityOnObstacle((Game) g, "zombie_toast", "boulder", null);
        this.rollbackEntityOnObstacle((Game) g, "zombie_toast", "door", null);
        this.rollbackEntityOnObstacle((Game) g, "spider", "boulder", (spider) -> {((Spider) spider).reverse(); ((Spider) spider).move(((Game)g).getEnvironment()); return null;});
    }
    private void rollbackEntityOnObstacle(Game g, String entity, String obstacle, Function<Object, Object> f){
        for (Entity z : g.getEnvironment().getAllEntities()){
            if (!z.getType().equals(entity))
                continue;
            if(this.isOnObstacle(g, z, obstacle) != null){
                this.rollbackEntity(z);
                if (f != null)
                    f.apply(z);
            }
        }
    }
    private boolean unlock(Player player, Door door){
        if (!player.hasKey())
            return false;
        for (Entity i : player.getInventory().getAllEntities()){
            if (!i.getType().equals("key"))
                continue;
            Key key = (Key) player.getItem(i.getId());
            if (key.getKeyCode() == door.getKeyCode()){
                player.removeFromInventory(key);
                door.unlock();
                return true;
            }
        }
        return false;
    }
    private void handlePlayerDoorInteraction(Game g){
        List<Entity> doors = this.isOnObstacle(g, getPlayer(g), "door");
        if(doors != null && ((Door) doors.get(0)).isUnlocked())
            return;
        if(doors != null && !this.unlock(getPlayer(g), (Door) doors.get(0)))
                this.rollbackEntity(getPlayer(g));
    }
    private void handlePlayerMovingThroughWall(Game g){
        if(this.isOnObstacle(g, getPlayer(g), "wall") != null)
                this.rollbackEntity(getPlayer(g));
    }
    private void handlePlayerBoulderInteraction(Game g){
        List<Entity> boulders = this.isOnObstacle(g, getPlayer(g), "boulder");
        if(boulders != null){
            Boulder boulder = (Boulder) boulders.get(0);
            boulder.setPosition(new Position(boulder.getPosition().getX() + (getPlayer(g).getPosition().getX() - getPlayer(g).getPrevPosition().getX()),
            boulder.getPosition().getY() + (getPlayer(g).getPosition().getY() - getPlayer(g).getPrevPosition().getY())));
            List<Entity> obstacles = this.isOnObstacle(g, boulder);
            if (obstacles == null)
                return;
            for (Entity obstacle : obstacles){
                if(obstacle != null && !(obstacle instanceof CollectableEntity) && !(obstacle instanceof FloorSwitch)){
                    this.rollbackEntity(getPlayer(g));
                    this.rollbackEntity(boulder);
                    break;
                }
            }
        }
        
    }
    private void rollbackEntity(Entity e){
        e.rollback();
    }
    private Player getPlayer(Game g){
        return g.getPlayer();
    }
    private Portal getCorrespondingPortal(Game g, Portal portal){
        for (Entity i : g.getEnvironment().getAllEntities()){
            if (!i.getType().equals("portal"))
                continue;
            if (i == portal)
                continue;
            if (((Portal) i).getColour().equals(portal.getColour()))
                return (Portal) i;
        }
        return null;
    }
    private void teleportThroughPortal(Game g){
        int dX = getPlayer(g).getPosition().getX()-getPlayer(g).getPrevPosition().getX();
        int dY = getPlayer(g).getPosition().getY()-getPlayer(g).getPrevPosition().getY();
        for (Entity i : g.getEnvironment().getAllEntities()){
            if (!i.getType().equals("portal"))
                continue;
            Portal destPortal = this.getCorrespondingPortal(g, (Portal) i);
            if (destPortal != null && i.getPosition().equals(getPlayer(g).getPosition())){
                getPlayer(g).setPositionOnly(new Position(destPortal.getPosition().getX() + dX, destPortal.getPosition().getY() + dY));
            }

        }
    }
    private void teleportThroughTimePortal(Game g){
        for (Entity i : g.getEnvironment().getAllEntities()){
            if (!(i instanceof TimeTravellingPortal))
                continue;
            TimeTravellingPortal portal = (TimeTravellingPortal) i;
            if (portal.getPosition().equals(getPlayer(g).getPosition()))
                portal.handleTimeTravelling();
        }
    }
}
