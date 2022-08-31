package dungeonmania.DungeonObjects;

import java.io.Serializable;

public class Buff implements Serializable{
    
    Entity BuffType;
    int duration;

    public Buff(Entity BuffType, int duration) {
        this.BuffType = BuffType;
        this.duration = duration;
    }

    public Entity getBuffType() {
        return this.BuffType;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

}
