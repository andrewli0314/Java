package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getOldPlayer;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getEntitiesOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.Mercenary;
import dungeonmania.DungeonObjects.BattleEntities.Spider;
import dungeonmania.DungeonObjects.CollectableEntities.TimeTurner;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static dungeonmania.TestUtils.getInventory;
public class TestTimeTravel {
    @Test
    @DisplayName("Test controller saves previous ticks")
    public void TestTickSaving() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_OnlyPlayer", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();
        // Player starts at position 1,1 upon initialisation
        assertEquals(player.getPosition(), new Position(1,1));

        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.LEFT);
        
        assertEquals(dmc.getTicks().size(), 5);

    }

    @Test
    @DisplayName("Test time travelling with portal")
    public void TestRewindThroughPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest2", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();
        // Player starts at position 1,1 upon initialisation
        assertEquals(player.getPosition(), new Position(1,1));

        List<EntityResponse> timePortals = getEntitiesOfType(res, "time_travelling_portal");
        assertTrue(!timePortals.isEmpty());

        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        res = dmc.getDungeonResponseModel();
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,2));
        EntityResponse oldPlayer = getOldPlayer(res).get();
        assertEquals(oldPlayer.getPosition(), new Position(1,1));
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,3));
        oldPlayer = getOldPlayer(res).get();
        assertEquals(oldPlayer.getPosition(), new Position(2,1));
    }

    @Test
    @DisplayName("Test time travelling for 5 ticks")
    public void TestRewind5ticks() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_timeTest3", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();
        // Player starts at position 1,1 upon initialisation
        assertEquals(player.getPosition(), new Position(1,1));
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,6));
        assertTrue(dmc.getGame().getPlayer().getInventoryEntities().get(0) instanceof TimeTurner);
        res = dmc.rewind(5);
        EntityResponse oldPlayer = getOldPlayer(res).get();
        assertEquals(oldPlayer.getPosition(), new Position(1,1));
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,6));
        EntityResponse timeTurner = getEntitiesOfType(res, "time_turner").get(0);
        assertEquals(timeTurner.getPosition(), new Position(1,2));

    }
}
