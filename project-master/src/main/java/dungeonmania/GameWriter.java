package dungeonmania;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dungeonmania.DungeonObjects.BattleEntities.Assassin;
import dungeonmania.DungeonObjects.BattleEntities.BattleEntity;
import dungeonmania.DungeonObjects.BattleEntities.Mercenary;
import dungeonmania.DungeonObjects.StaticEntites.EntitySpawners.EntitySpawner;
import dungeonmania.Patterns.Composite.Goals.DefaultGoal;
import dungeonmania.Patterns.observers.Observer;
import dungeonmania.util.Position;

import static dungeonmania.util.FileLoader.listFileNamesInResourceDirectory;

public class GameWriter {

    private String directory = "./src/main/resources/";

    GameWriter() {
    }

    public void writeGame(String gameName, Game game) {

        Gson gson = generateGson();

        try {
            String filePath = generateFilePath(gameName);

            FileWriter writer = new FileWriter(filePath);

            String gameJSON = gson.toJson(game);

            writer.write(gameJSON);
            writer.close();
          
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Game readGame(String gameName) {

        Gson gson = generateGson();

        try {
            Reader reader = new FileReader(getFilePath(gameName));

            // Convert JSON File to Game
            Game loadedGame = gson.fromJson(reader, Game.class);
            
            reader.close();

			return loadedGame;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private String generateFilePath(String fileName) {
        // Check if directory folder exists
        File directoryFolder = new File(directory);
        // If folder does not exist, create folder
        if (!directoryFolder.exists()) {
            directoryFolder.mkdir();
        }
        // Return new file path
        return getFilePath(fileName);
    }

    private String getFilePath(String fileName) {
        String newFilePath = directory + fileName + ".json";
        return newFilePath;
    }

    public boolean checkFilePath(String fileName) {
        // If folder does not exist then file path does not exist
        File directoryFolder = new File(directory);
        if (!directoryFolder.exists()) {
            return false;
        }
        // If file is not in directory then file path does not exist
        File[] listOfFiles = directoryFolder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].getName().equals(fileName + ".json")) {
                    return true;
                }
            } 
        }
        return false;
    }

    public List<String> getAllGames() {
        File directoryFolder = new File(directory);
        List<String> allGames = new ArrayList<>();
        // If file is not in directory then file path does not exist
        File[] listOfFiles = directoryFolder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                allGames.add(listOfFiles[i].getName());
            }
        } 
        return allGames;
    }
    

    private Gson generateGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();  
        gsonBuilder.registerTypeAdapter(DefaultGoal.class,
        new InterfaceSerializer());
        gsonBuilder.registerTypeAdapter(Observer.class,
        new InterfaceSerializer());
        gsonBuilder.registerTypeAdapter(EntitySpawner.class,
        new InterfaceSerializer());
        return gsonBuilder.create();
    }


    public class InterfaceSerializer implements
        JsonSerializer<Object>, JsonDeserializer<Object> {

        private static final String CLASS_META_KEY = "CLASS_META_KEY";

        @Override
        public Object deserialize(JsonElement jsonElement, Type type,JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            JsonObject jsonObj = jsonElement.getAsJsonObject();
            String className = jsonObj.get(CLASS_META_KEY).getAsString();
            try {
                Class<?> clz = Class.forName(className);
                return jsonDeserializationContext.deserialize(jsonElement, clz);
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        @Override
        public JsonElement serialize(Object object, Type type,
                JsonSerializationContext jsonSerializationContext) {
            JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
            jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY,
                    object.getClass().getCanonicalName());
            return jsonEle;
        }

    }


}
