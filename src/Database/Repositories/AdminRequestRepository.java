package Database.Repositories;

import Database.Repositories.Interfaces.IAdminRequestRepository;
import Database.IGeneralDBManager;
import Models.AdminRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple repository for admin requests.
 * Currently uses an in-memory list for storage so it compiles and behaves like a repository-backed implementation.
 * Constructor accepts IGeneralDBManager for future DB wiring.
 */
public class AdminRequestRepository implements IAdminRequestRepository {

    private final IGeneralDBManager dbManager; // reserved for future DB operations
    private final List<AdminRequest> storage = new ArrayList<>();
    private int nextId = 1;

    public AdminRequestRepository(IGeneralDBManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public synchronized List<AdminRequest> getOpenRequests() {
        List<AdminRequest> result = new ArrayList<>();
        for (AdminRequest r : storage) {
            if (!r.isClosed()) result.add(r);
        }
        return result;
    }

    @Override
    public synchronized List<AdminRequest> getClosedRequests() {
        List<AdminRequest> result = new ArrayList<>();
        for (AdminRequest r : storage) {
            if (r.isClosed()) result.add(r);
        }
        return result;
    }

    @Override
    public synchronized void createRequest(String description) {
        storage.add(new AdminRequest(nextId++, description, false, null));
    }

    @Override
    public synchronized void closeRequest(int id) {
        AdminRequest old = find(id);
        if (old == null) return;
        storage.remove(old);
        storage.add(new AdminRequest(old.getId(), old.getDescription(), true, old.getOriginalRequestId()));
    }

    @Override
    public synchronized void reopen(int id, String newDescription) {
        AdminRequest old = find(id);
        if (old == null || !old.isClosed()) return;
        storage.remove(old);
        storage.add(new AdminRequest(nextId++, newDescription, false, old.getId()));
    }

    private AdminRequest find(int id) {
        for (AdminRequest r : storage) if (r.getId() == id) return r;
        return null;
    }
}
