package dungeonmania.Patterns.observers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dungeonmania.Game;
import dungeonmania.Subject;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.util.Position;

public interface Observer extends Serializable{
    public void update(Subject s);

    default List<Entity> isOnObstacle(Game g, Entity entity, String obstacle){
        Position pos = entity.getPosition();
        List<Entity> result = new ArrayList<>();
        for (Entity e :  g.getEnvironment().getAllEntities()){
            if (!e.getType().equals(obstacle))
                continue;
            if (e.getPosition().equals(pos) && e != entity){
                result.add(e);
            }
        }
        if (result.isEmpty())
            return null;
        else
            return result;
    }
    
    default List<Entity> isOnObstacle(Game g, Entity entity){
        Position pos = entity.getPosition();
        List<Entity> result = new ArrayList<>();
        for (Entity e :  g.getEnvironment().getAllEntities()){
            if (e.getPosition().equals(pos) && e != entity){
                result.add(e);
            }
        }
        if (result.isEmpty())
            return null;
        else
            return result;
    }
}
