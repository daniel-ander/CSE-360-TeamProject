package Models;

import java.sql.Timestamp;

// reviewer request model class
public class ReviewerRequest {
    private int requestId;
    private int studentId;
    private int instructorId;
    private Timestamp requestDate;
    private String status;

    // reviewer request constructors
    public ReviewerRequest() {
    }
    public ReviewerRequest(int studentId, int instructorId) {
        this.studentId = studentId;
        this.instructorId = instructorId;
        this.status = "PENDING";
        this.requestDate = new Timestamp(System.currentTimeMillis());
    }

    // getters and setters
    public int getRequestId() {
        return requestId;
    }

    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public Timestamp getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Timestamp requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
