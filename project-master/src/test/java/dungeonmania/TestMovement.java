package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static dungeonmania.TestUtils.getPlayer;
import static dungeonmania.TestUtils.getEntities;
import static dungeonmania.TestUtils.getEntitiesOfType;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonObjects.BattleEntities.Mercenary;
import dungeonmania.DungeonObjects.BattleEntities.Spider;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import static dungeonmania.TestUtils.getInventory;

public class TestMovement {
    @Test
    @DisplayName("Test Player can move in each direction")
    public void TestSimpleMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_OnlyPlayer", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();
        // Player starts at position 1,1 upon initialisation
        assertEquals(player.getPosition(), new Position(1,1));

        res = dmc.tick(Direction.UP);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,0));

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,1));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(2,1));

        res = dmc.tick(Direction.LEFT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,1));
    }


    @Test
    @DisplayName("Test spider moves in circle direction")
    public void TestSpiderMovement() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_spiderTest_basicMovement", "c_spiderTest_basicMovement");
        Position pos = getEntities(res, "spider").get(0).getPosition();

        List<Position> movementTrajectory = new ArrayList<Position>();
        int x = pos.getX();
        int y = pos.getY();
        int nextPositionElement = 0;
        movementTrajectory.add(new Position(x  , y-1));
        movementTrajectory.add(new Position(x+1, y-1));
        movementTrajectory.add(new Position(x+1, y));
        movementTrajectory.add(new Position(x+1, y+1));
        movementTrajectory.add(new Position(x  , y+1));
        movementTrajectory.add(new Position(x-1, y+1));
        movementTrajectory.add(new Position(x-1, y));
        movementTrajectory.add(new Position(x-1, y-1));

        // Assert Circular Movement of Spider
        for (int i = 0; i <= 100; ++i) {
            res = dmc.tick(Direction.UP);
            assertEquals(movementTrajectory.get(nextPositionElement), getEntities(res, "spider").get(0).getPosition());
            
            nextPositionElement++;
            if (nextPositionElement == 8){
                nextPositionElement = 0;
            }
        }
    }

    @Test
    @DisplayName("Test zombie moves and onlymoves in adjacent tiles")
    public void testZombieCanMove() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_onlyZombie", "c_movementTest_testMovementDown");
        Position currPos;
        List<Position> adjacentPositions;
        Position nextPos;


        for (int i = 0; i < 100; i++) {
            currPos = getEntities(res, "zombie_toast").get(0).getPosition();
            adjacentPositions = currPos.getAdjacentPositions();

            res = dmc.tick(Direction.UP);
            nextPos = getEntities(res, "zombie_toast").get(0).getPosition();
            assertNotEquals(currPos, nextPos);
            assertTrue(adjacentPositions.contains(nextPos));
        }

    }

    @Test
    @DisplayName("Test Player can move through doors with a matching key")
    public void TestMovementThroughDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");
        EntityResponse player = getPlayer(res).get();
        // Player starts at position 1,1 upon initialisation
        assertEquals(player.getPosition(), new Position(1,1));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(2,1));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(3,1));
    }

    @Test
    @DisplayName("Test Player cannot move through doors without a key")
    public void TestInvalidMovementThroughDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_DoorsKeysTest_useKeyWalkThroughOpenDoor", "c_DoorsKeysTest_useKeyWalkThroughOpenDoor");
        EntityResponse player = getPlayer(res).get();
        // Player starts at position 1,1 upon initialisation
        assertEquals(player.getPosition(), new Position(1,1));

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,2));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(2,2));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(3,2));

        res = dmc.tick(Direction.UP);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(3,2));
    }

    @Test
    @DisplayName("Test Player cannot move over walls")
    public void TestInvalidMovementOverWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();
        // Player starts at position 1,1 upon initialisation
        assertEquals(player.getPosition(), new Position(1,1));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(2,1));

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(2,1));
    }

    @Test
    @DisplayName("Test Boulder-switch interaction")
    public void TestBoulderSwitchInteraction() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");
        EntityResponse player = getPlayer(res).get();

        assertEquals(player.getPosition(), new Position(2,2));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(3,2));

        List<EntityResponse> boulders = getEntitiesOfType(res, "boulder");
        EntityResponse boulder = boulders.get(0);
        assertEquals(boulder.getPosition(), new Position(4,2));
    }

    @Test
    @DisplayName("Test Invalid boulder movement")
    public void TestInvalidBoulderMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_doubleBoulder", "c_bombTest_placeBombRadius2");
        EntityResponse player = getPlayer(res).get();

        assertEquals(player.getPosition(), new Position(2,2));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(2,2));

        List<EntityResponse> boulders = getEntitiesOfType(res, "boulder");
        EntityResponse boulder1 = null;
        EntityResponse boulder2 = null;
        for (EntityResponse b : boulders){
            if (b.getPosition().equals(new Position(3,2))){
                boulder1 = b;
            }
            if (b.getPosition().equals(new Position(4,2))){
                boulder2 = b;
            }
        }
        assertNotNull(boulder1);
        assertNotNull(boulder2);
    }

    @Test
    @DisplayName("Test Switch-bomb interaction")
    public void TestSwitchBombInteraction() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bombTest_placeBombRadius2", "c_bombTest_placeBombRadius2");
        EntityResponse player = getPlayer(res).get();

        assertEquals(player.getPosition(), new Position(2,2));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(3,2));

        List<EntityResponse> boulders = getEntitiesOfType(res, "boulder");
        EntityResponse boulder = boulders.get(0);
        assertEquals(boulder.getPosition(), new Position(4,2));

        List<EntityResponse> bombs = getEntitiesOfType(res, "bomb");
        EntityResponse bomb = bombs.get(0);
        String bombID = bomb.getId();

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(3,3));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(4,3));

        try {
            res = dmc.tick(bombID);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidActionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        boulders = getEntitiesOfType(res, "boulder");
        assertTrue(boulders.isEmpty());
    }

    @Test
    @DisplayName("Test Spider movement when obstructed")
    public void TestSpiderMovemenWhenObstructed() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_testSpiderMovement2", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();

        assertEquals(player.getPosition(), new Position(6,1));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(7,1));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(8,1));

        List<EntityResponse> spiders = getEntitiesOfType(res, "spider");
        assertTrue(!spiders.isEmpty());
        Spider spider = (Spider) dmc.getGame().getEnvironment().getEntity(spiders.get(0).getId());
        assertEquals(spider.getPosition(), new Position(2, 0));

        res = dmc.tick(Direction.RIGHT);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(9,1));

        assertEquals(spider.getPosition(), new Position(1, 0));


    }

    @Test
    @DisplayName("Test Valid portal interaction")
    public void TestValidPortalInteraction() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities2", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();

        assertEquals(player.getPosition(), new Position(1,4));

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(6,6));


    }

    @Test
    @DisplayName("Test invalid portal interaction")
    public void TestInvalidPortalInteraction() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_staticEntities3", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();

        assertEquals(player.getPosition(), new Position(1,4));

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,4));
        
    }

    @Test
    @DisplayName("Test Fleeing strategy")
    public void TestFleeingStrategy() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_testInvincibilityPotion", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();
        for (int i = 0; i < 3; i++) {
            res = dmc.tick(Direction.DOWN);
            // Have player pick up all usable items
        }

        // Player should now have available items to use
        player = getPlayer(res).get();
        assertEquals(player.getPosition(), new Position(1,4));
        assertEquals(1, getInventory(res, "bomb").size());
        assertEquals(1, getInventory(res, "invisibility_potion").size());
        assertEquals(1, getInventory(res, "invincibility_potion").size());

        List<EntityResponse> mercenaries = getEntitiesOfType(res, "mercenary");
        assertTrue(!mercenaries.isEmpty());
        Mercenary mercenary = (Mercenary) dmc.getGame().getEnvironment().getEntity(mercenaries.get(0).getId());
        int oldDist = Math.abs(player.getPosition().getX() - mercenary.getPosition().getX()) + Math.abs(player.getPosition().getY() - mercenary.getPosition().getY());

        ItemResponse InvinciPot = getInventory(res, "invincibility_potion").get(0);
        String InvincId = InvinciPot.getId();
        assertDoesNotThrow(() -> dmc.tick(InvincId));
        int currDist = Math.abs(player.getPosition().getX() - mercenary.getPosition().getX()) + Math.abs(player.getPosition().getY() - mercenary.getPosition().getY());
        assertTrue(currDist > oldDist);
    }

    @Test
    @DisplayName("Test Stalking strategy")
    public void TestStalkingStrategySimple() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_testStalkingStrategySimple", "c_movementTest_testMovementDown");
        EntityResponse player = getPlayer(res).get();
        
        List<EntityResponse> mercenaries = getEntitiesOfType(res, "mercenary");
        assertTrue(!mercenaries.isEmpty());
        Mercenary mercenary = (Mercenary) dmc.getGame().getEnvironment().getEntity(mercenaries.get(0).getId());
        int oldDist = Math.abs(player.getPosition().getX() - mercenary.getPosition().getX()) + Math.abs(player.getPosition().getY() - mercenary.getPosition().getY());

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        int currDist = Math.abs(player.getPosition().getX() - mercenary.getPosition().getX()) + Math.abs(player.getPosition().getY() - mercenary.getPosition().getY());
        assertTrue(currDist < oldDist);
        oldDist = currDist;

        res = dmc.tick(Direction.DOWN);
        player = getPlayer(res).get();
        currDist = Math.abs(player.getPosition().getX() - mercenary.getPosition().getX()) + Math.abs(player.getPosition().getY() - mercenary.getPosition().getY());
        assertTrue(currDist < oldDist);
        oldDist = currDist;

    }
}
