package dungeonmania.DungeonObjects.StaticEntites;

import java.util.List;

import dungeonmania.DungeonObjects.Entity;
import dungeonmania.util.Position;

public class FloorSwitch extends StaticEntity {

    private boolean isDown = false;

    public FloorSwitch(Position position, String type) {
        super(position, type);
    }
    public boolean isDown(){
        return this.isDown;
    }
    public void down(){
        this.isDown = true;
        this.setIsInteractable(false);
    }
    public void up(){
        this.isDown = false;
        this.setIsInteractable(true);
    }
    public boolean isToggledByBoulder() {
        if (isDown) {
            // If switch is down then a boulder is on it
            return true;
        }
        return false;
    }
}
