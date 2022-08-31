package unsw.model;

import unsw.utils.Angle;

public class LaptopDevice extends Device {
    public LaptopDevice(String deviceId, Angle position) {
        super(deviceId, position, 30, 100000);
    }
}
