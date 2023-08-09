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

import java.util.Set;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class IpAddressMapTest {

	@Test
	void test() {
		
		Logger log = Logger.getLogger(IpAddressMapTest.class.getName()) ;
		
		String ip1 = "192.168.65.101" ;
		String h1  = "ioc15app.ibmplatform.com" ;
		String h2  = "ioc15app" ;
		String l1= ip1 + "\t" + h1 + " " + h2 ;
		
		String l2 = ip1 + "\t" + h2 + " \t" + h1 ;
		
		IpAddressMap iam = new IpAddressMap(l1, log) ;
		assertEquals(ip1, iam.getIpAddress()) ;

		IpAddressMap iam2 = new IpAddressMap(l2, log) ;
		assertTrue(iam.isTheSameAs(iam2)) ;
		
		Set<String> hostNames = iam.getHostNames() ;
		assertTrue(hostNames.contains(h1)) ;
		assertTrue(hostNames.contains(h2)) ;
		
		String h3  = "ioc51app" ;
		assertFalse(hostNames.contains(h3)) ;
		iam.addHostName(h3) ;
		assertTrue(hostNames.contains(h3)) ;
		
		assertFalse(iam.containSameHostNameWithDiffentAddress(iam2)) ;
		
		String ip2 =  "192.168.65.103" ;
		String l3  = ip2 + " " + h1 ;
		IpAddressMap iam3 = new IpAddressMap(l3, log) ;
		assertTrue(iam.containSameHostNameWithDiffentAddress(iam3)) ;
		assertFalse(iam.hasSameHostNamesWithDiffentAddress(iam3)) ;
		iam3.addHostName(h2).addHostName(h3) ;
		assertTrue(iam.hasSameHostNamesWithDiffentAddress(iam3)) ;
	}

	@Test
	void testWrongIpam() {
		
		Logger log = Logger.getLogger(IpAddressMapTest.class.getName()) ;
		
		IpAddressMap iam = new IpAddressMap("  bidon \t ", log) ;
		assertNull(iam.getIpAddress()) ;
	}
	
	@Test
	void testWrongIpam2() {
		
		Logger log = Logger.getLogger(IpAddressMapTest.class.getName()) ;
		
		IpAddressMap iam = new IpAddressMap("\t\t \t  \t  bidon \t ", log) ;
		assertNull(iam.getIpAddress()) ;
		
		IpAddressMap iam2 = new IpAddressMap("  bidon \t ", log) ;
		
		assertFalse(iam.containSameHostNameWithDiffentAddress(iam2)) ;
		assertFalse(iam.hasSameHostNamesWithDiffentAddress(iam2)) ;
		assertTrue(iam.isTheSameAs(iam2)) ;
	}
	
	@Test
	void testWrongIpam3() {
		
		Logger log = Logger.getLogger(IpAddressMapTest.class.getName()) ;
		
		IpAddressMap iam = new IpAddressMap("\t\t \t  \t  bidon \t ", log) ;
		assertNull(iam.getIpAddress()) ;
		
		IpAddressMap iam2 = new IpAddressMap("9.100.8.9 myHost", log) ;
		
		assertFalse(iam.containSameHostNameWithDiffentAddress(iam2)) ;
		assertFalse(iam.hasSameHostNamesWithDiffentAddress(iam2)) ;
		assertFalse(iam.isTheSameAs(iam2)) ;
	}
	
	@Test
	void testWrongIpam4() {
		
		Logger log = Logger.getLogger(IpAddressMapTest.class.getName()) ;
		
		IpAddressMap iam = new IpAddressMap("9.100.8.9 myHost", log) ;
		IpAddressMap iam2 = new IpAddressMap("\t\t \t  \t  bidon \t ", log) ;
		
		assertFalse(iam.containSameHostNameWithDiffentAddress(iam2)) ;
		assertFalse(iam.hasSameHostNamesWithDiffentAddress(iam2)) ;
		assertFalse(iam.isTheSameAs(iam2)) ;
	}
}
