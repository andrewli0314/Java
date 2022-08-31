package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import static dungeonmania.TestUtils.getInventory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;


public class TestBuildable {
    @Test
    @DisplayName("Build Test 1 - Test IllegalArgumentException for non bow/shield/sceptre/midnight_armour string")
    public void testIllegalArgumentException() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildableComponents", "c_movementTest_testMovementDown");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should now have available items to bield
        // 1 bow and 1 shield
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(3, getInventory(res, "wood").size());
        assertEquals(3, getInventory(res, "arrow").size());

        assertThrows(IllegalArgumentException.class, () -> dmc.build(""));
        assertThrows(IllegalArgumentException.class, () -> dmc.build("bowe"));
        assertThrows(IllegalArgumentException.class, () -> dmc.build("shiel"));
    }


    @Test
    @DisplayName("Build Test 2 - Test InvalidActionException is thrown if item cannot be built")
    public void testNotEnoughMaterialsToBuild() {
        DungeonManiaController dmc = new DungeonManiaController();
        dmc.newGame("d_OnlyPlayer", "c_movementTest_testMovementDown");

        // Since there is only player and not items in dungeon
        // no item can be built 
        assertThrows(InvalidActionException.class, () -> dmc.build("bow"));
        assertThrows(InvalidActionException.class, () -> dmc.build("shield"));
        assertThrows(InvalidActionException.class, () -> dmc.build("sceptre"));
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));           
    }

    @Test
    @DisplayName("Build Test 3 - Player can build input entity")
    public void testBuildEntity() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildableComponents", "c_movementTest_testMovementDown");


        // Since player has no items in inventory currently, their buildables list
        // should be empty
        assertTrue(res.getBuildables().equals(new ArrayList<>()));

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should now have available items to bield
        // 1 bow and 1 shield
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(3, getInventory(res, "wood").size());
        assertEquals(3, getInventory(res, "arrow").size());

        // buildables response should now also contain "bow" and "shield"
        assertTrue(res.getBuildables().contains("bow"));
        assertTrue(res.getBuildables().contains("shield"));

        assertDoesNotThrow(() -> dmc.build("bow"));
        // After building bow, the buildables response should no longer contain bow

        res = dmc.getDungeonResponseModel();
        assertFalse(res.getBuildables().contains("bow"));

        assertDoesNotThrow(() -> dmc.build("shield"));
        // Similarly, after building shield, the buildables response should no longer contain shield
        // and so nothing should be left in buildables list
        res = dmc.getDungeonResponseModel();
        assertTrue(res.getBuildables().equals(new ArrayList<>()));

        res = dmc.getDungeonResponseModel();

        // Bow and shield should be built
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(1, getInventory(res, "shield").size());

        // Player should have used the materials to build items
        assertEquals(0, getInventory(res, "treasure").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "arrow").size());

    }

    @Test
    @DisplayName("Build Test 4 - Player can build shield with key")
    public void testBuildShieldWithKey() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildShieldWithKey", "c_movementTest_testMovementDown");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should be able to build shield with 2 wood + 1 key
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(2, getInventory(res, "wood").size());

        assertDoesNotThrow(() -> dmc.build("shield"));

        res = dmc.getDungeonResponseModel();

        assertEquals(1, getInventory(res, "shield").size());
        // Player should have used the materials to build items
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(0, getInventory(res, "wood").size());
    }

    @Test
    @DisplayName("Build Test 5 - Player can build shield with sun stone and sun stone is kept")
    public void testBuildShieldWithSunStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest5", "c_movementTest_testMovementDown");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should be able to build shield with 2 wood + 1 Sun stone
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(2, getInventory(res, "wood").size());

        assertDoesNotThrow(() -> dmc.build("shield"));

        res = dmc.getDungeonResponseModel();

        assertEquals(1, getInventory(res, "shield").size());
        // Player should have kept the sun stone
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "wood").size());
    }

    ////////////
    // Sceptre 
    ////////////

    @Test
    @DisplayName("Build Test 6 - Player can build Sceptre with wood, key and sunstone")
    public void testBuildSceptreWoodKeySunStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest6", "c_bossTest1");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should be able to build sceptre with 
        // 1 wood + 1 key + 1 sun stone
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(1, getInventory(res, "key").size());

        assertDoesNotThrow(() -> dmc.build("sceptre"));

        res = dmc.getDungeonResponseModel();

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(0, getInventory(res, "key").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
    }

    @Test
    @DisplayName("Build Test 7 - Player can build Sceptre with arrows, treasure and sunstone")
    public void testBuildSceptreArrowsTreasureSunStone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest7", "c_bossTest1");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should be able to build sceptre with 
        // 2 arrows + 1 treasure + 1 sun stone
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "treasure").size());

        assertDoesNotThrow(() -> dmc.build("sceptre"));

        res = dmc.getDungeonResponseModel();

        assertEquals(1, getInventory(res, "sceptre").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "arrow").size());
        assertEquals(0, getInventory(res, "treasure").size());
    }

    @Test
    @DisplayName("Build Test 7 - Player can build Sceptre with wood and two sun stones")
    public void testBuildSceptreWoodSunStones() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest8", "c_bossTest1");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should be able to build sceptre with 
        // 1 wood + 2 sun stone
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "wood").size());

        assertDoesNotThrow(() -> dmc.build("sceptre"));

        res = dmc.getDungeonResponseModel();

        assertEquals(1, getInventory(res, "sceptre").size());
        // Player should have kept one sun stone
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "wood").size());
    }


    @Test
    @DisplayName("Build Test 9 - Player can build Sceptre with wood and two sun stones and does not use arrows")
    public void testBuildSceptreDoesNotUseWood() {
        // Tests that if player has picked up an arrow first
        // then key, then arrows, the wood is used first
        // and arrows are left unused
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest9", "c_bossTest1");
        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should be able to build sceptre with 
        // 1 wood + 2 sun stone
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "arrow").size());

        assertDoesNotThrow(() -> dmc.build("sceptre"));

        res = dmc.getDungeonResponseModel();

        assertEquals(1, getInventory(res, "sceptre").size());
        // Player should have kept one sun stone
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "arrow").size());
    }


    ////////////////////
    // Midnight armour
    ////////////////////
    @Test
    @DisplayName("Build Test 10 - Player cannot build midnight armour if zombie toast is present")
    public void testBuildMidnightArmourZombieInDungeon() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest10", "c_bossTest1");
        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());

        // Player now has materials for sun stone, but zombie
        // is in dungeon, so invalid action should be thrown
        assertThrows(InvalidActionException.class, () -> dmc.build("midnight_armour"));    
    }

    @Test
    @DisplayName("Build Test 11 - Player builds midnight armour")
    public void testBuildMidNightArmourSuccess() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest11", "c_bossTest1");
        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());

        // Player should be able to build midnight armour
        // as there are no zombies
        assertDoesNotThrow(() -> dmc.build("midnight_armour"));

        res = dmc.getDungeonResponseModel();

        assertEquals(1, getInventory(res, "midnight_armour").size());
        assertEquals(0, getInventory(res, "sun_stone").size());
        assertEquals(0, getInventory(res, "sword").size());  
    }


    @Test
    @DisplayName("Build Test 12 - Test buildables response contains bow, shield, midnight_armour and sceptre")
    public void testExpectedBuildablesStrings() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_buildTest12", "c_movementTest_testMovementDown");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all collectables
        }

        // Player should now have available items to bield
        // bow, shield, sceptre and midnight_armour
        assertEquals(2, getInventory(res, "sun_stone").size());
        assertEquals(2, getInventory(res, "wood").size());
        assertEquals(3, getInventory(res, "arrow").size());
        assertEquals(1, getInventory(res, "sword").size());

        // buildables response should now also contain "bow" and "shield"
        assertTrue(res.getBuildables().contains("bow"));
        assertTrue(res.getBuildables().contains("shield"));
        assertTrue(res.getBuildables().contains("midnight_armour"));
        assertTrue(res.getBuildables().contains("sceptre"));
    }
}
