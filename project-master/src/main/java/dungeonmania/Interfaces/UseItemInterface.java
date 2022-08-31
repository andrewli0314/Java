package dungeonmania.Interfaces;



import dungeonmania.EntitiesManager;
import dungeonmania.DungeonObjects.Player;

public interface UseItemInterface {

    public void use(EntitiesManager environment, EntitiesManager inventory, Player player);

}
