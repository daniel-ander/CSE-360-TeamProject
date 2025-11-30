package Controllers.InstructorPage;

import Database.Services.Interfaces.IAdminRequestService;
import Models.AdminRequest;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * ViewModel for managing admin requests inside Instructor page
 */
public class AdminRequestsViewModel {

    private final IAdminRequestService service;

    private final ObservableList<AdminRequest> openRequests =
            FXCollections.observableArrayList();
    private final ObservableList<AdminRequest> closedRequests =
            FXCollections.observableArrayList();

    private final StringProperty newRequestText = new SimpleStringProperty();
    private final StringProperty reopenDescription = new SimpleStringProperty();

    private final ObjectProperty<AdminRequest> selectedOpen =
            new SimpleObjectProperty<>();
    private final ObjectProperty<AdminRequest> selectedClosed =
            new SimpleObjectProperty<>();

    /**
     * AdminRequestsViewModel constructor sets up service and refreshes requests list
     * @param service
     */
    public AdminRequestsViewModel(IAdminRequestService service) {
        this.service = service;
        refresh();
    }

    /**
     * Refreshes requests list
     */
    public void refresh() {
        openRequests.setAll(service.getOpenRequests());
        closedRequests.setAll(service.getClosedRequests());
    }

    /**
     * Creates a new request
     */
    public void createRequest() {
        if (newRequestText.get() == null || newRequestText.get().isBlank())
            return;

        service.createRequest(newRequestText.get());
        newRequestText.set("");
        refresh();
    }

    /**
     * Reopens a closed request
     */
    public boolean reopenSelected() {
        AdminRequest c = selectedClosed.get();
        if (c == null) return false;

        if (reopenDescription.get() == null || reopenDescription.get().isBlank()) {
        	return false;
        }
        
        service.reopen(c.getId(), reopenDescription.get());
        reopenDescription.set("");
        refresh();
        return true;
    }
    
    /**
     * Closes an opened request
     */
    public void closeSelected() {
    	AdminRequest r = selectedOpen.get();
    	if (r == null || r.isClosed()) return;
    	
    	service.closeRequest(r.getId());
    	refresh();
    }

    // getters
    public ObservableList<AdminRequest> getOpenRequests() { return openRequests; }
    public ObservableList<AdminRequest> getClosedRequests() { return closedRequests; }

    public StringProperty newRequestTextProperty() { return newRequestText; }
    public StringProperty reopenDescriptionProperty() { return reopenDescription; }

    public ObjectProperty<AdminRequest> selectedOpenProperty() { return selectedOpen; }
    public ObjectProperty<AdminRequest> selectedClosedProperty() { return selectedClosed; }
}
