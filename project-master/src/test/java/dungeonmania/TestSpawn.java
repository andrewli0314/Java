package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static dungeonmania.TestUtils.countEntityOfType;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class TestSpawn {
    
    @Test
    @DisplayName("Test Zombies Spawn")
    public void testZombiesSpawn() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_spawnZombies", "c_testSpawn");

        // Initially there is only only zombie toast spawner
        // and no zombies
        assertEquals(countEntityOfType(res, "zombie_toast_spawner"), 1);

        int numZombies = 0;
        for (int i = 1; i <= 15; i++) {
            res = dmc.tick(Direction.DOWN);

            if (i % 5 == 0) {
                // Every 5 ticks a new zombie should spawn
                numZombies += 1;
            }
            assertEquals(countEntityOfType(res, "zombie_toast"), numZombies);
        }

    }

    @Test
    @DisplayName("Test Spiders Spawn")
    public void testSpidersSpawn() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_OnlyPlayer", "c_testSpawn");

        // Initially there are no spiders
        assertEquals(countEntityOfType(res, "spider"), 0);

        int numSpiders = 0;
        for (int i = 1; i <= 15; i++) {
            res = dmc.tick(Direction.DOWN);

            if (i % 5 == 0) {
                // Every 5 ticks a new spider
                numSpiders += 1;
            }
            assertEquals(countEntityOfType(res, "spider"), numSpiders);
        }

    }
}
