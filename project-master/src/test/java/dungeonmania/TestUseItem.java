package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.countEntityOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;


public class TestUseItem {
    @Test
    @DisplayName("Test IllegalArgumentException is thrown when id of input item is not of valid usable item")
    public void testIllegalArgumentException() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectableEntities", "c_movementTest_testMovementDown");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up items
        }

        assertEquals(3, getInventory(res, "wood").size());

        ItemResponse wood = getInventory(res, "wood").get(0);
        String woodId = wood.getId();

        // Player has wood in inventory but it's id is invalid for
        // using items
        assertThrows(IllegalArgumentException.class, () -> dmc.tick(woodId));

    }


    @Test
    @DisplayName("Test InvalidActionException is thrown when player tries to use item they do not have")
    public void testPlayerDoesNotHaveItem() {
  
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_usableItems", "c_movementTest_testMovementDown");


        // Player should have no items since they did not pick them up
        assertEquals(0, getInventory(res, "bomb").size());
        assertEquals(0, getInventory(res, "invisibility_potion").size());
        assertEquals(0, getInventory(res, "invincibility_potion").size());

        EntityResponse bomb = getEntities(res, "bomb").get(0);
        String bombId = bomb.getId();

        EntityResponse InvisiPot = getEntities(res, "invisibility_potion").get(0);
        String InvisId = InvisiPot.getId();

        EntityResponse InvinciPot = getEntities(res, "invincibility_potion").get(0);
        String InvincId = InvinciPot.getId();


        assertThrows(InvalidActionException.class, () -> dmc.tick(bombId));
        assertThrows(InvalidActionException.class, () -> dmc.tick(InvisId));
        assertThrows(InvalidActionException.class, () -> dmc.tick(InvincId));

    }


    @Test
    @DisplayName("Test Items can be used")
    public void testItemsCanBeUsed() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_usableItems", "c_movementTest_testMovementDown");

        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all usable items
        }

        // Player should now have available items to use
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());


        ItemResponse bomb = getInventory(res, "bomb").get(0);
        String bombId = bomb.getId();

        ItemResponse InvisiPot = getInventory(res, "invisibility_potion").get(0);
        String InvisId = InvisiPot.getId();

        ItemResponse InvinciPot = getInventory(res, "invincibility_potion").get(0);
        String InvincId = InvinciPot.getId();



        // There should be no bombs on the map as they have been picked up by the player
        assertEquals(countEntityOfType(res, "bomb"), 0);


        assertDoesNotThrow(() -> dmc.tick(bombId));
        // Since bomb is "used" it is placed back on the floor until
        // it detonates
        res = dmc.getDungeonResponseModel();
        assertEquals(countEntityOfType(res, "bomb"), 1);

        assertDoesNotThrow(() -> dmc.tick(InvisId));
        assertDoesNotThrow(() -> dmc.tick(InvincId));

        res = dmc.getDungeonResponseModel();
        assertEquals(0, getInventory(res, "invisibility_potion").size());
        assertEquals(0, getInventory(res, "invincibility_potion").size());



        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);

        // Player should not pick up bomb after they have placed it
        // and should still be on ground
        assertEquals(countEntityOfType(res, "bomb"), 1);
        assertEquals(0, getInventory(res, "bomb").size());
    }
}
