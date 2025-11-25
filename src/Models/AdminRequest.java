package Models;

public class AdminRequest {

    private final int id;
    private final String description;
    private final boolean isClosed;
    private final Integer originalRequestId; // null unless reopened

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

    @Override
    public String toString() {
        if (originalRequestId == null)
            return "["+id+"] " + description;
        return "["+id+"] " + description + " (Reopened from " + originalRequestId + ")";
    }
}
