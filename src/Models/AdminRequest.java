package Models;

public class AdminRequest {

    private final int id;
    private String description;
    private boolean isClosed;
    private final Integer originalRequestId;

    public AdminRequest(int id, String description, boolean isClosed, Integer originalRequestId) {
        this.id = id;
        this.description = description;
        this.isClosed = isClosed;
        this.originalRequestId = originalRequestId;
    }

    public int getId() { return id; }
    public String getDescription() { return description; }
    public boolean isClosed() { return isClosed; }
    public Integer getOriginalRequestId() { return originalRequestId; }
    
    public void setClosed(boolean status) {
    	this.isClosed = status;
    }
    
    public void setDescription(String newDescription) {
    	this.description = newDescription;
    }

    @Override
    public String toString() {
        if (originalRequestId == null)
            return "["+id+"] " + description;
        return "["+id+"] " + description + " (Reopened from " + originalRequestId + ")";
    }
}
