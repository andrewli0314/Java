package unsw.model;

import unsw.utils.Angle;

public class DesktopDevice extends Device {
    public DesktopDevice(String deviceId, Angle position) {
        super(deviceId, position, 20, 200000);
    }
}
