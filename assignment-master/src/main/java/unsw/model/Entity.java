package unsw.model;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import java.util.HashMap;
import java.util.Map;

public class Entity {
    protected String id;
    protected double height;
    protected Angle position;
    protected int speed;
    protected int range;
    protected Map<String, FileInfoResponse> files;

    public Entity(String id, double height, Angle position, int speed, int range) {
        this.id = id;
        this.height = height;
        this.position = position;
        this.speed = speed;
        this.range = range;
        this.files = new HashMap<String, FileInfoResponse>();
    }

    public FileInfoResponse getFileInforResponse(String flieName) {
        return files.get(flieName);
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Angle getPosition() {
        return position;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public Map<String, FileInfoResponse> getFiles() {
        return files;
    }

    public void setFiles(Map<String, FileInfoResponse> files) {
        this.files = files;
    }

    public void addFile(String fileName, FileInfoResponse fileInfoResponse) {
        this.files.put(fileName, fileInfoResponse);
    }
    public void simulate() {

    }
}
