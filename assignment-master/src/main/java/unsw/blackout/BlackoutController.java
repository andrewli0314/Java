package unsw.blackout;

import java.io.FileInputStream;
import java.sql.SQLData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import unsw.model.*;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class BlackoutController {
    private List<Entity> entities = new ArrayList<Entity>();
    private List<FileTransferState> fileTransferStates = new ArrayList<FileTransferState>();

    public void createDevice(String deviceId, String type, Angle position) {
        if (type.equals("HandheldDevice")) {
            this.entities.add(new HandheldDevice(deviceId, position));
        } else if (type.equals("LaptopDevice")) {
            this.entities.add(new LaptopDevice(deviceId, position));
        } else if (type.equals("DesktopDevice")) {
            this.entities.add(new DesktopDevice(deviceId, position));
        }
    }

    public void removeDevice(String deviceId) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i) instanceof Device && entities.get(i).getId().equals(deviceId)) {
                entities.remove(i);
                break;
            }
        }
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        if(type.equals("StandardSatellite")) {
            this.entities.add(new StandardSatellite(satelliteId, height, position));
        } else if (type.equals("TeleportingSatellite")) {
            this.entities.add(new TeleportingSatellite(satelliteId, height, position));
        } else if (type.equals("RelaySatellite")) {
            this.entities.add(new RelaySatellite(satelliteId, height, position));
        }
    }

    public void removeSatellite(String satelliteId) {
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i) instanceof Satellite && entities.get(i).getId().equals(satelliteId)) {
                entities.remove(i);
                break;
            }
        }
    }

    public List<String> listDeviceIds() {
        List<String> deviceIds = new ArrayList<String>();
        for (Entity entity: entities) {
            if (entity instanceof Device) {
                deviceIds.add(entity.getId());
            }
        }
        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        List<String> satelliteIds = new ArrayList<String>();
        for (Entity entity: entities) {
            if (entity instanceof Satellite)
                satelliteIds.add(entity.getId());
        }
        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        Device device = null;
        for (Entity entity: entities) {
            if (entity.getId().equals(deviceId)) {
                device = (Device)entity;
                device.addFile(filename, content);
                break;
            }
        }
    }

    public EntityInfoResponse getInfo(String id) {
        for (Entity entity: entities) {
            if (entity.getId().equals(id)) {
                return new EntityInfoResponse(id, entity.getPosition(), entity.getHeight(), entity.getClass().getSimpleName(), entity.getFiles());
            }
        }
        return null;
    }

    public void simulate() {
        for (Entity entity: entities) {
            entity.simulate();
        }

        //file transfer
        for (int i = fileTransferStates.size() -1; i >= 0; i--) {
            FileTransferState fileTransferState = fileTransferStates.get(i);
            // get transfer speed
            int sendSpeed = 0;
            int receSpeed = 0;
            int speed = 0;
            Entity fromEntity = fileTransferState.getFromEntity();
            if (fromEntity instanceof Satellite) {
                int fileSendNum = 0;
                for (FileTransferState f: fileTransferStates) {
                    if (f.getFromEntity().equals(fromEntity)) {
                        fileSendNum++;
                    }
                }
                sendSpeed = (int)Math.floor(((Satellite) fromEntity).getSendBandWidth() / fileSendNum);
            }

            Entity toEntity = fileTransferState.getToEntity();
            if (toEntity instanceof Satellite) {
                int fileSendNum = 0;
                for (FileTransferState f: fileTransferStates) {
                    if (f.getToEntity().equals(toEntity)) {
                        fileSendNum++;
                    }
                }
                receSpeed = (int)Math.floor(((Satellite) toEntity).getReceiveBandWidth() / fileSendNum);
            }
            speed = sendSpeed > receSpeed? sendSpeed:receSpeed;
            fileTransferState.setCurrentBytes(fileTransferState.getCurrentBytes() + speed);
            if (fileTransferState.getCurrentBytes() == fileTransferState.getFileContent().length()) {
                toEntity.addFile(fileTransferState.getFileName(), new FileInfoResponse(fileTransferState.getFileName(), fileTransferState.getFileContent(), fileTransferState.getFileContent().length(), true));
                fileTransferStates.remove(i);
            } else {
                toEntity.addFile(fileTransferState.getFileName(), new FileInfoResponse(fileTransferState.getFileName(), fileTransferState.getFileContent().substring(0, fileTransferState.getCurrentBytes()), fileTransferState.getFileContent().length(), false));
            }
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        List<String>  communicableEntities = new ArrayList<String>();

        Device device = null;
        Satellite satellite = null;
        for (Entity entity: entities) {
            if (entity.getId().equals(id)) {
                if (entity instanceof Device) {
                    device = (Device) entity;
                } else {
                    satellite = (Satellite) entity;
                }
                break;
            }
        }

        if (device != null) {
            // it is a device
            if (device.getClass().getSimpleName().equals("DesktopDevice")) {
                // not standard satellite
                for (Entity entity: entities) {
                    if (entity instanceof Satellite) {
                        Satellite s = (Satellite) entity;
                        if (!s.getClass().getSimpleName().equals("StandardSatellite")) {
                            if (MathsHelper.getDistance(s.getHeight(), s.getPosition(), device.getPosition()) < device.getRange()
                                    && MathsHelper.isVisible(s.getHeight(), s.getPosition(), device.getPosition())) {
                                communicableEntities.add(s.getId());
                            } else if (s.getClass().getSimpleName().equals("TeleportingSatellite")){
                                for (Entity relayEntity: entities) {
                                    if (relayEntity instanceof RelaySatellite) {
                                        RelaySatellite relay = (RelaySatellite)relayEntity;
                                        if (MathsHelper.getDistance(relay.getHeight(), relay.getPosition(), device.getPosition()) < device.getRange()
                                                && MathsHelper.getDistance(relay.getHeight(), relay.getPosition(), s.getHeight(),s.getPosition()) < relay.getRange()
                                                && MathsHelper.isVisible(relay.getHeight(), relay.getPosition(), device.getPosition())
                                                && MathsHelper.isVisible(relay.getHeight(), relay.getPosition(), s.getHeight(), s.getPosition())) {
                                            communicableEntities.add(s.getId());
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                for (Entity entity: entities) {
                    if (entity instanceof Satellite) {
                        Satellite s = (Satellite) entity;
                        if (MathsHelper.getDistance(s.getHeight(), s.getPosition(), device.getPosition()) < device.getRange()
                                && MathsHelper.isVisible(s.getHeight(), s.getPosition(), device.getPosition())) {
                            communicableEntities.add(s.getId());
                        } else {
                            for (Entity relayEntity: entities) {
                                if (relayEntity instanceof RelaySatellite) {
                                    RelaySatellite relay = (RelaySatellite) relayEntity;
                                    if (relay.getClass().getSimpleName().equals("RelaySatellite")) {
                                        if (MathsHelper.getDistance(relay.getHeight(), relay.getPosition(), device.getPosition()) < device.getRange()
                                                && MathsHelper.getDistance(relay.getHeight(), relay.getPosition(), s.getHeight(),s.getPosition()) < relay.getRange()
                                                && MathsHelper.isVisible(relay.getHeight(), relay.getPosition(), device.getPosition())
                                                && MathsHelper.isVisible(relay.getHeight(), relay.getPosition(), s.getHeight(), s.getPosition())) {
                                            communicableEntities.add(s.getId());
                                            break;
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
            }

        }
        if (satellite != null) {
            for (Entity entity: entities) {
                if (MathsHelper.getDistance(satellite.getHeight(), satellite.getPosition(), entity.getHeight(), entity.getPosition()) < satellite.getRange()
                    && MathsHelper.isVisible(satellite.getHeight(), satellite.getPosition(), entity.getHeight() + 50, entity.getPosition())) {
                    if (!entity.equals(satellite)) {
                        communicableEntities.add(entity.getId());
                    }
                } else {
                    for (Entity relayEntity: entities) {
                        if (relayEntity instanceof RelaySatellite) {
                            RelaySatellite relay = (RelaySatellite) relayEntity;
                            if (relay.getClass().getSimpleName().equals("RelaySatellite")) {
                                if (MathsHelper.getDistance(relay.getHeight(), relay.getPosition(), satellite.getHeight(), satellite.getPosition()) < satellite.getRange()
                                        && MathsHelper.getDistance(relay.getHeight(), relay.getPosition(), entity.getHeight(),entity.getPosition()) < relay.getRange()
                                        && MathsHelper.isVisible(relay.getHeight(), relay.getPosition(),satellite.getHeight(), satellite.getPosition())
                                        && MathsHelper.isVisible(relay.getHeight(), relay.getPosition(), entity.getHeight() + 50, entity.getPosition())) {
                                    communicableEntities.add(entity.getId());
                                    break;
                                }
                            }
                        }
                    }
                }
            }

        }
        return communicableEntities;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        Entity fromEntity = null;
        Entity toEntity = null;
        for (Entity entity: entities) {
            if (entity.getId().equals(fromId)) {
               fromEntity = entity;
            } else if (entity.getId().equals(toId)) {
                toEntity = entity;
            }
        }

        FileInfoResponse fileInfoResponse = fromEntity.getFileInforResponse(fileName);
        if (fileInfoResponse == null || !fileInfoResponse.hasTransferCompleted()) {
            throw new FileTransferException.VirtualFileNotFoundException(fileName);
        }
        FileInfoResponse toFileInfo = toEntity.getFileInforResponse(fileName);
        if (toFileInfo != null) {
            throw new FileTransferException.VirtualFileAlreadyExistsException(fileName);
        }

        //bandwidth
        if (fromEntity instanceof StandardSatellite || fromEntity instanceof TeleportingSatellite) {
            Satellite satellite = (Satellite)fromEntity;
            Map<String, FileInfoResponse> files = fromEntity.getFiles();
            int bandWidthNum = 0;
            for (FileInfoResponse fileInfo : files.values()) {
                if (!fileInfo.hasTransferCompleted()) {
                    bandWidthNum ++;
                }
            }
            if (bandWidthNum >= satellite.getSendBandWidth()) {
                throw new FileTransferException.VirtualFileNoBandwidthException(fileName);
            }
        }
        if (toEntity instanceof StandardSatellite || toEntity instanceof TeleportingSatellite) {
            Satellite satellite = (Satellite)toEntity;
            Map<String, FileInfoResponse> files = toEntity.getFiles();
            int bandWidthNum = 0;
            int fileNum = 0;
            int byteNum = 0;
            for (FileInfoResponse fileInfo : files.values()) {
                if (!fileInfo.hasTransferCompleted()) {
                    bandWidthNum ++;
                }
                byteNum += fileInfo.getFileSize();
                fileNum++;
            }
            if (bandWidthNum >= satellite.getReceiveBandWidth()) {
                throw new FileTransferException.VirtualFileNoBandwidthException(fileName);
            }
            if (byteNum + fileInfoResponse.getFileSize() > satellite.getMaxBufferLength()) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Storage Reached");
            }
            if (fileNum >= satellite.getMaxFileNum()) {
                throw new FileTransferException.VirtualFileNoStorageSpaceException("Max Files Reached");
            }
        }

        this.fileTransferStates.add(new FileTransferState(fromEntity, toEntity, fileName, fileInfoResponse.getData()));
        toEntity.addFile(fileName, new FileInfoResponse(fileName, "", fileInfoResponse.getFileSize(), false));

    }

    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        for (Entity entity: entities) {
            if (entity instanceof Device && entity.getId().equals(deviceId)) {
                Device device = (Device)entity;
                device.setMoving(true);
                break;
            }
        }
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }

}
