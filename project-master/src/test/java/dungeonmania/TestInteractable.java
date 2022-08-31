package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.countEntityOfType;
import static dungeonmania.TestUtils.getEntitiesOfType;

import dungeonmania.response.models.BattleResponse;
import static dungeonmania.TestBattle.assertBattleCalculations;

public class TestInteractable {

    @Test
    @DisplayName("Interactable Test 1 - IllegalArgumentException thrown when id of character does not exist")
    public void testIdOfEntityDoesNotExist() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_OnlyPlayer", "c_movementTest_testMovementDown");
        String invalidId = getPlayer(res).get().getId() + "a";
        assertThrows(IllegalArgumentException.class, () -> dmc.interact(invalidId));
    }

    @Test
    @DisplayName("Interactable Test 2 - Test IllegalArgumentException thrown when id of entity is not an interactable entity")
    public void testIdOfEntityIsNotInteractable() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest2", "c_movementTest_testMovementDown");

        // Player moves down and picks up sword, which is not interactable
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());
        String swordId = getInventory(res, "sword").get(0).getId();


        assertThrows(IllegalArgumentException.class, () -> dmc.interact(swordId));
    }

    @Test
    @DisplayName("Interactable Test 3 - Test InvalidActionException is thrown when player does not have a weapon")
    public void testCannotInteractWithZTSpawnerNoWeapon() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest3", "c_movementTest_testMovementDown");

        String idZTS = getEntities(res, "zombie_toast_spawner").get(0).getId();

        assertThrows(InvalidActionException.class, () -> dmc.interact(idZTS));
    }


    @Test
    @DisplayName("Interactable Test 4 - Test InvalidActionException is thrown when player has a weapon but is not cardiannly adjacent")
    public void testPlayerCannotInteractWIthSpawnerNotCardinallyAdjacent() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest4", "c_movementTest_testMovementDown");

        // Player moves down and picks up sword, but is not cardinally adjacent
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());

        String idZTS = getEntities(res, "zombie_toast_spawner").get(0).getId();

        assertThrows(InvalidActionException.class, () -> dmc.interact(idZTS));
    }


    @Test
    @DisplayName("Interactable Test 5 - Test player breaks spawner")
    public void testPlayerBreaksSpawner() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest5", "c_testSpawn");

        // Player moves down and picks up sword, but is not cardinally adjacent
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());

        String idZTS = getEntities(res, "zombie_toast_spawner").get(0).getId();
        // Player breaks spawner
        assertDoesNotThrow(() -> dmc.interact(idZTS));
        // Spawner should no longer be in environment
        res = dmc.getDungeonResponseModel();
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 0);

        // Zombies should not every spawn
        for (int i = 1; i <= 100; i++) {
            res = dmc.tick(Direction.DOWN);
            assertEquals(countEntityOfType(res, "zombie_toast"), 0);
        }
    }


    @Test
    @DisplayName("Interactable Test 6 - Test invalid action when player does not have enough gold to bribe")
    public void testNotEnoughGoldToBribe() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest6", "c_testSpawn");

        assertEquals(countEntityOfType(res, "player"), 1);
        String idMerc = getEntities(res, "mercenary").get(0).getId();
        // Player and mercenary are nex to each other but
        // player does not have enough gold
        assertThrows(InvalidActionException.class, () -> dmc.interact(idMerc));
    }

    @Test
    @DisplayName("Interactable Test 7 - Test invalid action when player is not within range of mercenary bribe radius")
    public void testPlayerAndMercNotInRange() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest7", "c_testBribeAmount0");

        assertEquals(countEntityOfType(res, "player"), 1);
        String idMerc = getEntities(res, "mercenary").get(0).getId();

        // Player has enough gold to bribe merc, but player
        // is not in range of merc should throw invalid action exception
        assertThrows(InvalidActionException.class, () -> dmc.interact(idMerc));
    }

    @Test
    @DisplayName("Interactable Test 8 - Test not invalid if player is within range and has enough treasure to bribe")
    public void testPlayerBribesSuccess() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest8", "c_testBribeAmount0");

        assertEquals(countEntityOfType(res, "player"), 1);
        String idMerc = getEntities(res, "mercenary").get(0).getId();

        // Player is in range, and treasure required is set to 0
        // no error should be thrown.
        assertDoesNotThrow(() -> dmc.interact(idMerc));
        res = dmc.getDungeonResponseModel();
        boolean isMercInteractable = getEntitiesOfType(res, "mercenary").get(0).isInteractable();
        assertEquals(isMercInteractable, false);
    }


    @Test
    @DisplayName("Interactable Test 9 - Test player bribes mercenary loses treasure")
    public void testPlayerBribeSuccessLosesTreasure() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest9", "c_testBribeAmount5");

        assertEquals(countEntityOfType(res, "player"), 1);
        String idMerc = getEntities(res, "mercenary").get(0).getId();

        // Player moves down to pick up 5 treasure
        res = dmc.tick(Direction.DOWN);
        assertEquals(5, getInventory(res, "treasure").size());

        // Player can now bribe mercenary for 5 treasure
        assertDoesNotThrow(() -> dmc.interact(idMerc));
        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "treasure").size());

        boolean isMercInteractable = getEntitiesOfType(res, "mercenary").get(0).isInteractable();
        assertEquals(isMercInteractable, false);
    }


    @Test
    @DisplayName("Interactable Test 10 - Test player bribes merc with sceptre")
    public void testPlayerBribesMercWithSceptre() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest10", "c_interactableTest10");

        assertEquals(countEntityOfType(res, "player"), 1);
        String idMerc = getEntities(res, "mercenary").get(0).getId();

        // Player moves down to pick up collectables for Sceptre
        res = dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "sceptre").size());  
        // Player bribes merc with sceptre in order
        // bribe -> wait 1 tick for bribe to fade -> bribe again
        
        for (int i = 1; i < 3; i ++) {
            boolean expectedInteractable;
            res = dmc.tick(Direction.DOWN);

            if (i % 2 != 0) {
                assertDoesNotThrow(() -> dmc.interact(idMerc));
            }
            // On every even tick player does not bribe
            // and thus the merc should be interactable
            if (i % 2 != 0) {
                expectedInteractable = false;
            } else {
                expectedInteractable = true;
            }

            res = dmc.getDungeonResponseModel();
            boolean isMercInteractable = getEntitiesOfType(res, "mercenary").get(0).isInteractable();
            assertEquals(isMercInteractable, expectedInteractable);
        }
    }

    @Test
    @DisplayName("Interactable Test 11 - Test player bribes assassin with sceptre")
    public void testPlayerBribesAssassinWithSceptre() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest11", "c_interactableTest10");

        assertEquals(countEntityOfType(res, "player"), 1);
        String assassinId = getEntities(res, "assassin").get(0).getId();

        // Player moves down to pick up collectables for Sceptre
        res = dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "sceptre").size());  
        // Player bribes merc with sceptre in order
        // bribe -> wait 1 tick for bribe to fade -> bribe again
        
        for (int i = 1; i < 100; i ++) {
            boolean expectedInteractable;
            res = dmc.tick(Direction.DOWN);
            // Player should be able to bribe assassin even in fail rate is 100%
            if (i % 2 != 0) {
                assertDoesNotThrow(() -> dmc.interact(assassinId));
            }
            // On every even tick player does not bribe
            // and thus the merc should be interactable
            if (i % 2 != 0) {
                expectedInteractable = false;
            } else {
                expectedInteractable = true;
            }
            
            res = dmc.getDungeonResponseModel();
            boolean isMercInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
            assertEquals(isMercInteractable, expectedInteractable);
        }
    }


    @Test
    @DisplayName("Interactable Test 12 - Test player can fight assassin after mind control duration stops")
    public void testPlayerFightsAfterMindControlOver() {
    DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_interactableTest12", "c_interactableTest10");

        assertEquals(countEntityOfType(res, "player"), 1);
        String assassinId = getEntities(res, "assassin").get(0).getId();

        // Player moves down to pick up collectables for Sceptre
        res = dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.build("sceptre"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "sceptre").size());  
        
        // Player should bribe assassin and now they are no longer
        // interactable
        assertDoesNotThrow(() -> dmc.interact(assassinId));
        res = dmc.getDungeonResponseModel();
        boolean isAssassinInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
        assertEquals(isAssassinInteractable, false);

        // After one tick they should move to assassin who 
        // is an ally and shouldn't be attacked
        res = dmc.tick(Direction.DOWN);
        isAssassinInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
        assertEquals(isAssassinInteractable, true);

        // Player moves down and can now attack assassin
        res = dmc.tick(Direction.UP);
        BattleResponse battle = res.getBattles().get(0);
        assertBattleCalculations("assassin", battle, true, "c_interactableTest10");
    }

}
