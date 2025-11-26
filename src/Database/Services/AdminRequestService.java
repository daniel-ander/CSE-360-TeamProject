package Database.Services;

import Database.Repositories.Interfaces.IAdminRequestRepository;
import Database.Services.Interfaces.IAdminRequestService;
import Models.AdminRequest;

import java.util.ArrayList;
import java.util.List;


public class AdminRequestService implements IAdminRequestService {
	
	private final IAdminRequestRepository adminRequestRepository;
    
    public AdminRequestService(IAdminRequestRepository adminRequestRepository) {
		this.adminRequestRepository = adminRequestRepository;
	}

    @Override
    public List<AdminRequest> getOpenRequests() {
        List<AdminRequest> adminRequests  = adminRequestRepository.getAllRequests();
        List<AdminRequest> openRequests = new ArrayList<AdminRequest>();
        
        for(AdminRequest request : adminRequests){
        	if (!request.isClosed()) {
        		openRequests.add(request);
        	}
        }
        return openRequests;
    }

    @Override
    public List<AdminRequest> getClosedRequests() {
        List<AdminRequest> adminRequests  = adminRequestRepository.getAllRequests();
        List<AdminRequest> openRequests = new ArrayList<AdminRequest>();
        
        for(AdminRequest request : adminRequests){
        	if (request.isClosed()) {
        		openRequests.add(request);
        	}
        }
        return openRequests;
    }

    @Override
    public void createRequest(String description) {
    	AdminRequest request = new AdminRequest(0, description, false, null);
        adminRequestRepository.insert(request);
    }
    
    @Override
    public void closeRequest(int id) {
    	AdminRequest oldRequest = adminRequestRepository.getById(id);
    	if (oldRequest == null || oldRequest.isClosed()) return;
    	
    	oldRequest.setClosed(true);
    	adminRequestRepository.update(oldRequest);    	
    }

    @Override
    public void reopen(int id, String newDescription) {
    	AdminRequest oldRequest = adminRequestRepository.getById(id);
    	if (oldRequest == null || !oldRequest.isClosed()) return;
    	
    	
    	AdminRequest newRequest = new AdminRequest(
    		0,
    		newDescription,
    		false,
    		oldRequest.getId()
		);
    	
    	adminRequestRepository.insert(newRequest);
    }
}
