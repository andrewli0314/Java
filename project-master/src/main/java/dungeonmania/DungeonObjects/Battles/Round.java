package dungeonmania.DungeonObjects.Battles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import dungeonmania.DungeonObjects.Buff;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.BattleEntity;
import dungeonmania.Interfaces.BattleInterfaces.LinearStatIncrease;
import dungeonmania.Interfaces.BattleInterfaces.MultiplyStatIncrease;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;

public class Round implements Serializable{
    
    double deltaPlayerHealth;
    double deltaEnemyHealth;

    double playerDamage;
    double playerDefence;
    double enemyDamage;
    double enemyDefence;

    double initialPlayerHealth;
    double initialEnemyHealth;

    List<Entity> weaponsUsed = new ArrayList<>();

    public Round(Player player, BattleEntity enemy) {
        initialPlayerHealth = player.getHealth();
        initialEnemyHealth = enemy.getHealth();

        // Attack of player and enenmy are set as default 
        // from config. Weapon bonuses are calculated afterwards.
        playerDamage = player.getAttack();
        enemyDamage = enemy.getAttack();

        // Have player equip weapons and get bonus stats.
        equipWeapons(player);
        
        // Allies increase stats of player 
        alliesHelpPlayer(player);

        // Buff player if they have any buffs relevant to battle
        buffPlayer(player, enemy);

        // Increase enemy's stats if they have any to increase
        enenmyIncreasesStats(enemy);

        deltaPlayerHealth = -(calculateEnemyDamage(enemy));
        deltaEnemyHealth = -(calculatePlayerDamage(player));

        player.setHealth(initialPlayerHealth + deltaPlayerHealth);
        enemy.setHealth(initialEnemyHealth + deltaEnemyHealth);
    }

    private void equipWeapons(Player player) {
        List<Entity> inventory = player.getInventoryEntities();
        // If item is a weapon and the type of weapon not been used before
        // Increase stats linearly first
        for (Entity item : inventory) {
            if (!hasUsedWeapon(item)) {
                if (item instanceof LinearStatIncrease) {
                    weaponsUsed.add(item);

                    LinearStatIncrease weapon = (LinearStatIncrease) item;
                    playerDamage = weapon.increaseAttackLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
                    playerDefence = weapon.increaseDefenceLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
                }
            }
        }
        // Increase stats multiplicatively after
        for (Entity item : inventory) {
            if (!hasUsedWeapon(item)) {
                if (item instanceof MultiplyStatIncrease) {
                    weaponsUsed.add(item);
                    MultiplyStatIncrease weapon = (MultiplyStatIncrease) item;
                    playerDamage = weapon.increaseAttackMulitply(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
                }
            }
        }
    }

    private void alliesHelpPlayer(Player player) {
        for (Entity ally : player.getAllies()) {
            if (ally instanceof LinearStatIncrease) {
                LinearStatIncrease linearIncreaseAlly = (LinearStatIncrease) ally;
                playerDamage = linearIncreaseAlly.increaseAttackLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
                playerDefence = linearIncreaseAlly.increaseDefenceLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
            }
        }
    }

    private void buffPlayer(Player player, BattleEntity enemy) {
        Entity currBuff = player.getCurrentBuffEntity();
        if (currBuff != null) {
            if (currBuff instanceof LinearStatIncrease) {
                LinearStatIncrease battleBuff = (LinearStatIncrease) currBuff;
                playerDamage = battleBuff.increaseAttackLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
                playerDefence = battleBuff.increaseDefenceLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
            }
        }
    }

    private void enenmyIncreasesStats(BattleEntity enemy) {
        // Increase enemy attack and defence if they have stats to increase
        if (enemy instanceof LinearStatIncrease) {
            LinearStatIncrease defendingEnemy = (LinearStatIncrease) enemy;
            enemyDamage = defendingEnemy.increaseAttackLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
            enemyDefence = defendingEnemy.increaseDefenceLinear(playerDamage, playerDefence, initialPlayerHealth, enemyDamage, enemyDefence, initialEnemyHealth);
        }
    }

    private double calculatePlayerDamage(Player player) {
        return (playerDamage - enemyDefence) / 5;
    }

    private double calculateEnemyDamage(BattleEntity enemy) {
        return (enemyDamage - playerDefence) / 10;
    }

    public RoundResponse getRoundResponse() {
        return new RoundResponse(deltaPlayerHealth, deltaEnemyHealth, weaponsUsedItemResponseList());
    }

    public List<ItemResponse> weaponsUsedItemResponseList() {
        List<ItemResponse> weaponsUsedList = new ArrayList<>();

        for (Entity weapon : weaponsUsed) {
            weaponsUsedList.add(weapon.itemResponse());
        }
        return weaponsUsedList;
    }

    private boolean hasUsedWeapon(Entity item) {
        // If item type has been sued before return true
        // else return false
        for (Entity weapon : weaponsUsed) {
            if (weapon.getType().equals(item.getType())) {
                return true;
            }
        }
        return false;
    }

    public List<Entity> getWeaponEntities() {
        return this.weaponsUsed;
    }

}
