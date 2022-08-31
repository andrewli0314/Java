package dungeonmania.DungeonObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dungeonmania.EntitiesManager;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

public class Entity implements Serializable{

    private String id;
    private String type;
    private static int totalEntities;
    private boolean isInteractable;
    private List<Position> pastPositions = new ArrayList<>();
    
    public Entity(Position position, String type) {
        this.id = String.valueOf(totalEntities);
        totalEntities += 1;
        this.pastPositions.add(position);
        this.type = type;
        this.isInteractable = false;
    }


    public String getId() {
        return this.id;
    }

    public Position getPosition() {
        return this.pastPositions.get(this.pastPositions.size()-1);
    }

    public void setPosition(Position position) {
        this.pastPositions.add(position);
        if(pastPositions.size()>10){
            pastPositions.remove(0);
        }
    }

    public void setPositionOnly(Position position) {
        this.pastPositions.remove(this.pastPositions.size()-1);
        this.pastPositions.add(position);
        if(pastPositions.size()>10){
            pastPositions.remove(0);
        }
    }

    public void rollback(){
        if(pastPositions.size()>1)
            this.pastPositions.remove(this.pastPositions.size()-1);
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

    public boolean getIsInteractable() {
        return this.isInteractable;
    }
    
    public void setIsInteractable(boolean isInteractable) {
        this.isInteractable = isInteractable;
    }

    public EntityResponse entityResponse() {
        return new EntityResponse(id, type, getPosition(), isInteractable);
    }

    public ItemResponse itemResponse() {
        return new ItemResponse(id, type);
    }

    public void update(EntitiesManager entiitesManager, Player player) {
    }

    public Position getPrevPosition(){
        if (this.pastPositions.size() > 1)
            return this.pastPositions.get(this.pastPositions.size()-2);
        else 
            return this.pastPositions.get(this.pastPositions.size()-1);
    }
}
