package dungeonmania;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dungeonmania.DungeonObjects.Player;
import dungeonmania.util.Position;

public class TimeTravelling {
    public static void timeTravel(int nTicks){
        List<Game> ticks = DungeonManiaController.getTicks();
        if (ticks.size() <= 1)
            return;
        int index = ticks.size() - 1 - nTicks;
        if(index < 0)
            index = 0;
        List<Position> path = new ArrayList<>();
        for (int i = index + 1; i < ticks.size(); i++){
            path.add(DungeonManiaController.getTicks().get(i).getPlayer().getPosition());
        }
        Game currTick = DungeonManiaController.getGame();
        Player currPlayer = currTick.getPlayer();
        Game dstTick;
        try {
            dstTick = ticks.get(index).saveTick();
            Player oldPlayer = dstTick.getPlayer();
            oldPlayer.setPath(path);
            oldPlayer.setType("old_player");
            dstTick.setPlayer(currPlayer);
            DungeonManiaController.setGame(dstTick);
            DungeonManiaController.setTicks(ticks.subList(0, index + 1));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
