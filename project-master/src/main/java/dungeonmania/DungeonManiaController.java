package dungeonmania;


import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.CollectableEntities.UsableEntity.UsableCollectableEntity;
import dungeonmania.Interfaces.InteractableInterface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class DungeonManiaController {

    static Game game;
    private GameWriter gameWriter = new GameWriter();
    static List<Game> ticks = new ArrayList<>();

    public String getSkin() {
        return "default";
    }

    public static List<Game> getTicks(){
        return ticks;
    }
    public static void setTicks(List<Game> newTicks){
        ticks = newTicks;
    }
    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    private void reset(){
        ticks.clear();
        game = null;
    }
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException("This dungeon Name does not exist");
        }
        if (!configs().contains(configName)) {
            throw new IllegalArgumentException("This config name does not exist");
        }

        try {
            JSONObject config = new JSONObject(FileLoader.loadResourceFile("/configs/" + configName + ".json"));
            JSONObject dungeon = new JSONObject(FileLoader.loadResourceFile("/dungeons/" + dungeonName + ".json"));
            this.reset();
            game = new Game(dungeonName, dungeon, config);
            ticks.add(game.saveTick());
            return getDungeonResponseModel();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return new DungeonResponse(game.getDungeonId(), game.getDungeonName(), game.getEntityResponseList(), game.getInventoryResponseList(), game.getBattleResponseList(), game.getBuildables(), game.getGoals());
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {

        Entity item = game.getPlayerItem(itemUsedId);
        if (item == null) {
            // Item could not be found in inventory
            throw new InvalidActionException(itemUsedId + " is an item the player does not own");
        }
        

        if (!(item instanceof UsableCollectableEntity)) {
            // If item is not a usable item throw an error
            String itemType = item.getType();
            throw new IllegalArgumentException(itemType + " cannot be used.");
        }

        game.tick(item);
        try {
            ticks.add(game.saveTick());
            if(ticks.size() > 50){
                ticks.remove(0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return getDungeonResponseModel();
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        game.tick(movementDirection);
        try {
            this.ticks.add(game.saveTick());
            if(this.ticks.size() > 50){
                this.ticks.remove(0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return getDungeonResponseModel();
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        if (!game.getAllPossibleBuildables().contains(buildable)) {
            // If buildable is not an item that can be built
            throw new IllegalArgumentException(buildable + " cannot be built.");
        }
        if (!game.canPlayerBuildEntity(buildable)) {
            // If player does not have the inventory to build the item
            throw new InvalidActionException(buildable + " cannot be built with current inventory");
        }

        game.buildItem(buildable);

        return getDungeonResponseModel();
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity entity = game.getInteractableEntity(entityId);

        if (entity == null) {
            // If null is returned, entity is not in the environment
            throw new IllegalArgumentException("Entity " + entityId + "does not exist in the environment");
        }

        if (!entity.getIsInteractable()) {
            // If entity is not interactable then 
            // illegal argument exception is thrown
            throw new IllegalArgumentException("Type " + entity.getType() + " cannot be interacted with");
        }

        InteractableInterface interactableEntity = (InteractableInterface) entity;
        if (!interactableEntity.canInteract(game.getPlayer())) {
            // If entity is an interactable entity but does not fulfill
            // the conditions to interact with it
            throw new InvalidActionException("Player cannot interact with entity " + entity.getId());
        }
        
        game.interact(interactableEntity);
        return getDungeonResponseModel();
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        // Save Entities info to file in json
        gameWriter.writeGame(name, game);
        return getDungeonResponseModel();
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        //  Check if file exists
        if (!gameWriter.checkFilePath(name)) {
            throw new IllegalArgumentException("Illegal file name");
        }
        // read json file 
        game = gameWriter.readGame(name);
        return getDungeonResponseModel();
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        return gameWriter.getAllGames();
    }

    public static void setGame(Game g){
        game = g;
    }
    public static Game getGame(){
        return game;
    }

    public DungeonResponse rewind(int parseInt) throws IllegalArgumentException {
        if (parseInt <= 0 || parseInt > ticks.size() - 1){
            throw new IllegalArgumentException("Invalid number of ticks");
        }
        TimeTravelling.timeTravel(parseInt);
        return getDungeonResponseModel();
    }
}
