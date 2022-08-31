package unsw.model;

import unsw.utils.Angle;

public class HandheldDevice extends Device {
    public HandheldDevice(String deviceId, Angle position) {
        super(deviceId, position, 50, 50000);
    }
}
