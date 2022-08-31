package dungeonmania.Interfaces;

import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;

public interface InteractableInterface {
    public boolean canInteract(Player player);
    public void interact(Player player, EntitiesManager environment);
}
