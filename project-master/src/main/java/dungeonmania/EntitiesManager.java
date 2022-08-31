package dungeonmania;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;


public class EntitiesManager implements Serializable{

    private List<Entity> allEntities = new ArrayList<>();
    private Player player;

    public void addEntityToManager(Entity entity) {
        if (entity.getType().equals("player")) {
            this.player = (Player) entity;
        } else {
            if (entity != null)
                allEntities.add(entity);
        }
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Entity> getAllEntities() {
        // Get entities should be a copy, so the memory 
        // cannot be manipulated
        return new ArrayList<>(allEntities);
    }

    public Entity getEntity(String entityId) {
        for (Entity entity : allEntities) {
            if (entity.getId().equals(entityId)) {
                return entity;
            }
        }
        return null;
    }


    public List<EntityResponse> getAllEntitiesResponseList() {
        List<EntityResponse> entityResponseList = new ArrayList<>();

        for (Entity entity : allEntities) {
            if (entity == null)
                continue;
            entityResponseList.add(entity.entityResponse());
        }
        if (player != null) {
            entityResponseList.add(player.entityResponse());
        }

        return entityResponseList;
    }

    public void removeDuplicates(EntitiesManager entitiesToRemove) {
        // If update comes from movement tick then player may pick up an item 
        // which should be deleted.
        // Remove items added from player inventory from all entities
        for (Entity entityToDelete : entitiesToRemove.getAllEntities()) {
            removeEntityFromManager(entityToDelete);
        }
    }


    public void updateAllEntities() {
        for (Entity entity : allEntities) {
            if (entity == null)
                continue;
            entity.update(this, player);
        }
    }

    public List<ItemResponse> getItemResponseList() {
        List<ItemResponse> ItemResponseList = new ArrayList<>();
        for (Entity entity : allEntities) {
            ItemResponseList.add(entity.itemResponse());
        }
        return ItemResponseList;
    }

    public void removeEntityFromManager(Entity entity) {
        allEntities.remove(entity);
    }

    public void removePlayer() {
        player = null;
    }


}
