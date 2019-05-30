package org.fl.hostFileUpdater;

import static org.junit.jupiter.api.Assertions.*;

import org.fl.hostFileUpdater.IpAddressMap.Reachable;
import org.junit.jupiter.api.Test;

class HostFileStatementTest {

	@Test
	void test() {
		
		String ip1 = "192.168.65.101" ;
		String h1  = "ioc15app.ibmplatform.com" ;
		String h2  = "ioc15app" ;
		String l1= ip1 + "\t" + h1 + " " + h2 ;
		
		String l2 = ip1 + "\t" + h2 + " \t" + h1 ;
		
		String l3 = "# this is a comment" ;
		
		HostFileStatement hfs1 = new HostFileStatement(l1) ;
		assertFalse(hfs1.isCommentLine()) ;
		
		HostFileStatement hfs2 = new HostFileStatement(l2) ;
		assertFalse(hfs2.containSameHostNameWithDiffentAddress(hfs1)) ;
		
		HostFileStatement hfs3 = new HostFileStatement(l3) ;
		assertTrue(hfs3.isCommentLine()) ;
		
		String ip2 =  "192.168.65.103" ;
		String l4  = ip2 + " " + h1 + "\t" + h2 ;
		HostFileStatement hfs4 = new HostFileStatement(l4) ;
		assertTrue(hfs1.containSameHostNameWithDiffentAddress(hfs4)) ;
		
		assertEquals(Reachable.UNKNOWN,hfs4.getReachable()) ;
		
		hfs4.testReachable();
		assertEquals(Reachable.FALSE,hfs4.getReachable()) ;
	}

}
