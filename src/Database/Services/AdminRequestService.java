package Database.Services;

import Database.Repositories.Interfaces.IAdminRequestRepository;
import Database.Services.Interfaces.IAdminRequestService;
import Models.AdminRequest;

import java.util.List;



public class AdminRequestService implements IAdminRequestService {
	
	private final IAdminRequestRepository adminRequestRepository;
    
    public AdminRequestService(IAdminRequestRepository adminRequestRepository) {
		this.adminRequestRepository = adminRequestRepository;
	}

    @Override
    public List<AdminRequest> getOpenRequests() {
        return adminRequestRepository.getOpenRequests();
    }

    @Override
    public List<AdminRequest> getClosedRequests() {
        return adminRequestRepository.getClosedRequests();
    }

    @Override
    public void createRequest(String description) {
        adminRequestRepository.createRequest(description);
    }

    @Override
    public void closeRequest(int id) {
    	adminRequestRepository.closeRequest(id);
    }

    @Override
    public void reopen(int id, String newDescription) {
    	adminRequestRepository.reopen(id, newDescription);
    }

    @Override
    public AdminRequest find(int id) {
    	return adminRequestRepository.find(id);
    }
}
