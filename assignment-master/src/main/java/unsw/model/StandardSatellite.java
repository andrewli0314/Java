package unsw.model;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {


    public StandardSatellite(String satelliteId, double height, Angle position) {
        super(satelliteId, height, position, 2500, 150000,80, 3, 1, 1);

    }

    public void simulate() {
        Angle moveAngle = Angle.fromRadians(((double)this.speed / this.height));
        Angle newPosition = position.subtract(moveAngle);
        if (newPosition.toDegrees() < 0) {
            this.position = Angle.fromDegrees(newPosition.toDegrees() + 360);
        } else {
            this.position = newPosition;
        }
    }

}
