package unsw.model;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import java.util.HashMap;
import java.util.Map;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class Device extends Entity{
    private boolean isMoving;

    public Device (String deviceId, Angle position, int speed, int range) {
        super(deviceId, RADIUS_OF_JUPITER, position, speed, range);
        this.isMoving = false;
    }

    public boolean hasFile(String filename) {
        return files.containsKey(filename);
    }

    public void addFile(String filename, String content) {
        this.files.put(filename, new FileInfoResponse(filename, content, content.length(), true));
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean moving) {
        isMoving = moving;
    }

    public void simulate() {
        if (isMoving) {
            Angle moveAngle = Angle.fromRadians(((double) this.speed / this.height));
            Angle newPosition = position.add(moveAngle);
            if (newPosition.toDegrees() > 360) {
                this.position = Angle.fromDegrees(newPosition.toDegrees() - 360);
            } else {
                this.position = newPosition;
            }
        }
    }
}
