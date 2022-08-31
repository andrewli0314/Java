package dungeonmania.DungeonObjects.BattleEntities;

import org.json.JSONObject;

import dungeonmania.DungeonManiaController;
import dungeonmania.EntitiesManager;
import dungeonmania.Game;
import dungeonmania.DungeonObjects.Entity;
import dungeonmania.DungeonObjects.Player;
import dungeonmania.DungeonObjects.BuildableEntities.Sceptre;
import dungeonmania.DungeonObjects.CollectableEntities.Treasure;
import dungeonmania.Interfaces.InteractableInterface;
import dungeonmania.Interfaces.BattleInterfaces.LinearStatIncrease;
import dungeonmania.Patterns.strategy.FollowingStrategy;
import dungeonmania.Patterns.strategy.StalkingStrategy;
import dungeonmania.util.Position;

import static dungeonmania.util.Position.isWithinRadius;

import java.util.ArrayList;
import java.util.List;

public class Mercenary extends BattleEntity implements InteractableInterface, LinearStatIncrease {

    private int bribeAmount;
    private int bribeRadius;
    private int allyAttack;
    private int allyDefence;

    private int bribeDuration;

    public Mercenary(Position position, String type, JSONObject config) {
        super(position, type);
        this.bribeAmount = config.getInt("bribe_amount");
        this.bribeRadius = config.getInt("bribe_radius");
        this.allyAttack = config.getInt("ally_attack");
        this.allyDefence = config.getInt("ally_defence");

        super.setAttack(config.getDouble("mercenary_attack"));
        super.setHealth(config.getDouble("mercenary_health"));

        super.setIsInteractable(true);
    }

    @Override
    public boolean canInteract(Player player) {
        if (player.hasSceptre()) {
            // If player has sceptre they can bribe merc
            // regardless of distance
            return true;
        }
        if (player.getNumTreasure() < bribeAmount) {
            // If player does not have enough money, they cannot 
            // interact with mercenary.
            return false;
        }
        if (!isWithinRadius(player.getPosition(), super.getPosition(), bribeRadius)) {
            // If player is not within radius of mercenary
            // they cannot interact
            return false;
        }

        return true;
    }


    @Override
    public void move(EntitiesManager environment) {
        // If merc is an ally, they move to player's previous position
        if (super.getIsAlly()) {
            super.setPosition(environment.getPlayer().getPrevPosition());
        } else {
            super.move(environment);
        }

    }

    @Override 
    public void update(EntitiesManager entitiesManager, Player player) {
        this.move(entitiesManager);
        if (bribeDuration > 0) {
            bribeDuration -= 1;
            if (bribeDuration == 0) {
                // If duration of bribe is over then merc is no 
                // longer an ally and can be interacted with again
                player.removeAlly(this);
                super.setIsAlly(false);
                super.setIsInteractable(true);
            }
        }
    }

    @Override
    public Position getNextPosition() {
        // STUB
        if(this.getMovementStrategy() == null){
            Position currPos = super.getPosition();
            return new Position(currPos.getX(), currPos.getY());
        } else {
            return this.getMovementStrategy().getNextPosition();
        }
    }

    @Override
    public void interact(Player player, EntitiesManager environment)  {
        if (!player.hasSceptre()) {
            // If player does not have sceptre 
            // Merc takes treasure from player
            takeTreasure(player);
        } else {
            // Player has sceptre and is bribed based on bribe duration
            bribeDuration = player.getMindControlDuration();
        }

        // Merc becomes ally
        player.addAlly(this);
        this.setIsAlly(true);

        // Merc is no longer interactable as they have been bribed
        super.setIsInteractable(false);
    }

    @Override
    public void setIsAlly(boolean isAlly) {
        super.setIsAlly(isAlly);
        if (isAlly){
            this.setMovementStrategy(new FollowingStrategy<Mercenary>(DungeonManiaController.getGame()));
            this.getMovementStrategy().setEntity(this);
        } else {
            this.setMovementStrategy(new StalkingStrategy<Mercenary>(DungeonManiaController.getGame()));
            this.getMovementStrategy().setEntity(this);
        }
    }

    protected void takeTreasure(Player player) {
        // Interaction causes entity to now become an ally to player
        // player must also lose the required amount of terausre.
        List<Entity> treasures = new ArrayList<>();
        int treasureAcquired = 0;
        for (Entity item : player.getInventoryEntities()) {
            if (item instanceof Treasure && treasureAcquired < bribeAmount) {
                treasures.add(item);
                treasureAcquired += 1;
            }
        }
        // Player needs to now remove all treasure from inventory 
        // equal to bribe amount
        for (Entity treasure : treasures) {
            player.getInventory().removeEntityFromManager(treasure);
        }
    }

    @Override
    public double increaseAttackLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        if (super.getIsAlly()) {
            // If merc is bribed and an ally, increase the attack stats of player
            return playerAttack + this.allyAttack;
        }
        // Otherwise merc does not have any attack bonuses
        return enemyAttack;
    }

    @Override
    public double increaseDefenceLinear(double playerAttack, double playerDefence, double playerHealth, double enemyAttack, double enemyDefence, double enemyHealth) {
        if (super.getIsAlly()) {
            // If merc is bribed and an ally, increase the defence stats of player
            return playerDefence + this.allyDefence;
        }
        // Otherwise merc does not have any attack bonuses
        return enemyDefence;
    }



    public int getBribeAmount() {
        return this.bribeAmount;
    }

    public void setBribeAmount(int bribeAmount) {
        this.bribeAmount = bribeAmount;
    }

    public int getBribeRadius() {
        return this.bribeRadius;
    }

    public void setBribeRadius(int bribeRadius) {
        this.bribeRadius = bribeRadius;
    }

}
