package dungeonmania.DungeonObjects.StaticEntites;

import dungeonmania.util.Position;

public class Door extends StaticEntity {

    private int key;
    private boolean unlocked = false;

    public Door(Position position, String type, int key) {
        super(position, type);
        this.key = key;
    }

    public int getKeyCode(){
        return this.key;
    }

    public void unlock(){
        this.unlocked = true;
        this.setType(this.getType()+"_unlocked");
    }
    public boolean isUnlocked(){
        return this.unlocked;
    }
}
