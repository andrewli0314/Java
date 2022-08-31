package dungeonmania.DungeonObjects.Battles;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BattleEntities.BattleEntity;
import dungeonmania.Interfaces.BattleInterfaces.WeaponInterface;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.RoundResponse;

public class Battle implements Serializable{
    
    double initialPlayerHealth;
    double initialEnemyHealth;
    Player player; 
    BattleEntity enemy;
    List<Round> rounds = new ArrayList<>();
    List<Entity> killedEntities = new ArrayList<>();

    public Battle(Player player, BattleEntity enemy, JSONObject config) {
        
        initialPlayerHealth = player.getHealth();
        initialEnemyHealth = enemy.getHealth();

        this.player = player;
        this.enemy = enemy;

        while (player.getHealth() > 0 && enemy.getHealth() > 0) {
            rounds.add(new Round(player, enemy));
        }

        if (player.getHealth() <= 0) {
            killedEntities.add(player);
        } 
        if (enemy.getHealth() <= 0) {
            killedEntities.add(enemy);
        }

        updateWeaponDurability();
    }

    public BattleResponse getBattleResponse() {
        List<RoundResponse> roundsResponse = new ArrayList<>();

        for (Round round : rounds) {
            roundsResponse.add(round.getRoundResponse());
        }
        return new BattleResponse(enemy.getType(), roundsResponse, initialPlayerHealth, initialEnemyHealth);
    }

    public List<Entity> getKilledEntities() {
        return killedEntities;
    }

    public void updateWeaponDurability() {
        // Get all weapon entities used as entities
        List<Entity> itemsUsed = rounds.get(0).getWeaponEntities();

        for (Entity item : itemsUsed) {
            if (item instanceof WeaponInterface) {
                WeaponInterface weapon = (WeaponInterface) item;
                int newDurability = weapon.reduceDurability();
                // Update durability of weapon after battle
                // if it is now 0, then remove weapon from inventory

                if (newDurability == 0) {
                    player.getInventory().removeEntityFromManager(item);
                }
            }
        }
    }


}
