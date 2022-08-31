package unsw.model;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;
import static unsw.utils.MathsHelper.CLOCKWISE;

public class TeleportingSatellite extends Satellite {
    private int direction;

    public TeleportingSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, 1000, 200000,200, Integer.MAX_VALUE, 10, 15);
        this.direction = ANTI_CLOCKWISE; // anticlockwise.
    }

    public void simulate() {
        Angle moveAngle = Angle.fromRadians(((double)this.speed / this.height));
        Angle newPosition;
        if (this.direction == ANTI_CLOCKWISE) {
            newPosition = position.add(moveAngle);
            if (newPosition.toDegrees() > 180) {
                position = Angle.fromDegrees(360);
                this.direction = CLOCKWISE;
            } else {
                position = newPosition;
            }
        } else {
            newPosition = position.subtract(moveAngle);
            if (newPosition.toDegrees() < 180) {
                position = Angle.fromDegrees(0);
                this.direction = ANTI_CLOCKWISE;
            } else {
                position = newPosition;
            }
        }
    }
}
