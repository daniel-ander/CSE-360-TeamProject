package Tests;

import Models.AdminRequest;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AdminRequest
 */
public class AdminRequestTest {
	
	/**
	 * Tests that Constructor, With Valid Inputs, Sets Fields Correctly
	 */
	@Test
	public void Constructor_WithValidInputs_SetsFieldsCorrectly() {
	    AdminRequest req = new AdminRequest(5, "Test Request", false, null);

	    assertEquals(5, req.getId());
	    assertEquals("Test Request", req.getDescription());
	    assertFalse(req.isClosed());
	    assertNull(req.getOriginalRequestId());
	}
	
	/**
	 * Tests that Constructor, With Original Request Id, Sets Original Request Id
	 */
	@Test
	public void Constructor_WithOriginalRequestId_SetsOriginalRequestId() {
	    AdminRequest req = new AdminRequest(10, "Test Request", true, 3);

	    assertEquals(10, req.getId());
	    assertEquals("Test Request", req.getDescription());
	    assertTrue(req.isClosed());
	    assertEquals(3, req.getOriginalRequestId());
	}

	
	/**
	 * Tests that GetDescription, Returns Correct Value
	 */
	@Test
	public void GetDescription_ReturnsCorrectValue() {
	    AdminRequest req = new AdminRequest(1, "Test Request", false, null);
	    assertEquals("Test Request", req.getDescription());
	}
	
	/**
	 * Tests that IsClosed, Returns Correct Boolean
	 */
	@Test
	public void IsClosed_ReturnsCorrectBoolean() {
	    AdminRequest req = new AdminRequest(1, "Test Request", true, null);
	    assertTrue(req.isClosed());
	}

	
	/**
	 * Tests that SetClosed, Updates Closed Status
	 */
	@Test
	public void SetClosed_UpdatesClosedStatus() {
	    AdminRequest req = new AdminRequest(1, "Test Request", false, null);
	    req.setClosed(true);
	    assertTrue(req.isClosed());
	}
	
	/**
	 * Tests that SetDescription, With Valid Inputs, Updates Description
	 */
	@Test
	public void SetDescription_WithValidInputs_UpdatesDescription() {
	    AdminRequest req = new AdminRequest(1, "Old Request", false, null);
	    req.setDescription("New Request");
	    assertEquals("New Request", req.getDescription());
	}
	
	/**
	 * Tests that SetDescription, When Null Description, Updates Description
	 */
	@Test
	public void SetDescription_WhenNullDescription_UpdatesDescription() {
	    AdminRequest req = new AdminRequest(1, "Old Request", false, null);
	    req.setDescription(null);
	    assertNull(req.getDescription());
	}
	
	/**
	 * Tests that ToString, Without Original Request Id, Returns Correct String
	 */
	@Test
	public void ToString_WithoutOriginalRequestId_ReturnsCorrectString() {
	    AdminRequest req = new AdminRequest(2, "Test Request", false, null);
	    assertEquals("[2] Test Request", req.toString());
	}
	
	/**
	 * Tests that ToString, With Original Request Id, Returns Reopened Format
	 */
	@Test
	public void ToString_WithOriginalRequestId_ReturnsReopenedFormat() {
	    AdminRequest req = new AdminRequest(7, "Test Request", false, 3);
	    assertEquals("[7] Test Request (Reopened from 3)", req.toString());
	}
}
