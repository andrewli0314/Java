package dungeonmania.DungeonObjects;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.DungeonManiaController;
import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.BattleEntities.BattleEntity;
import dungeonmania.DungeonObjects.BuildableEntities.BuildableEntity;
import dungeonmania.DungeonObjects.BuildableEntities.Sceptre;
import dungeonmania.DungeonObjects.CollectableEntities.Key;
import dungeonmania.DungeonObjects.CollectableEntities.Treasure;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.UsableCollectableEntity;
import dungeonmania.Patterns.Factories.EntityFactory;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends BattleEntity{

    private EntitiesManager inventory = new EntitiesManager();
    private Direction currentDirection;
    private List<Buff> buffs = new ArrayList<>();
    private List<Entity> allies = new ArrayList<>();
    private List<Position> path = null;

    public Player(Position position, String type, JSONObject config) {
        super(position, type);
        super.setAttack(config.getDouble("player_attack"));
        super.setHealth(config.getDouble("player_health"));
    }


    public void useItem(Entity item, EntitiesManager environment) {
        // Item already has been checked if it can be used hence
        // now item can be downcasted and be used.
        UsableCollectableEntity usableItem = (UsableCollectableEntity) item;
        usableItem.use(environment, inventory, this);
    }

    public void movePlayer(EntitiesManager entitiesManager, Direction movementDirection) {
        currentDirection = movementDirection;
        super.move(entitiesManager);
    }

    public void buildItem(String buildable, JSONObject config) {
        Entity buildableAsEntity = EntityFactory.buildEntity(new Position(0,0), buildable, config);
        // SInce it has been checked that buildable can be built, the new entity
        // should be a buildableEntity, and thus have the useMaterials method to use materials
        // from inventory input specific to the buildableEntity by polymorphism.
     
        BuildableEntity newBuildableEntity = (BuildableEntity) buildableAsEntity;
        newBuildableEntity.useMaterials(inventory);
        inventory.addEntityToManager(newBuildableEntity);
    }

    public void addBuff(Entity entityBuff, int duration) {
        buffs.add((new Buff(entityBuff, duration)));
    }


    public void tickBuff() {

        Buff currBuff = getCurrentBuff();

        if (currBuff == null) {
            return;
        }

        int remainingDuration = currBuff.getDuration() - 1;
        
        if (remainingDuration == 0) {
            // If duration of buff is at 0 
            // then buff is finished
            buffs.remove(currBuff);
        } else {
            currBuff.setDuration(remainingDuration);
        }
    }

    @Override
    public Position getNextPosition() {
        if (this.path == null){
            Position currPos = super.getPosition();
            // Stubs
            if (currentDirection == Direction.UP) {
                return new Position(currPos.getX(), currPos.getY() - 1);
            } 
            if (currentDirection == Direction.DOWN) {
                return new Position(currPos.getX(), currPos.getY() + 1);
            } 
            if (currentDirection == Direction.RIGHT) {
                return new Position(currPos.getX() + 1, currPos.getY());
            }
            if (currentDirection == Direction.LEFT) {
                return new Position(currPos.getX() - 1, currPos.getY());
            }
            return null;
        } else {
            if (this.path.isEmpty()){
                DungeonManiaController.getGame().getEnvironment().removeEntityFromManager(this);
            } else {
                Position newPos = this.path.get(0);
                this.path.remove(0);
                return newPos;
            }
        }
        return null;
    }

    public EntitiesManager getInventory() {
        return this.inventory;
    }

    public void addToInventory(Entity entity) {
        inventory.addEntityToManager(entity);
    }

    public void removeFromInventory(Entity entity){
        inventory.removeEntityFromManager(entity);
    }

    public List<ItemResponse> getInventoryResponseList() {
        return inventory.getItemResponseList();
    }

    public List<Entity> getInventoryEntities() {
        return inventory.getAllEntities();
    }

    public Entity getItem(String id) {
        return inventory.getEntity(id);
    }

    public boolean hasKey() {
        for (Entity entity : inventory.getAllEntities()) {
            if (entity instanceof Key) {
                return true;
            }
        }
        return false;
    }

    public Buff getCurrentBuff() {
        if (buffs.size() == 0) {
            return null;
        } else {
            return buffs.get(0);
        }
    }

    public Entity getCurrentBuffEntity() {
        // Gets the entity related to buff, e.g. Invincibilitypotion
        if (getCurrentBuff() != null) {
            return getCurrentBuff().getBuffType();
        }
        return null;
    }

    public int getNumTreasure() {
        int numTreasure = 0;
        for (Entity e : inventory.getAllEntities()) {
            if (e instanceof Treasure) {
                numTreasure += 1;
            }
        }
        return numTreasure;
    }

    public void addAlly(Entity ally) {
        allies.add(ally);
    }

    public void removeAlly(Entity ally) {
        allies.remove(ally);
    }

    public List<Entity> getAllies() {
        return this.allies;
    }


    public boolean hasSceptre() {
        for (Entity item : inventory.getAllEntities()) {
            if (item instanceof Sceptre) {
                return true;
            }
        }
        return false;
    }

    public int getMindControlDuration() {
        for (Entity item : inventory.getAllEntities()) {
            if (item instanceof Sceptre) {
                Sceptre sceptre = (Sceptre) item;
                return sceptre.getMindControlDuration();
            }
        }
        return 0;
    }

    public void setPath(List<Position> path){
        this.path = path;
    }
}
