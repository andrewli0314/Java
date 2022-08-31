package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.countEntityOfType;
import static dungeonmania.TestUtils.getValueFromConfigFile;
import static dungeonmania.TestUtils.getEntitiesOfType;
import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestBattle.assertBattleCalculations;

public class TestPersistence {
    @Test
    @DisplayName("Persistence Test 0 - Test load game throws IllegalArgumentException if file does not exist") 
    public void testLoadGameFileDoesNotExist() {

        String configFilePath = "c_movementTest_testMovementDown";
        String dungeonFilePath = "d_persistenceTest1";
        String gameName = "test";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame(gameName));
    }


    @Test
    @DisplayName("Persistence Test 1 - Test simple save and load with player") 
    public void testSimpleSaveLoadOnlyPlayer() {

        String configFilePath = "c_movementTest_testMovementDown";
        String dungeonFilePath = "d_persistenceTest1";
        String gameName = "game1";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);
        assertEquals(countEntityOfType(res, "player"), 1);
        EntityResponse initPlayer = getPlayer(res).get();
        // Init player starts at position 1, 1
        assertTrue(initPlayer.getPosition().equals(new Position(1, 1)));
        dmc.saveGame(gameName);
        // Player moves down 1 tick
        res = dmc.tick(Direction.DOWN);
        initPlayer = getPlayer(res).get();
        assertTrue(initPlayer.getPosition().equals(new Position(1, 2)));

        // Game loaded should have player back at position 1,1
        res = dmc.loadGame(gameName);
        initPlayer = getPlayer(res).get();
        assertTrue(initPlayer.getPosition().equals(new Position(1, 1)));
    }


    @Test
    @DisplayName("Persistence Test 2 - Test player collects sword, no longer has item after load") 
    public void testSwordLostAfterReset() {

        String configFilePath = "c_movementTest_testMovementDown";
        String dungeonFilePath = "d_persistenceTest2";
        String gameName = "game1";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        EntityResponse player = getPlayer(res).get();
        assertEquals(0, getInventory(res, "sword").size());
        dmc.saveGame(gameName);
        // Player moves down and picks up sword
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(1, getInventory(res, "sword").size());
        assertTrue(player.getPosition().equals(new Position(1, 2)));

        // Game reloasd to previous state and player should no longer have sword
        res = dmc.loadGame(gameName);
        player = getPlayer(res).get();
        assertEquals(0, getInventory(res, "sword").size());
    }
    

    @Test
    @DisplayName("Persistence Test 3 - Test that if player reloads from battle, the enemy should be back") 
    public void testReloadAfterBattle() {

        String configFilePath = "c_movementTest_testMovementDown";
        String dungeonFilePath = "d_persistenceTest3";
        String gameName = "game1";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        EntityResponse player = getPlayer(res).get();
        assertEquals(1, countEntityOfType(res, "spider"));
        dmc.saveGame(gameName);
        // Player moves down and fights spider
        res = dmc.tick(Direction.DOWN);
        BattleResponse battle = res.getBattles().get(0);
        assertBattleCalculations("spider", battle, true, configFilePath);
        assertEquals(countEntityOfType(res, "spider"), 0);

        // Game reloasd to previous state and should not have kileld spider
        res = dmc.loadGame(gameName);
        player = getPlayer(res).get();
        assertEquals(countEntityOfType(res, "spider"), 1);
        assertEquals(res.getBattles().size(), 0);
    }


    @Test
    @DisplayName("Persistence Test 4 - Test player builds item, item should be lost after reload") 
    public void testReloadAfterBuildingItem() {

        String configFilePath = "c_bossTest1";
        String dungeonFilePath = "d_persistenceTest4";
        String gameName = "game1";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        dmc.saveGame(gameName);
        // Player moves and picks up items
        res = dmc.tick(Direction.DOWN);
        // Player builds item
        assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "midnight_armour").size());
        
        // Game reloasd to previous state and should not built armour
        res = dmc.loadGame(gameName);
        assertEquals(0, getInventory(res, "midnight_armour").size());
        res = dmc.tick(Direction.DOWN);
    }


    @Test
    @DisplayName("Persistence Test 5 - Test player breaks spawner, spawner should be back after reload") 
    public void testReloadAfterBreakingSpawner() {

        String configFilePath = "c_bossTest1";
        String dungeonFilePath = "d_persistenceTest5";
        String gameName = "game1";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        res = dmc.saveGame(gameName);
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 1);
        // Player moves and picks up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());
        String idZTS = getEntities(res, "zombie_toast_spawner").get(0).getId();
        // Player breaks spawner
        assertDoesNotThrow(() -> dmc.interact(idZTS));
        res = dmc.getDungeonResponseModel();
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 0);
        
        // Game reloasd to previous state and should not have broken spawner and picked up sword
        res = dmc.loadGame(gameName);
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 1);
    }

    @Test
    @DisplayName("Persistence Test 6 - Test zombies spawn, after reload they should be gone from map") 
    public void testReloadAfterSpiderSpawns() {

        String configFilePath = "c_persistenceTest6";
        String dungeonFilePath = "d_persistenceTest6";
        String gameName = "game1";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        res = dmc.saveGame(gameName);
        assertEquals(countEntityOfType(res, "zombie_toast"), 0);
        for (int i = 0; i < 20; i ++) {
            res = dmc.tick(Direction.DOWN);
        }
        // 20 zombiexs spawn
        assertEquals(countEntityOfType(res, "zombie_toast"), 20);
        
        // Game reloasd to previous state and zombies should not have spawned
        res = dmc.loadGame(gameName);
        assertEquals(countEntityOfType(res, "zombie_toast"), 0);
    }

    @Test
    @DisplayName("Persistence Test 7 - Test multiple Games saved are shown in list") 
    public void testMultipleGamesSaves() {

        String configFilePath = "c_persistenceTest6";
        String dungeonFilePath = "d_persistenceTest6";
        String gameName = "game";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        for (int i = 0; i < 10; i++) {
            dmc.tick(Direction.DOWN);
            dmc.saveGame(gameName + String.valueOf(i));
        }
        res = dmc.getDungeonResponseModel();
        // Multiple games should be saved and should be in all games repsonse list
        for (int i = 0; i < 10; i++) {
            assertTrue(dmc.allGames().contains(gameName + String.valueOf(i) + ".json"));
        }
    }
    /* 
    @Test
    @DisplayName("Persistence Test 7 - Test bribe mercenary, no longer bribed after reload") 
    public void testReloadAfterBribing() {

        String configFilePath = "c_persistenceTest7";
        String dungeonFilePath = "d_persistenceTest7";
        String gameName = "game1";
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame(dungeonFilePath, configFilePath);

        res = dmc.saveGame(gameName);
        assertEquals(countEntityOfType(res, "mercenary"), 1);
        // Player bribes mercenary 
        String idMerc = getEntities(res, "mercenary").get(0).getId();
        assertDoesNotThrow(() -> dmc.interact(idMerc));
        // Merc should no longer be interactable
        res = dmc.getDungeonResponseModel();
        boolean isMercInteractable = getEntitiesOfType(res, "mercenary").get(0).isInteractable();
        assertEquals(isMercInteractable, false);
        // If player moves down to mercenary there should be no fight
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "mercenary"), 1);

        // Game reloasd to before the bribe
        res = dmc.loadGame(gameName);
        // Merc should be bribable
        isMercInteractable = getEntitiesOfType(res, "mercenary").get(0).isInteractable();
        assertEquals(isMercInteractable, true);
        // If player moves down they should fight the merc since it is no longer bribed
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "mercenary"), 0);
    }
    */
}
