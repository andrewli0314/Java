package dungeonmania;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import static dungeonmania.TestUtils.countEntityOfType;
import static dungeonmania.TestUtils.getEntitiesOfType;
import static dungeonmania.TestUtils.getInventory;

public class TestGoals {
    @Test
    @DisplayName("Goal Test 1 - Test Player reaches exit goal")
    public void testExitGoalSimple() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest1", "c_movementTest_testMovementDown");

        assertTrue(res.getGoals().contains(":exit"));
        // Player walks down to exit, goals should be empty
        res = dmc.tick(Direction.DOWN);
        assertTrue(res.getGoals().equals(""));
    }

    @Test
    @DisplayName("Goal Test 2 - Test enenmies goal simple, single enemy")
    public void testEnemiesGoalOneEnemy() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest2", "c_movementTest_testMovementDown");

        assertTrue(res.getGoals().contains(":enemies"));
        assertEquals(countEntityOfType(res, "mercenary"), 1);
        // Player walks down to fight enenmy
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "mercenary"), 0);
        assertTrue(res.getGoals().equals(""));
    }

    @Test
    @DisplayName("Goal Test 3 - Test treasure goal simple, single treasure")
    public void testTreasureGoalSingleTreasure() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest3", "c_movementTest_testMovementDown");

        assertTrue(res.getGoals().contains(":treasure"));
        assertEquals(countEntityOfType(res, "treasure"), 1);
        // Player walks down to collect treasure
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "treasure").size());
        assertTrue(res.getGoals().equals(""));
    }

    @Test
    @DisplayName("Goal Test 4 - Test boulder goal simple, single switch")
    public void testBoulderGoalSimpleSingleSwitch() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest4", "c_movementTest_testMovementDown");

        assertTrue(res.getGoals().contains(":boulders"));
        assertEquals(countEntityOfType(res, "boulder"), 1);
        assertEquals(countEntityOfType(res, "switch"), 1);
        // Player walks down to push boulder onto switch
        res = dmc.tick(Direction.DOWN);
        assertTrue(res.getGoals().equals(""));
    }

    @Test
    @DisplayName("Goal Test 5 - Test player needs to destroy spawner and all enemies to win enemies goal")
    public void testEnemiesGoalWithSpawner() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest5", "c_movementTest_testMovementDown");

        assertTrue(res.getGoals().contains(":enemies"));
        assertEquals(countEntityOfType(res, "zombie_toast"), 1);
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 1);
        // Player walks down to fight enenmy
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "zombie_toast"), 0);
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 1);
        assertTrue(res.getGoals().contains(":enemies"));
        // Player interacts with zombie toast spawner to destroy it
        // since player has sword
        assertEquals(1, getInventory(res, "sword").size());
        String ZTId = getEntitiesOfType(res, "zombie_toast_spawner").get(0).getId();

        assertDoesNotThrow(() -> dmc.interact(ZTId));
        res = dmc.getDungeonResponseModel();
        assertTrue(res.getGoals().equals(""));
    }

    @Test
    @DisplayName("Goal Test 6 - Test OR Goal does not need to fulfill both conditions to succeed")
    public void testORGoalSuccessfulIfOneIsComplete() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_goalTest6", "c_movementTest_testMovementDown");

        assertTrue(res.getGoals().contains(":enemies"));
        assertTrue(res.getGoals().contains(":treasure"));
        assertEquals(countEntityOfType(res, "zombie_toast"), 3);
        assertEquals(countEntityOfType(res, "treasure"), 1);
        // Player walks down to pick up treasure
        res = dmc.tick(Direction.DOWN);
        // Althouygh there are still zombies, the goal is still
        // complete as player has picked up treasure
        assertEquals(countEntityOfType(res, "zombie"), 3);
        assertEquals(countEntityOfType(res, "treasure"), 0);
        assertTrue(res.getGoals().equals(""));

    }

    

}
