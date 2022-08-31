package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.countEntityOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;


public class TestNewGame {
    @Test
    @DisplayName("Test IllegalArgumentException on illegal Dungeon Name")
    public void testIllegalDungeonName() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.newGame("d_movementTest_testMovementDown", "test"));
    }

    @Test
    @DisplayName("Test IllegalArgumentException on illegal Config Name")
    public void testIllegalConfigName() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertThrows(IllegalArgumentException.class, () -> dmc.newGame("test", "c_movementTest_testMovementDown"));
    }

    @Test
    @DisplayName("Test No exception thrown with valid dungeon name and config name")
    public void testLegalDungeonNameAndConfigName() {
        DungeonManiaController dmc = new DungeonManiaController();
        assertDoesNotThrow(() -> dmc.newGame("d_movementTest_testMovementDown", "c_movementTest_testMovementDown"));
    }

    @Test
    @DisplayName("Test JSON file with only single player in it and gives correct output on EntiityInfoResponse")
    public void testSinglePlayer() {
        DungeonManiaController dmc = new DungeonManiaController();

        DungeonResponse initDungonRes = dmc.newGame("d_OnlyPlayer", "c_movementTest_testMovementDown");
        EntityResponse initPlayer = getPlayer(initDungonRes).get();
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(1, 1), false);
        assertEquals(initPlayer, expectedPlayer);
    }
    @Test
    @DisplayName("Tests that static entities are initialised")
    public void testStaticEntityInitalised() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities", "c_movementTest_testMovementDown");
        assertEquals(res.getDungeonName(), "d_staticEntities");
        assertEquals(countEntityOfType(res, "wall"), 2);
        assertEquals(countEntityOfType(res, "exit"), 1);
        assertEquals(countEntityOfType(res, "boulder"), 1);
        assertEquals(countEntityOfType(res, "switch"), 1);
        assertEquals(countEntityOfType(res, "portal"), 2);
        assertEquals(countEntityOfType(res, "door"), 1);
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 1);
    }

    @Test
    @DisplayName("Tests that moving entities are initialised")
    public void testMovingEntityInitalised() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_movingEntities", "c_movementTest_testMovementDown");
        assertEquals(res.getDungeonName(), "d_movingEntities");
        assertEquals(countEntityOfType(res, "spider"), 1);
        assertEquals(countEntityOfType(res, "zombie_toast"), 1);
        assertEquals(countEntityOfType(res, "mercenary"), 2);
    }

    @Test
    @DisplayName("Tests that collectable entities are initialised")
    public void testCollectableEntityInitalised() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_collectableEntities", "c_movementTest_testMovementDown");
        assertEquals(res.getDungeonName(), "d_collectableEntities");
        assertEquals(countEntityOfType(res, "treasure"), 1);
        assertEquals(countEntityOfType(res, "key"), 1);
        assertEquals(countEntityOfType(res, "invisibility_potion"), 1);
        assertEquals(countEntityOfType(res, "invincibility_potion"), 1);
        assertEquals(countEntityOfType(res, "wood"), 3);
        assertEquals(countEntityOfType(res, "arrow"), 2);
        assertEquals(countEntityOfType(res, "bomb"), 2);
        assertEquals(countEntityOfType(res, "sword"), 1);
    }

}

