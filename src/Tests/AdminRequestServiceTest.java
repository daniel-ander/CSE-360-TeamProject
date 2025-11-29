package Tests;

import Database.Repositories.Interfaces.IAdminRequestRepository;
import Database.Services.AdminRequestService;
import Models.AdminRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AdminRequestServiceTest {

    private IAdminRequestRepository mockRepo;
    private AdminRequestService service;

    @BeforeEach
    public void setup() {
        mockRepo = mock(IAdminRequestRepository.class);
        service = new AdminRequestService(mockRepo);
    }


    @Test
    public void GetOpenRequests_ReturnsOnlyOpenRequests() {
        AdminRequest r1 = new AdminRequest(1, "A", false, null);
        AdminRequest r2 = new AdminRequest(2, "B", true, null);
        AdminRequest r3 = new AdminRequest(3, "C", false, null);
        when(mockRepo.getAllRequests()).thenReturn(Arrays.asList(r1, r2, r3));

        List<AdminRequest> result = service.getOpenRequests();

        assertEquals(2, result.size());
        assertTrue(result.contains(r1));
        assertTrue(result.contains(r3));
        assertFalse(result.contains(r2));
    }

    @Test
    public void GetClosedRequests_ReturnsOnlyClosedRequests() {
        AdminRequest r1 = new AdminRequest(1, "A", false, null);
        AdminRequest r2 = new AdminRequest(2, "B", true, null);
        when(mockRepo.getAllRequests()).thenReturn(Arrays.asList(r1, r2));

        List<AdminRequest> result = service.getClosedRequests();

        assertEquals(1, result.size());
        assertEquals(r2, result.get(0));
    }

    @Test
    public void CreateRequest_WithValidInputs_InsertsNewRequest() {
        ArgumentCaptor<AdminRequest> captor = ArgumentCaptor.forClass(AdminRequest.class);

        service.createRequest("Test Request");

        verify(mockRepo).insert(captor.capture());
        AdminRequest inserted = captor.getValue();
        assertEquals(0, inserted.getId());
        assertEquals("Test Request", inserted.getDescription());
        assertFalse(inserted.isClosed());
        assertNull(inserted.getOriginalRequestId());
    }

    @Test
    public void CloseRequest_WhenRequestDoesNotExist_DoesNothing() {
        when(mockRepo.getById(5)).thenReturn(null);

        service.closeRequest(5);

        verify(mockRepo, never()).update(any());
    }

    @Test
    public void CloseRequest_WhenAlreadyClosed_DoesNothing() {
        AdminRequest closed = new AdminRequest(3, "Test Request", true, null);
        when(mockRepo.getById(3)).thenReturn(closed);

        service.closeRequest(3);

        verify(mockRepo, never()).update(any());
    }

    @Test
    public void CloseRequest_WhenOpen_ClosesRequest() {
        AdminRequest open = new AdminRequest(1, "Test Request", false, null);
        when(mockRepo.getById(1)).thenReturn(open);

        service.closeRequest(1);

        assertTrue(open.isClosed());
        verify(mockRepo).update(open);
    }

    @Test
    public void ReopenRequest_WhenDoesNotExist_DoesNothing() {
        when(mockRepo.getById(10)).thenReturn(null);

        service.reopen(10, "Test Description");

        verify(mockRepo, never()).insert(any());
    }

    @Test
    public void ReopenRequest_WhenNotClosed_DoesNothing() {
        AdminRequest open = new AdminRequest(2, "Test Request", false, null);
        when(mockRepo.getById(2)).thenReturn(open);

        service.reopen(2, "Test Request 2");

        verify(mockRepo, never()).insert(any());
    }

    @Test
    public void ReopenRequest_WhenClosed_InsertsNewRequest() {
        AdminRequest closed = new AdminRequest(7, "Old Description", true, null);
        when(mockRepo.getById(7)).thenReturn(closed);
        ArgumentCaptor<AdminRequest> captor = ArgumentCaptor.forClass(AdminRequest.class);

        service.reopen(7, "New Description");

        verify(mockRepo).insert(captor.capture());
        AdminRequest inserted = captor.getValue();

        assertEquals(0, inserted.getId());
        assertEquals("New Description", inserted.getDescription());
        assertFalse(inserted.isClosed());
        assertEquals(7, inserted.getOriginalRequestId());
    }
}
