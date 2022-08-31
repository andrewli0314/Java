package dungeonmania;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import dungeonmania.DungeonObjects.StaticEntites.Boulder;
import dungeonmania.DungeonObjects.StaticEntites.FloorSwitch;

import org.json.JSONArray;
import org.json.JSONObject;

import static dungeonmania.Patterns.Factories.GoalFactory.buildGoal;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.Assassin;
import dungeonmania.DungeonObjects.BattleEntities.BattleEntity;
import dungeonmania.DungeonObjects.BattleEntities.Mercenary;
import dungeonmania.DungeonObjects.Battles.Battle;
import dungeonmania.DungeonObjects.BuildableEntities.Bow;
import dungeonmania.DungeonObjects.BuildableEntities.MidnightArmour;
import dungeonmania.DungeonObjects.BuildableEntities.Sceptre;
import dungeonmania.DungeonObjects.BuildableEntities.Shield;
import dungeonmania.Interfaces.InteractableInterface;
import dungeonmania.Interfaces.MovingInterface;
import dungeonmania.Patterns.Composite.Goals.DefaultGoal;
import dungeonmania.Patterns.Factories.EntityFactory;
import dungeonmania.Patterns.observers.MapObserver;
import dungeonmania.Patterns.observers.Observer;
import dungeonmania.Patterns.observers.ObstacleObserver;
import dungeonmania.Patterns.observers.PlayerObserver;
import dungeonmania.Patterns.strategy.MovementStrategy;
import dungeonmania.Patterns.strategy.StalkingStrategy;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game implements Subject, Serializable{
    
    private String dungeonId;
    private static int numDungeons;
    private String dungeonName;
    private int tick;

    private DefaultGoal goals;

    private EntitiesManager environment;
    private Player player;
    private String config;
    private Spawner spawner;

    private List<Battle> battles = new ArrayList<>();

    private List<String> buildables = Arrays.asList("bow", "shield", "sceptre", "midnight_armour");

    private MapObserver mapObserver = new MapObserver();
    private ObstacleObserver obstacleObserver = new ObstacleObserver();
    private PlayerObserver playerObserver = new PlayerObserver();
    private List<Observer> listObservers = new ArrayList<>(List.of(obstacleObserver, mapObserver, playerObserver));
    

    public Game(String dungeonName, JSONObject dungeon, JSONObject config) {
        
        this.dungeonName = dungeonName;
        this.dungeonId = String.valueOf(numDungeons);
        this.config = config.toString();
        environment = new EntitiesManager();

        JSONArray ent = dungeon.getJSONArray("entities");

        for (int i = 0; i < ent.length(); i++){
            environment.addEntityToManager(EntityFactory.buildEntityFromJSON(ent.getJSONObject(i), config));
        }
        player = environment.getPlayer();
        spawner = new Spawner(environment, config);

        // Build goal from goal json
        JSONObject goalJSON = (JSONObject) dungeon.getJSONObject("goal-condition");
        this.goals = buildGoal(goalJSON, environment);

        this.initEntityMovementStrategy("mercenary", new StalkingStrategy<Mercenary>(this));
        this.initEntityMovementStrategy("assassin", new StalkingStrategy<Assassin>(this));

    }

    public <E extends Entity & MovingInterface> void initEntityMovementStrategy(String type, MovementStrategy<E> movementStrategy){
        for (Entity e : this.getEnvironment().getAllEntities()){
            if (!e.getType().equals(type) || !(e instanceof MovingInterface))
                continue;
            E movingEntity = (E) e;
            movingEntity.setMovementStrategy(movementStrategy);
            movementStrategy.setEntity(movingEntity);
        }
    }

    public String getDungeonId() {
        return this.dungeonId;
    }

    public String getDungeonName() {
        return this.dungeonName;
    }

    public Player getPlayer(){
        return this.player;
    }

    public EntitiesManager getEnvironment(){
        return this.environment;
    }

    public List<EntityResponse> getEntityResponseList() {
        return environment.getAllEntitiesResponseList();
    }

    public List<BattleResponse> getBattleResponseList() {
        List<BattleResponse> battlesResponseList = new ArrayList<>();
        for (Battle battle : battles) {
            battlesResponseList.add(battle.getBattleResponse());
        }
        return battlesResponseList;
    }

    public List<ItemResponse> getInventoryResponseList() {
        return player.getInventoryResponseList();
    }

    public List<String> getBuildables() {
        List<String> buildablesList = new ArrayList<>();
        for (String buildable : buildables) {
            if (canPlayerBuildEntity(buildable)) {
                buildablesList.add(buildable);
            }
        }
        return buildablesList;
    }

    public String getGoals() {
        return goals.getGoalString();
    }


    public void tick(Entity item) {
        // Increase tick
        tick += 1;
        // BUILD ITEM
        player.useItem(item, environment);
        this.notifyObservers();
        updateEnvironment();
        this.notifyObservers();
    }    

    public void tick(Direction movementDirection) {
        // Increase tick
        tick += 1;
        // MOVE PLAYER
        player.movePlayer(environment, movementDirection);

        checkForBattle(battles, new JSONObject (config));

        updateEnvironment();
        
        // Since player has moved to new location, remove entities
        // from player's inventory present in environment.
        // This is done at the end to avoid concurrentModification
        // during uupdates for all entities.
        environment.removeDuplicates(player.getInventory());
        this.notifyObservers();
    }

    public void buildItem(String buildable) {
        // BUILD ITEM
        player.buildItem(buildable, new JSONObject (config));
    }    


    public void updateEnvironment() {
        // UPDATE ALL ENTITIES
        environment.updateAllEntities();
        // SPAWN ENTITES
        spawner.spawnEnitites(tick, new JSONObject (config));
        // CHECK FOR NEW BATTLES
        checkForBattle(battles, new JSONObject (config));
        // TICK BUFF DURATION
        player.tickBuff();
    }


    public boolean canPlayerBuildEntity(String buildable) {
        EntitiesManager inventory = player.getInventory();

        if (buildable.equals("bow")) {
            return Bow.canBeBuilt(inventory);
        }
        if (buildable.equals("shield")) {
            return Shield.canBeBuilt(inventory);
        }
        if (buildable.equals("sceptre")) {
            return Sceptre.canBeBuilt(inventory);
        }
        if (buildable.equals("midnight_armour")) {
            return MidnightArmour.canBeBuilt(inventory, environment);
        }
        return true;
    }

    public Entity getPlayerItem(String id) {
        return player.getItem(id);
    }


    public List<String> getAllPossibleBuildables() {
        return this.buildables;
    }


    @Override
    public void registerObserver(Observer o) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void removeObserver(Observer o) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void notifyObservers() {
        for (Observer obs: listObservers){
            obs.update(this);
        }        
    }

    public void checkForBattle(List<Battle> battles, JSONObject config) {
        List<Entity> deadEntities = new ArrayList<>();
        Position playerPos = player.getPosition();
        for (Entity entity : environment.getAllEntities()) {
            // If entity is on the same position as player and 
            // can be battled, then create a new battle.
            if (entity instanceof BattleEntity && playerPos.equals(entity.getPosition())) {

                BattleEntity enemy = (BattleEntity) entity;
                if (enemy.getIsAlly()) {
                    // If enemy is an ally they should not be battled
                    continue;
                }

                Battle newBattle = new Battle(player, enemy, config);

                List<Entity> killedEntities = newBattle.getKilledEntities();
                deadEntities.addAll(killedEntities);
                battles.add(newBattle);

                // If player is killed, remove them from the environment
                // and end all battles
                if (killedEntities.contains(player)) {
                    environment.removePlayer();
                    break;
                }
            }
        } 
        // Remove all dead entities from environment
        for (Entity deadEntity : deadEntities) {
            environment.removeEntityFromManager(deadEntity);
        }
    }


    public Entity getInteractableEntity(String entityId) {
        return environment.getEntity(entityId);
    }

    public void interact(InteractableInterface entity) {
        entity.interact(player, environment);
    }

    public ObstacleObserver getObstacleObserver(){
        return this.obstacleObserver;
    }

    public Game saveTick() throws IOException, ClassNotFoundException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(this);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream in = new ObjectInputStream(bis);
        Game savedTick = (Game) in.readObject();
        return savedTick;
    }

    public void setPlayer(Player newPlayer){
        this.environment.addEntityToManager(this.player);
        this.player = newPlayer;
        this.environment.addEntityToManager(this.player);
    }
}
