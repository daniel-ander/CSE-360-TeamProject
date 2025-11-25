package Database.Services;

import Database.Repositories.Interfaces.IAdminRequestRepository;
import Database.Services.Interfaces.IAdminRequestService;
import Models.AdminRequest;

import java.util.ArrayList;
import java.util.List;


//TODO: Implement DB storage instead of in-memory list

public class AdminRequestService implements IAdminRequestService {
	
	private final IAdminRequestRepository adminRequestRepository;

    private final List<AdminRequest> storage = new ArrayList<>();
    private int nextId = 1;
    
    public AdminRequestService(IAdminRequestRepository adminRequestRepository) {
		this.adminRequestRepository = adminRequestRepository;
	}

    @Override
    public List<AdminRequest> getOpenRequests() {
        return storage.stream()
                .filter(r -> !r.isClosed())
                .toList();
    }

    @Override
    public List<AdminRequest> getClosedRequests() {
        return storage.stream()
                .filter(AdminRequest::isClosed)
                .toList();
    }

    @Override
    public void createRequest(String description) {
        storage.add(new AdminRequest(nextId++, description, false, null));
    }

    @Override
    public void closeRequest(int id) {
        AdminRequest old = find(id);
        if (old == null) return;

        storage.remove(old);
        storage.add(new AdminRequest(
                old.getId(),
                old.getDescription(),
                true,
                old.getOriginalRequestId()
        ));
    }

    @Override
    public void reopen(int id, String newDescription) {
        AdminRequest old = find(id);
        if (old == null || !old.isClosed()) return;

        storage.remove(old);

        // New request with link to the original
        storage.add(new AdminRequest(
                nextId++,                      // new ID
                newDescription,                // updated description
                false,                         // reopened = open
                old.getId()                    // link to previous closed request
        ));
    }

    private AdminRequest find(int id) {
        for (AdminRequest r : storage)
            if (r.getId() == id) return r;
        return null;
    }
}
