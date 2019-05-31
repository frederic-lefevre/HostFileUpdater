package org.fl.hostFileUpdater;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class HostFileTest {

	@Test
	void test() {
		
		Logger log = Logger.getLogger(HostFileTest.class.getName()) ;
		
		HostFile hf1 = new HostFile(log) ;
		
		assertEquals("", hf1.toString()) ;

		List<String> statements1 = Arrays.asList(
				"127.0.0.1	localhost\r\n" ,
				"127.0.0.1	LAPTOP-4LB058J2\r\n" ,
				"# DMZ pureflex access\r\n" ,
				"192.168.113.134 ESWPURE04.ipfs.cloud.ibm.com\r\n" ,
				"192.168.113.135 ESWPURE05.ipfs.cloud.ibm.com\r\n" ) ;
	
		HostFile hf2 = new HostFile(statements1, log) ;
		
		assertEquals("", hf2.toString()) ;
		assertNull(hf2.getFilePath()) ;
		
		
	}
	
	

}
