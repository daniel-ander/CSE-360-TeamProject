package Database.Repositories.Interfaces;

import Models.AdminRequest;

import java.util.List;

/**
 * Repository interface for admin requests.
 * Defines basic CRUD operations used by services/controllers.
 */
public interface IAdminRequestRepository {
    List<AdminRequest> getAllRequests();
    AdminRequest getById(int id);

    void insert(AdminRequest request);

    void update(AdminRequest request);    
}
