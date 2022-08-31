package unsw.model;

public enum DeviceType {
    HandheldDevice("HandheldDevice", 50000, 50),
    LaptopDevice("LaptopDevice", 100000, 30),
    DesktopDevice("DesktopDevice", 200000, 20);


    private String typeName;
    private int range;
    private int speed;

    private DeviceType(String typeName, int range, int speed) {
        this.typeName  = typeName;
        this.range = range;
        this.speed = speed;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getRange() {
        return range;
    }

    public int getSpeed() {
        return speed;
    }
}
