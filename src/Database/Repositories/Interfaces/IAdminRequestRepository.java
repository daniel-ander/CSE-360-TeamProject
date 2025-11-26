package Database.Repositories.Interfaces;

import Models.AdminRequest;

import java.util.List;

/**
 * Repository interface for admin requests.
 * Defines basic CRUD operations used by services/controllers.
 */
public interface IAdminRequestRepository {

    List<AdminRequest> getOpenRequests();

    List<AdminRequest> getClosedRequests();

    void createRequest(String description);

    void closeRequest(int id);

    void reopen(int id, String newDescription);
    
    AdminRequest find(int id);
}
