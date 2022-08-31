package dungeonmania.DungeonObjects.BattleEntities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import dungeonmania.util.Position;

public class Spider extends BattleEntity {

    private List<Position> movementTiles = new ArrayList<>();
    private boolean isClockwise;

    public Spider(Position position, String type, JSONObject config) {
        super(position, type);

        super.setAttack(config.getDouble("spider_attack"));
        super.setHealth(config.getDouble("spider_health"));

        movementTiles = position.getAdjacentPositions();
        isClockwise = true;
    }

    public void reverse(){
        this.isClockwise = !this.isClockwise;
    }
    @Override
    public Position getNextPosition() {
        // Get next position in the movement tiles
        if (this.getMovementStrategy() == null){
            int currentTileIdx = -1;
            for (int i = 0; i < movementTiles.size(); i++) {
                if (this.getPosition().equals(movementTiles.get(i))) {
                    currentTileIdx = i;
                }
            }
            if (currentTileIdx == -1) {
                // Spider was just initialsied and still at centre
                return movementTiles.get(1);
            }
            int nextTIleIdx;

            // Spiders move in a circle based on adjacent tiles initialised
            // at spawn. Index can be found if spider is clockwise or anticlockwise.
            if (isClockwise) {
                nextTIleIdx = currentTileIdx + 1;
                if (nextTIleIdx >= movementTiles.size()) {
                    return movementTiles.get(0);
                } else {
                    return movementTiles.get(nextTIleIdx);
                }
            } else {
                nextTIleIdx = currentTileIdx - 1;
                if (nextTIleIdx < 0) {
                    return movementTiles.get(movementTiles.size() - 1);
                } else {
                    return movementTiles.get(nextTIleIdx);
                }
            }
        } else {
            return this.getMovementStrategy().getNextPosition();
        }
    }



    
}
