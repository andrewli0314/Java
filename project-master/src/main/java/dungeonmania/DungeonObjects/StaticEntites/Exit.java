package dungeonmania.DungeonObjects.StaticEntites;

import dungeonmania.DungeonObjects.Player;
import dungeonmania.util.Position;

public class Exit extends StaticEntity {

    public Exit(Position position, String type) {
        super(position, type);
    }
    
    public boolean hasPlayerOnSelf(Player player) {
        // If player is on same position as self, they have 
        // reached the exit
        if (player.getPosition().equals(super.getPosition())) {
            return true;
        }
        return false;
    }

}
