package Tests;

import Models.AdminRequest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AdminRequestTest {
	@Test
	public void Constructor_WithValidInputs_SetsFieldsCorrectly() {
	    AdminRequest req = new AdminRequest(5, "Test Request", false, null);

	    assertEquals(5, req.getId());
	    assertEquals("Test Request", req.getDescription());
	    assertFalse(req.isClosed());
	    assertNull(req.getOriginalRequestId());
	}

	@Test
	public void Constructor_WithOriginalRequestId_SetsOriginalRequestId() {
	    AdminRequest req = new AdminRequest(10, "Test Request", true, 3);

	    assertEquals(10, req.getId());
	    assertEquals("Test Request", req.getDescription());
	    assertTrue(req.isClosed());
	    assertEquals(3, req.getOriginalRequestId());
	}

	
	@Test
	public void GetDescription_ReturnsCorrectValue() {
	    AdminRequest req = new AdminRequest(1, "Test Request", false, null);
	    assertEquals("Test Request", req.getDescription());
	}

	@Test
	public void IsClosed_ReturnsCorrectBoolean() {
	    AdminRequest req = new AdminRequest(1, "Test Request", true, null);
	    assertTrue(req.isClosed());
	}

	
	@Test
	public void SetClosed_UpdatesClosedStatus() {
	    AdminRequest req = new AdminRequest(1, "Test Request", false, null);
	    req.setClosed(true);
	    assertTrue(req.isClosed());
	}

	@Test
	public void SetDescription_WithValidInputs_UpdatesDescription() {
	    AdminRequest req = new AdminRequest(1, "Old Request", false, null);
	    req.setDescription("New Request");
	    assertEquals("New Request", req.getDescription());
	}

	@Test
	public void SetDescription_WhenNullDescription_UpdatesDescription() {
	    AdminRequest req = new AdminRequest(1, "Old Request", false, null);
	    req.setDescription(null);
	    assertNull(req.getDescription());
	}

	@Test
	public void ToString_WithoutOriginalRequestId_ReturnsCorrectString() {
	    AdminRequest req = new AdminRequest(2, "Test Request", false, null);
	    assertEquals("[2] Test Request", req.toString());
	}

	@Test
	public void ToString_WithOriginalRequestId_ReturnsReopenedFormat() {
	    AdminRequest req = new AdminRequest(7, "Test Request", false, 3);
	    assertEquals("[7] Test Request (Reopened from 3)", req.toString());
	}
}
