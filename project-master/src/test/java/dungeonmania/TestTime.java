package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.countEntityOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class TestTime {
    @Test
    @DisplayName("Time test 1 - Test time turner can be collected")
    public void testTimeTurnerAddedToInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest1", "c_movementTest_testMovementDown");

        assertEquals(countEntityOfType(res, "time_turner"), 1);
        // Picks up time turner
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "time_turner"), 0);
        assertEquals(1, getInventory(res, "time_turner").size());
    }

    @Test
    @DisplayName("Time test 2 - Test time travelling portal initialised")
    public void testTimeTravellingPortalInEnvironment() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest2", "c_movementTest_testMovementDown");

        assertEquals(countEntityOfType(res, "time_travelling_portal"), 1);
    }

    @Test
    @DisplayName("Time test 3 - IllegalArgumentException if ticks is <= 0")
    public void testRewind0ticks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest1", "c_movementTest_testMovementDown");

        res = dmc.tick(Direction.DOWN);
        // Player picks up time turner
        assertEquals(1, getInventory(res, "time_turner").size());

        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(0));
    }

    @Test
    @DisplayName("Time test 4 - IllegalArgumentException if ticks exceeds total number of ticks")
    public void testRewindticksExceedTotalTicks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest1", "c_movementTest_testMovementDown");
        // One tick has occurred
        res = dmc.tick(Direction.DOWN);
        // Player picks up time turner
        assertEquals(1, getInventory(res, "time_turner").size());

        // 2 ticks hasn't occurred so illegal argument exception is thrown
        assertThrows(IllegalArgumentException.class, () -> dmc.rewind(2));
    }

    @Test
    @DisplayName("Time test 5 - Items are back in environment after 1 tick rewind")
    public void testRewind1TickItemsReturn() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest1", "c_movementTest_testMovementDown");
        // One tick has occurred

        EntityResponse initPlayer = getPlayer(res).get();
        res = dmc.tick(Direction.DOWN);
        // Player picks up time turner 
        initPlayer = getPlayer(res).get();
        assertEquals(countEntityOfType(res, "time_turner"), 0);
        assertEquals(1, getInventory(res, "time_turner").size());

        // If player tries to rewind 1 tick then there should be a time turner 
        assertDoesNotThrow(() -> dmc.rewind(1));
        res = dmc.getDungeonResponseModel();
        initPlayer = getPlayer(res).get();
        assertEquals(countEntityOfType(res, "time_turner"), 1);
    }
    @Test
    @DisplayName("Time test 6 - Items are back after 5 ticks")
    public void testRewindSimplePlayerGoesBackwards() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest6", "c_movementTest_testMovementDown");

        EntityResponse initPlayer = getPlayer(res).get();
        int numTimeTurners = 0;
        // Player picks up time turners after each tick
        for (int i = 0; i < 5; i++) {
            numTimeTurners += 1;
            res = dmc.tick(Direction.DOWN);
            assertEquals(countEntityOfType(res, "time_turner"), 5 - numTimeTurners);
            assertEquals(numTimeTurners, getInventory(res, "time_turner").size());
        }
        // Player has picked up all the time turners
        initPlayer = getPlayer(res).get();
        assertEquals(countEntityOfType(res, "time_turner"), 0);
        assertEquals(5, getInventory(res, "time_turner").size());

        // If player resets backwards 5 ticks, the time turners should be back
        assertDoesNotThrow(() -> dmc.rewind(5));
        res = dmc.getDungeonResponseModel();
        initPlayer = getPlayer(res).get();
        assertEquals(countEntityOfType(res, "time_turner"), 5);
    }
    
}
