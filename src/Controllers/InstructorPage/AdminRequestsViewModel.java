package Controllers.InstructorPage;

import Database.Services.Interfaces.IAdminRequestService;
import Models.AdminRequest;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    public AdminRequestsViewModel(IAdminRequestService service) {
        this.service = service;
        refresh();
    }

    public void refresh() {
        openRequests.setAll(service.getOpenRequests());
        closedRequests.setAll(service.getClosedRequests());
    }

    public void createRequest() {
        if (newRequestText.get() == null || newRequestText.get().isBlank())
            return;

        service.createRequest(newRequestText.get());
        newRequestText.set("");
        refresh();
    }

    public void reopenSelected() {
        AdminRequest c = selectedClosed.get();
        if (c == null) return;

        service.reopen(c.getId(), reopenDescription.get());
        reopenDescription.set("");
        refresh();
    }
    
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
