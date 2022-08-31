package dungeonmania;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;

import static dungeonmania.TestUtils.getInventory;
import static dungeonmania.TestUtils.countEntityOfType;
import static dungeonmania.TestUtils.getValueFromConfigFile;
import static dungeonmania.TestUtils.getEntitiesOfType;


public class TestBattle {

    public static void assertBattleCalculations(String enemyType, BattleResponse battle, boolean enemyDies, String configFilePath) {
        List<RoundResponse> rounds = battle.getRounds();
        double playerHealth = Double.parseDouble(getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(playerAttack / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }

        if (enemyDies) {
            assertTrue(enemyHealth <= 0);
        } else {
            assertTrue(playerHealth <= 0);
        }
    }

    @Test
    @DisplayName("Battle Test 1 - Test Battle simple battle without weapons")
    public void testBattleInitialised() {

        String configFilePath = "c_movementTest_testMovementDown";
        String enemyType = "zombie";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest1", configFilePath);
        
        // Player moves to Zombie
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);

        assertBattleCalculations(enemyType, battle, true, configFilePath);

        assertEquals(countEntityOfType(res, "zombie"), 0);
    }

    @Test
    @DisplayName("Battle Test 2 - Test Battle simple battle without weapons against spider")
    public void testBattleSpiderNoWeapons() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest2", configFilePath);
        
        // Player moves to spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);

        assertBattleCalculations(enemyType, battle, true, configFilePath);

        assertEquals(countEntityOfType(res, "spider"), 0);
    }

    @Test
    @DisplayName("Battle Test 3 - Test simple battle against mercenary no weapons")
    public void testBattleMercenaryNoWeapons() {

        String configFilePath = "c_testSpawn";
        String enemyType = "mercenary";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest3", configFilePath);
        
        // Player moves to mercenary
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        assertBattleCalculations(enemyType, battle, true, configFilePath);

        assertEquals(countEntityOfType(res, "mercenary"), 0);
    }


    @Test
    @DisplayName("Battle Test 4 - Test sword gives bonus against enemy")
    public void testBattleSpiderWithSword() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest4", configFilePath);
        
        // Player picks up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());

        // Player fights spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double swordBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("sword_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + swordBonus) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            assertEquals(round.getWeaponryUsed().get(0).getType(), "sword");
        }
        // Sword used for round
        assertEquals(rounds.get(0).getWeaponryUsed().get(0).getType(), "sword");
    }


    @Test
    @DisplayName("Battle Test 5 - Test bow gives double damage against emeny")
    public void testBattleSpiderWithBow() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest5", configFilePath);
        
        // Player materials for bow
        res = dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.build("bow"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "bow").size());

        // Player fights spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack * 2) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            assertEquals(round.getWeaponryUsed().get(0).getType(), "bow");
        }
    }


    @Test
    @DisplayName("Battle Test 6 - Test shield gives defence against attack")
    public void testBattleSpiderWithShield() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest6", configFilePath);
        
        // Player materials for shield
        res = dmc.tick(Direction.DOWN);
        assertDoesNotThrow(() -> dmc.build("shield"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "shield").size());

        // Player fights spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double shieldBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("shield_defence", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - shieldBonus) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(playerAttack / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            assertEquals(round.getWeaponryUsed().get(0).getType(), "shield");
        }
        

    }


    @Test
    @DisplayName("Battle Test 7 - Test bow bonus multiplies after sword bonus added")
    public void testBattleSpiderWithBowAndSword() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest7", configFilePath);
        
        // Player picks up sword and materials for shield
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());
        assertDoesNotThrow(() -> dmc.build("bow"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "bow").size());

        // Player fights spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double swordBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("sword_attack", configFilePath));

        List<String> weaponsUsed = new ArrayList<>();

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(2 * (playerAttack + swordBonus) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            for (ItemResponse weaponType : round.getWeaponryUsed()) {
                weaponsUsed.add(weaponType.getType());
            }
            assertTrue(weaponsUsed.contains("bow"));
            assertTrue(weaponsUsed.contains("sword"));
           
        }
    }

    @Test
    @DisplayName("Battle Test 8 - Test bonuses of sword, bow and shield together. Bow and Shield added to inventory before sword")
    public void testBattleSpiderWithSwordBowShield() {
        // Tests that damage multiplies even if bow is added
        // to inventory first before sword.

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest8", configFilePath);
        
        // Player picks materials for shield and bow but does
        // not have sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getInventory(res, "sword").size());
        assertDoesNotThrow(() -> dmc.build("bow"));
        assertDoesNotThrow(() -> dmc.build("shield"));

        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(1, getInventory(res, "shield").size());

        // Player picks up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());

        // Player fights spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double swordBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("sword_attack", configFilePath));
        double shieldBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("shield_defence", configFilePath));

        List<String> weaponsUsed = new ArrayList<>();

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - shieldBonus) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(2 * (playerAttack + swordBonus) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            for (ItemResponse weaponType : round.getWeaponryUsed()) {
                weaponsUsed.add(weaponType.getType());
            }
            assertTrue(weaponsUsed.contains("bow"));
            assertTrue(weaponsUsed.contains("sword"));
            assertTrue(weaponsUsed.contains("shield"));
        }
        assertEquals(1, getInventory(res, "shield").size());
        assertEquals(1, getInventory(res, "sword").size());
        assertEquals(1, getInventory(res, "bow").size());
    }


    @Test
    @DisplayName("Battle Test 9 - Test same weapons do not stack")
    public void testBattleMultipleSwordsDoNotStack() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest9", configFilePath);
        
        // Player picks up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(5, getInventory(res, "sword").size());

        // Player fights spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double swordBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("sword_attack", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + swordBonus) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            assertEquals(round.getWeaponryUsed().get(0).getType(), "sword");
        }
    }


    @Test
    @DisplayName("Battle Test 10 - Test weapon deteriorates if durability reach 0 after battle")
    public void testBattleCheckWeaponsDeteriorate() {
        // Tests that damage multiplies even if bow is added
        // to inventory first before sword.

        String configFilePath = "c_battleDurability1";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest8", configFilePath);
        
        // Player picks materials for shield and bow but does
        // not have sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(0, getInventory(res, "sword").size());
        assertDoesNotThrow(() -> dmc.build("bow"));
        assertDoesNotThrow(() -> dmc.build("shield"));

        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "bow").size());
        assertEquals(1, getInventory(res, "shield").size());

        // Player picks up sword
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sword").size());

        // Player fights spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double swordBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("sword_attack", configFilePath));
        double shieldBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("shield_defence", configFilePath));

        List<String> weaponsUsed = new ArrayList<>();

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - shieldBonus) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(2 * (playerAttack + swordBonus) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            for (ItemResponse weaponType : round.getWeaponryUsed()) {
                weaponsUsed.add(weaponType.getType());
            }
            assertTrue(weaponsUsed.contains("bow"));
            assertTrue(weaponsUsed.contains("sword"));
            assertTrue(weaponsUsed.contains("shield"));
        }

        assertEquals(0, getInventory(res, "shield").size());
        assertEquals(0, getInventory(res, "sword").size());
        assertEquals(0, getInventory(res, "bow").size());
    }



    @Test
    @DisplayName("Battle Test 11 - Test battle is won immediately with invicibility_potion")
    public void testBattleWithInvincibilitPotion() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest11", configFilePath);
        
        // Player picks up potion
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "invincibility_potion").size());

        // Player drinks potion
        String potId = getInventory(res, "invincibility_potion").get(0).getId();
        assertDoesNotThrow(()-> dmc.tick(potId));
        res = dmc.getDungeonResponseModel();

        // Spider moves to player and now initialises battle
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();
        // With the potion there should only be one round
        assertEquals(1, rounds.size());

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));


        RoundResponse round = rounds.get(0);
        // Since player is invincible, they should immediately lose
        // player sohuld also have lost no health
        assertEquals(round.getDeltaCharacterHealth(), -0.0);
        assertEquals(round.getDeltaEnemyHealth(), -enemyHealth);

        assertEquals(countEntityOfType(res, "spider"), 0);
    }



    @Test
    @DisplayName("Battle Test 12 - Test if player dies they are removed from the game")
    public void testPlayerDies() {

        String configFilePath = "c_strongEnemies";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest2", configFilePath);
        
        assertEquals(countEntityOfType(res, "player"), 1);

        // Player moves to spider
        res = dmc.tick(Direction.DOWN);
        
        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        // Spider is too strong for player
        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
            assertEquals(round.getDeltaEnemyHealth(), -(playerAttack / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();
        }
        assertEquals(countEntityOfType(res, "player"), 0);
    }

    
    @Test
    @DisplayName("Battle Test 13 - Test multiple battles can occur if player walks on tile with multiple enemies and that each battle deteriorates weapon")
    public void testBattleMultipleEnemies() {

        String configFilePath = "c_testSpawn";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest13", configFilePath);
        

        assertEquals(countEntityOfType(res, "spider"), 4);
        // Player picks up sword
        res = dmc.tick(Direction.DOWN);

        // Player fights multiple spiders at once
        res = dmc.tick(Direction.DOWN);

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double swordBonus = Double.parseDouble(TestUtils. getValueFromConfigFile("sword_attack", configFilePath));
        int numBattles = 0;

        for (BattleResponse battle : res.getBattles()) {

            if (numBattles == 2) {
                // After two battles the sword should be broken
                swordBonus = 0;
            }
            List<RoundResponse> rounds = battle.getRounds();

            double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
            double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
            double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

            for (RoundResponse round : rounds) {
                assertEquals(round.getDeltaCharacterHealth(), -(enemyAttack / 10));
                assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + swordBonus) / 5));
                enemyHealth += round.getDeltaEnemyHealth();
                playerHealth += round.getDeltaCharacterHealth();
            }

            numBattles += 1;
        }

        assertEquals(countEntityOfType(res, "spider"), 0);
        assertEquals(0, getInventory(res, "sword").size());
    }

    @Test
    @DisplayName("Battle Test 14 - Test bribed mercenary gives ally bonuses to player")
    public void testBattleWithAlly() {

        String configFilePath = "c_testBribeAmount0";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest14", configFilePath);
        
        // Player bribes mercenary and now mercenary becomes an ally
        // who gives attack and defence bonuses to the player
        String idMerc = getEntitiesOfType(res, "mercenary").get(0).getId();
        assertDoesNotThrow(() -> dmc.interact(idMerc));

        // Player moves down to fight spider, now with ally buff
        res = dmc.tick(Direction.DOWN);

        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double allyAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("ally_attack", configFilePath));
        double allyDefence = Double.parseDouble(TestUtils. getValueFromConfigFile("ally_defence", configFilePath));

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - allyDefence) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + allyAttack) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

        }
        assertEquals(countEntityOfType(res, "spider"), 0);
        assertEquals(countEntityOfType(res, "mercenary"), 1);
        assertEquals(countEntityOfType(res, "player"), 1);
    }



    @Test
    @DisplayName("Battle Test 15 - Test Midnight Armour gives bonus stats")
    public void testBattleWithMidnightArmour() {

        String configFilePath = "c_battleTest15";
        String enemyType = "spider";

        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_battleTest15", configFilePath);
        
        // Player moves down to pick up items for midnight amrour
        res = dmc.tick(Direction.DOWN);
        assertEquals(1, getInventory(res, "sun_stone").size());
        assertEquals(1, getInventory(res, "sword").size());

        // Player should be able to build midnight armour
        // as there are no zombies
        assertDoesNotThrow(() -> dmc.build("midnight_armour"));
        res = dmc.getDungeonResponseModel();
        assertEquals(1, getInventory(res, "midnight_armour").size());

        // Player moves down to fight spider
        res = dmc.tick(Direction.DOWN);

        BattleResponse battle = res.getBattles().get(0);
        List<RoundResponse> rounds = battle.getRounds();

        double playerHealth = Double.parseDouble(TestUtils.getValueFromConfigFile("player_health", configFilePath));
        double enemyHealth = Double.parseDouble(TestUtils.getValueFromConfigFile(enemyType + "_health", configFilePath));
        double playerAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", configFilePath));
        double enemyAttack = Double.parseDouble(TestUtils. getValueFromConfigFile(enemyType + "_attack", configFilePath));

        double armourAtt = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_attack", configFilePath));
        double armourDefence = Double.parseDouble(TestUtils. getValueFromConfigFile("midnight_armour_defence", configFilePath));

        List<String> weaponsUsed = new ArrayList<>();

        for (RoundResponse round : rounds) {
            assertEquals(round.getDeltaCharacterHealth(), -((enemyAttack - armourDefence) / 10));
            assertEquals(round.getDeltaEnemyHealth(), -((playerAttack + armourAtt) / 5));
            enemyHealth += round.getDeltaEnemyHealth();
            playerHealth += round.getDeltaCharacterHealth();

            for (ItemResponse weaponType : round.getWeaponryUsed()) {
                weaponsUsed.add(weaponType.getType());
            }
            assertTrue(weaponsUsed.contains("midnight_armour"));
        }
        assertEquals(countEntityOfType(res, "spider"), 0);
        assertEquals(countEntityOfType(res, "player"), 1);
    }
}
