package dungeonmania.DungeonObjects.StaticEntites;

import dungeonmania.util.Position;

public class Portal extends StaticEntity {

    private String colour;

    public Portal(Position position, String type, String colour) {
        super(position, type);
        this.colour = colour;
        //TODO Auto-generated constructor stub
    }

    public String getColour(){
        return this.colour;
    }
}
