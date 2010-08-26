package edu.wustl.xipHost.wg23;

import java.io.File;
import org.nema.dicom.wg23.State;

import edu.wustl.xipHost.application.Application;
import edu.wustl.xipHost.avt2ext.iterator.IterationTarget;
import edu.wustl.xipHost.wg23.HostImpl;
import edu.wustl.xipHost.wg23.StateChangeException;
import junit.framework.TestCase;

/**
 * Set of test for notifyStateChanged method of WG23 interface
 * @author Jaroslaw Krych
 *
 */
public class ChangeStateWG23Test extends TestCase {
	Application app;
	HostImpl hostImpl;
	protected void setUp() throws Exception {
		super.setUp();
		app = new Application("Application1", new File("./src-tests/edu/wustl/xipHost/application/test.bat"), "", "", new File("src-tests/edu/wustl/xipHost/application/test.png"),
				"rendering", true, "files", 1, IterationTarget.SERIES);
		hostImpl = new HostImpl(app);
	}	
	//Application 1Aa - basic flow. Incoming State is valid and app current state can be changed
	//to requested State.
	//test of null -> IDLE
	public void testChangeState1Aa() throws StateChangeException {								
		hostImpl.changeState(State.IDLE);
		assertEquals("Perfect conditions but system did not change the state.", app.getState(), State.IDLE);		
	}
	//Application 1Ab - basic flow. Incoming State is valid and app current state can be changed
	//to requested State.
	//test of IDLE -> INPROGRESS
	public void testChangeState1Ab() throws StateChangeException {								
		hostImpl.changeState(State.IDLE);
		assertEquals("Perfect conditions but system did not change the state.", app.getState(), State.IDLE);
		hostImpl.changeState(State.INPROGRESS);
		assertEquals("Perfect conditions but system did not change the state.", app.getState(), State.INPROGRESS);
	}
	//Application 1Ac - basic flow. Incoming State is valid and app current state can be changed
	//to requested State.
	//test of IDLE -> EXIT
	public void testChangeState1Ac() throws StateChangeException {						
		hostImpl.changeState(State.IDLE);
		assertEquals("Perfect conditions but system did not change the state.", app.getState(), State.IDLE);
		hostImpl.changeState(State.EXIT);
		assertEquals("Perfect conditions but system did not change the state.", app.getState(), State.EXIT);
	}
	
	//Application 1Ad - basic flow. Incoming State is valid and app current state can be changed
	//to requested State.
	//test of INPROGRESS -> SUSPENDED
	public void testChangeState1Ad() throws StateChangeException {						
		hostImpl.changeState(State.IDLE);
		hostImpl.changeState(State.INPROGRESS);
		assertEquals("Perfect conditions but system did not change the state.", app.getState(), State.INPROGRESS);
		hostImpl.changeState(State.SUSPENDED);
		assertEquals("Perfect conditions but system did not change the state.", app.getState(), State.SUSPENDED);
	}
	
	//Application 1B - alternative flow. Incoming State is null and app current state is valid.
	public void testChangeState1B() throws StateChangeException {
		hostImpl.changeState(State.IDLE);
		try{
			hostImpl.changeState(null);
			fail("State cannot be changed to null");
		}catch (StateChangeException e){
			assertTrue(true);
		}		
	}
	
	//Application 1Ca - alternative flow. App state cannot be changed to the requested state.
	//trying IDLE -> CANCELED
	public void testChangeState1Ca() throws StateChangeException {
		hostImpl.changeState(State.IDLE);
		try{
			hostImpl.changeState(State.CANCELED);
			fail("State cannot be changed to the requested state.");
		}catch (StateChangeException e){
			assertTrue(true);
		}		
	}
	
	//Application 1Cb - alternative flow. App state cannot be changed to the requested state.
	//trying INPROGRESS -> IDLE
	public void testChangeState1Cb() throws StateChangeException {
		hostImpl.changeState(State.IDLE);
		hostImpl.changeState(State.INPROGRESS);
		try{
			hostImpl.changeState(State.IDLE);
			fail("State cannot be changed to the requested state.");
		}catch (StateChangeException e){
			assertTrue(true);
		}		
	}
	
	//Application 1Cc - alternative flow. App state cannot be changed to the requested state.
	//trying INPROGRESS -> IDLE
	public void testChangeState1Cc() throws StateChangeException {
		hostImpl.changeState(State.IDLE);
		hostImpl.changeState(State.INPROGRESS);
		try{
			hostImpl.changeState(State.IDLE);
			fail("State cannot be changed to the requested state.");
		}catch (StateChangeException e){
			assertTrue(true);
		}		
	}	
}
