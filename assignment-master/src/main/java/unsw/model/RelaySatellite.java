package unsw.model;

import unsw.utils.Angle;

import static unsw.utils.MathsHelper.ANTI_CLOCKWISE;
import static unsw.utils.MathsHelper.CLOCKWISE;

public class RelaySatellite extends Satellite {
    private int direction;

    public RelaySatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, 1500, 300000, 0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
        if (position.toDegrees() >= 345 || position.toDegrees() <= 140) {
            this.direction = ANTI_CLOCKWISE;
        } else {
            this.direction = CLOCKWISE;
        }
    }

    public void simulate() {
        Angle moveAngle = Angle.fromRadians(((double)this.speed / this.height));
        Angle newPosition;
        if (this.direction == ANTI_CLOCKWISE) {
            if (position.toDegrees() > 190 && position.toDegrees() < 345) {
                //edge
                newPosition = position.subtract(moveAngle);
                position = newPosition;
                this.direction = CLOCKWISE;
            } else {
                newPosition = position.add(moveAngle);
                if (newPosition.toDegrees() > 360) {
                    position = Angle.fromDegrees(newPosition.toDegrees() - 360);
                } else {
                    position = newPosition;
                }
            }
        } else {
            if (position.toDegrees() < 140) {
                newPosition = position.add(moveAngle);
                this.direction = ANTI_CLOCKWISE;
            } else {
                newPosition = position.subtract(moveAngle);
            }
            position = newPosition;

        }
    }
}
