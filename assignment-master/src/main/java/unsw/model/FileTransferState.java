package unsw.model;

public class FileTransferState {
    private Entity fromEntity;
    private Entity toEntity;
    private String fileName;
    private String fileContent;
    private int currentBytes;

    public FileTransferState(Entity fromEntity, Entity toEntity, String fileName, String fileContent) {
        this.fromEntity = fromEntity;
        this.toEntity = toEntity;
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.currentBytes = 0;
    }

    public Entity getFromEntity() {
        return fromEntity;
    }

    public void setFromEntity(Entity fromEntity) {
        this.fromEntity = fromEntity;
    }

    public Entity getToEntity() {
        return toEntity;
    }

    public void setToEntity(Entity toEntity) {
        this.toEntity = toEntity;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public int getCurrentBytes() {
        return currentBytes;
    }

    public void setCurrentBytes(int currentBytes) {
        this.currentBytes = currentBytes;
    }
}
