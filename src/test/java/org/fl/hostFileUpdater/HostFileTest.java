package org.fl.hostFileUpdater;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class HostFileTest {

	@Test
	void test() {
		
		Logger log = Logger.getLogger(HostFileTest.class.getName()) ;
		
		HostFile hf1 = new HostFile(log) ;
		
		assertEquals("", hf1.toString()) ;

	}

}
