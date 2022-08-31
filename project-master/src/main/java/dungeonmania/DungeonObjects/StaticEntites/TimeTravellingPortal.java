package dungeonmania.DungeonObjects.StaticEntites;

import dungeonmania.TimeTravelling;
import dungeonmania.util.Position;

public class TimeTravellingPortal extends Portal{

    public TimeTravellingPortal(Position position, String type, String colour) {
        super(position, type, colour);
    }
    public void handleTimeTravelling(){
        TimeTravelling.timeTravel(30);
    }
}
