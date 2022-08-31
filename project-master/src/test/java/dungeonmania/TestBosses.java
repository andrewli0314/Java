package dungeonmania;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;

import static dungeonmania.TestUtils.countEntityOfType;
import static dungeonmania.TestBattle.assertBattleCalculations;
import static dungeonmania.TestUtils.getEntitiesOfType;
import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.getValueFromConfigFile;


public class TestBosses {

    //////////////
    // Hydra Tests
    //////////////

    @Test
    @DisplayName("BOSS TEST 1 - Test Hydra is initialised in the game")
    public void testHydraInitialised() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest1", "c_bossTest1");
        assertEquals(countEntityOfType(res, "hydra"), 1);
    }

    @Test
    @DisplayName("BOSS TEST 2 - Test Hydra does not gain health if increase rate is set to 0")
    public void testHydraCannotRegainHealthRate0() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest1", "c_bossTest1");
        assertEquals(countEntityOfType(res, "hydra"), 1);

        // Player moves to fight hydra, both have 100 health and
        // 2 and 1 attack respectively, hydra should not regain health
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        assertBattleCalculations("hydra", battle, true, "c_bossTest1");

        assertEquals(countEntityOfType(res, "hydra"), 0);
    }

    @Test
    @DisplayName("BOSS TEST 3 - Test Hydra does not gain health if increase rate is set to 1 but increase amount is 0")
    public void testHydraCannotRegainHealthAmount0() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest1", "c_bossTest2");
        assertEquals(countEntityOfType(res, "hydra"), 1);

        String configFilePath = "c_bossTest2";
        String enemyType = "hydra";
        

        // Player moves to fight hydra, both have 100 health and
        // 2 and 1 attack respectively, hydra should not regain health
        res = dmc.tick(Direction.DOWN);

        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));


        // Since the hydra wil lalways gain health instead of taking damage
        // the player will evnetually die
        for (RoundResponse round : rounds) {

            double deltaHydraHealth;
            double increaseRate = Double.parseDouble(TestUtils. getValueFromConfigFile("hydra_health_increase_rate", configFilePath));
            double increaseAmount = Double.parseDouble(TestUtils. getValueFromConfigFile("hydra_health_increase_rate", configFilePath));
            // There is a 50% chance that hydra will gain or lose health

            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -0.0);
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

        }

        assertEquals(countEntityOfType(res, "hydra"), 1);
        assertEquals(countEntityOfType(res, "player"), 0);
    }

    @Test
    @DisplayName("BOSS TEST 4 - Test Hydra regains health when fighting")
    public void testHydraRegainsHealth() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest1", "c_bossTest3");
        assertEquals(countEntityOfType(res, "hydra"), 1);

        String configFilePath = "c_bossTest3";
        String enemyType = "hydra";
        
        Random rand = new Random(1);
        // Hydra has been initialised with a random seed of 1,
        // which can be confirmed with another random with seed 1.
        // By comparing if expected random numbers generated, 
        // confirming whether a hydra gains health can be checked.
        res = dmc.tick(Direction.DOWN);

        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        
        // Rate of health increase has been set to 0.5
        for (RoundResponse round : rounds) {
            double randomNum = rand.nextDouble();

            double deltaHydraHealth;
            double increaseRate = Double.parseDouble(TestUtils. getValueFromConfigFile("hydra_health_increase_rate", configFilePath));
            double increaseAmount = Double.parseDouble(TestUtils. getValueFromConfigFile("hydra_health_increase_amount", configFilePath));
            // There is a 50% chance that hydra will gain or lose health
            if (randomNum <= increaseRate) {
                deltaHydraHealth = increaseAmount;
            } else {
                deltaHydraHealth = -playerAttack / 5;
            }

            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), deltaHydraHealth);
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

        }

        assertEquals(countEntityOfType(res, "hydra"), 0);
    }


    ////////////////
    // Assasin Tests
    ////////////////
    @Test
    @DisplayName("BOSS TEST 5 - Test assassin is initialised in the game")
    public void testAssassinInitialised() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest1", "c_bossTest1");
        assertEquals(countEntityOfType(res, "assassin"), 1);
    }


    @Test
    @DisplayName("BOSS TEST 6 - Test assassin assassin can never be bribed if rate is set to 1")
    public void testAssassinCannotBeBribedRateOne() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest1", "c_bossTest1");
        assertEquals(countEntityOfType(res, "assassin"), 1);

        // Since bribe fail rate is set to 1, player should always
        // fail the bribe
        EntityResponse assassin = getEntitiesOfType(res, "assassin").get(0);
        for (int i = 0; i < 1000; i++) {
            assertDoesNotThrow(() -> dmc.interact(assassin.getId()));

            boolean isAssassinInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
            // Assassin should always interactable as they have never been
            // bribed
            assertEquals(isAssassinInteractable, true);
        }
    }

    @Test
    @DisplayName("BOSS TEST 7 - Test player still loses treasure if assassin is not bribed")
    public void testPlayLosesTreasureOnFailedBribe() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest7", "c_bossTest2");

        res = dmc.tick(Direction.DOWN);
        // Player walks down and picks up 5 treausre
        assertEquals(5, getInventory(res, "treasure").size());
        int numTreasure = 5;

        // Since bribe fail rate is set to 1, player should always
        // fail the bribe
        EntityResponse assassin = getEntitiesOfType(res, "assassin").get(0);
        for (int i = 0; i < 5; i++) {
            assertDoesNotThrow(() -> dmc.interact(assassin.getId()));
            // Player should lose their treasure
            numTreasure -= 1;
            res = dmc.getDungeonResponseModel();
            assertEquals(numTreasure, getInventory(res, "treasure").size());

            boolean isAssassinInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
            // Assassin should always interactable as they have never been
            // bribed
            assertEquals(isAssassinInteractable, true);
        }
        // By the end assassin was never bribed
        boolean isAssassinInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
        assertEquals(isAssassinInteractable, true);
        res = dmc.tick(Direction.RIGHT);
        // Fight should occur with assassin

        BattleResponse battle = res.getBattles().get(0);
        assertBattleCalculations("assassin", battle, true, "c_bossTest2");
        assertEquals(countEntityOfType(res, "assassin"), 0);

    }


    @Test
    @DisplayName("BOSS TEST 8 - Player bribes assassin with 99% failure rate")
    public void testPlayerBribesAssassinSuccess() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest8", "c_bossTest3");

        EntityResponse assassin = getEntitiesOfType(res, "assassin").get(0);

        // Assassin has random seet of 1, so the next double
        // should be equal
        Random rand = new Random(1);
        double bribeFailRate = Double.parseDouble(getValueFromConfigFile("assassin_bribe_fail_rate", "c_bossTest3"));
        boolean bribed = false;
        // If random num is > bribe fail rate then bribe should be successful
        while (!bribed) {
            double randomNum = rand.nextDouble();
            assertDoesNotThrow(() -> dmc.interact(assassin.getId()));
            if (randomNum > bribeFailRate) {
                bribed = true;
            }
        }

        res = dmc.getDungeonResponseModel();
        // Player has now bribed assassin and should no longer be interactable
        boolean isAssassinInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
        assertEquals(isAssassinInteractable, false);

        res = dmc.tick(Direction.DOWN);
        // Player should move down and not fight allied assassin
        assertEquals(countEntityOfType(res, "player"), 1);
        assertEquals(countEntityOfType(res, "assassin"), 1);
    }


    @Test
    @DisplayName("BOSS TEST 9 - Player bribes assassin and now assassin helps in battle")
    public void testAssassinAllyInBattle() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_bossTest9", "c_bossTest9");

        // Assassin is bribed
        EntityResponse assassin = getEntitiesOfType(res, "assassin").get(0);
        assertDoesNotThrow(() -> dmc.interact(assassin.getId()));

        res = dmc.getDungeonResponseModel();
        // Player has now bribed assassin and should no longer be interactable
        boolean isAssassinInteractable = getEntitiesOfType(res, "assassin").get(0).isInteractable();
        assertEquals(isAssassinInteractable, false);

        assertEquals(countEntityOfType(res, "zombie_toast"), 1);
        res = dmc.tick(Direction.DOWN);
        // Player should move down and fight zombie
        String configFilePath = "c_bossTest9";
        String enemyType = "zombie_toast";
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("zombie_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile("zombie_attack", configFilePath));

        double allyAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("ally_attack", configFilePath));
        double allyDefence = Double.parseDouble(TestUtils. getValueFromConfigFile("ally_defence", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - allyDefence) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + allyAttack) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

        }
        assertEquals(countEntityOfType(res, "zombie_toast"), 0);
        assertEquals(countEntityOfType(res, "assassin"), 1);
        assertEquals(countEntityOfType(res, "player"), 1);
    }

}
