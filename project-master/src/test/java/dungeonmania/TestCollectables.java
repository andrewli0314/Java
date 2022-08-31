package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.countEntityOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;


public class TestCollectables {
    @Test
    @DisplayName("Test Each item is removed from Entity Response List and added to inventory")
    public void testCollectableItemsCollected() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectableEntities", "c_movementTest_testMovementDown");
        assertEquals(countEntityOfType(res, "treasure"), 1);

        EntityResponse player;

        assertEquals(countEntityOfType(res, "treasure"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 2));
        // Picks up treasure
        assertEquals(countEntityOfType(res, "treasure"), 0);

        assertEquals(countEntityOfType(res, "key"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 3));
        // Picks up key
        assertEquals(countEntityOfType(res, "key"), 0);

        assertEquals(countEntityOfType(res, "invisibility_potion"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 4));
        // Picks up invisibility_potion
        assertEquals(countEntityOfType(res, "invisibility_potion"), 0);

        assertEquals(countEntityOfType(res, "invincibility_potion"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 5));
        // Picks up invincibility_potion
        assertEquals(countEntityOfType(res, "invincibility_potion"), 0);

        assertEquals(countEntityOfType(res, "wood"), 3);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 6));
        // Picks up wood
        assertEquals(countEntityOfType(res, "wood"), 2);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 7));
        // Picks up wood
        assertEquals(countEntityOfType(res, "wood"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 8));
        // Picks up wood
        assertEquals(countEntityOfType(res, "wood"), 0);

        assertEquals(countEntityOfType(res, "arrow"), 2);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 9));
        // Picks up arrow
        assertEquals(countEntityOfType(res, "arrow"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 10));
        // Picks up arrow
        assertEquals(countEntityOfType(res, "arrow"), 0);

        assertEquals(countEntityOfType(res, "bomb"), 2);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 11));
        // Picks up bomb
        assertEquals(countEntityOfType(res, "bomb"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 12));
        // Picks up bomb
        assertEquals(countEntityOfType(res, "bomb"), 0);

        assertEquals(countEntityOfType(res, "sword"), 1);
        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1, 13));
        // Picks up sword
        assertEquals(countEntityOfType(res, "sword"), 0);


        // Check player has items in inventory
        assertEquals(1, getInventory(res, "treasure").size());
        assertEquals(1, getInventory(res, "key").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());
        assertEquals(3, getInventory(res, "wood").size());
        assertEquals(2, getInventory(res, "arrow").size());
        assertEquals(2, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "sword").size());
    }


    @Test
    @DisplayName("Test only one key can be in player's inventory at a time")
    public void testOnlySingleKeyInInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_2keys", "c_movementTest_testMovementDown");


        assertEquals(countEntityOfType(res, "key"), 2);
        // Picks up key
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "key"), 1);
        assertEquals(1, getInventory(res, "key").size());

        // Tries to pick up other key but does not enter inventory
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "key"), 1);
        assertEquals(1, getInventory(res, "key").size());
    }

    @Test
    @DisplayName("Test sun stone is initialised and can be collected")
    public void testSunStoneAddedToInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectableSunStone", "c_movementTest_testMovementDown");


        assertEquals(countEntityOfType(res, "sun_stone"), 1);
        // Picks up key
        res = dmc.tick(Direction.DOWN);
        assertEquals(countEntityOfType(res, "sun_stone"), 0);
        assertEquals(1, getInventory(res, "sun_stone").size());
    }

}
