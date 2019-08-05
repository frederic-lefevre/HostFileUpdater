package org.fl.hostFileUpdater.hostFile;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.fl.hostFileUpdater.HostFileStatement;
import org.fl.hostFileUpdater.hostFile.HostFile;
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
		
		assertFalse(hf1.includes(hf2)) ;
		assertTrue(hf2.includes(hf1)) ;
		
		List<String> statements2 = Arrays.asList(
				"127.0.0.1	localhost\r\n" ,		
				"192.168.113.134 ESWPURE04.ipfs.cloud.ibm.com\r\n" ,
				"192.168.113.135 ESWPURE05.ipfs.cloud.ibm.com\r\n" ) ;
		
		HostFile hf3 = new HostFile(statements2, log) ;
		
		hf1.append(hf3) ;

		assertTrue(hf2.includes(hf3)) ;
		assertTrue(hf2.includes(hf1)) ;		
		assertFalse(hf1.includes(hf2)) ;
		
		List<String> statements3 = Arrays.asList(				
				"127.0.0.1	LAPTOP-4LB058J2\r\n" 
				 ) ;
		HostFile hf4 = new HostFile(statements3, log) ;
		HostFile hf5 = new HostFile(log) ;
		hf5.append(hf3).append(hf4) ;
		assertTrue(hf2.includes(hf5)) ;
		assertTrue(hf5.includes(hf4)) ;
		
		List<HostFileStatement> emptyList = hf5.getNotIncludedStatements(hf4) ;
		assertEquals(0, emptyList.size()) ;
		
		List<HostFileStatement> stList = hf3.getNotIncludedStatements(hf2) ;
		assertEquals(1, stList.size()) ;

		HostFileStatement hfs1 = stList.get(0) ;
		HostFileStatement hfs2 = new HostFileStatement("127.0.0.1	LAPTOP-4LB058J2\r\n", log) ;
		assertTrue(hfs1.getIpAddressMap().isTheSameAs(hfs2.getIpAddressMap())) ;
		
		List<String> statements4 = Arrays.asList(				
				"127.0.0.1	SomeThingElse\n" 
				 ) ;
		HostFile hf6 = new HostFile(statements4, log) ;

		List<HostFile> hfList = new ArrayList<HostFile>() ;
		hfList.add(hf3) ;
		hfList.add(hf4) ;
		hfList.add(hf6) ;
		
		List<HostFile> includuedHf = hf2.getIncludedHostFiles(hfList) ;
		
		assertEquals(2, includuedHf.size()) ;
		assertFalse(includuedHf.contains(hf6)) ;
		assertTrue(includuedHf.contains(hf3)) ;
		assertTrue(includuedHf.contains(hf4)) ;
	}
	
	

}
