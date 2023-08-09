/*
 * MIT License

Copyright (c) 2017, 2023 Frederic Lefevre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.fl.hostFileUpdater;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import org.fl.hostFileUpdater.IpAddressMap.Reachable;
import org.junit.jupiter.api.Test;

class HostFileStatementTest {

	@Test
	void test() {
		
		Logger log = Logger.getLogger(HostFileStatementTest.class.getName()) ;
		
		String ip1 = "192.168.65.101" ;
		String h1  = "ioc15app.ibmplatform.com" ;
		String h2  = "ioc15app" ;
		String l1= ip1 + "\t" + h1 + " " + h2 ;
		
		String l2 = ip1 + "\t" + h2 + " \t" + h1 ;
		
		String l3 = "# this is a comment" ;
		
		HostFileStatement hfs1 = new HostFileStatement(l1, log) ;
		assertFalse(hfs1.isCommentLine()) ;
		
		HostFileStatement hfs2 = new HostFileStatement(l2, log) ;
		assertFalse(hfs2.containSameHostNameWithDiffentAddress(hfs1)) ;
		
		HostFileStatement hfs3 = new HostFileStatement(l3, log) ;
		assertTrue(hfs3.isCommentLine()) ;
		
		String ip2 =  "192.168.65.103" ;
		String l4  = ip2 + " " + h1 + "\t" + h2 ;
		HostFileStatement hfs4 = new HostFileStatement(l4, log) ;
		assertTrue(hfs1.containSameHostNameWithDiffentAddress(hfs4)) ;
		
		assertEquals(Reachable.UNKNOWN,hfs4.getReachable()) ;
		
		hfs4.testReachable();
		assertEquals(Reachable.FALSE,hfs4.getReachable()) ;
	}

}
