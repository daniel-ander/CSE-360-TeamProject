package Database.Services.Interfaces;

import Models.AdminRequest;
import java.util.List;

public interface IAdminRequestService {

    List<AdminRequest> getOpenRequests();
    List<AdminRequest> getClosedRequests();

    void createRequest(String description);

    void closeRequest(int id);
    
    void reopen(int id, String newDescription);
}