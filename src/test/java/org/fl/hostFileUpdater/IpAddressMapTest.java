package org.fl.hostFileUpdater;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class IpAddressMapTest {

	@Test
	void test() {
		
		String ip1 = "192.168.65.101" ;
		String h1  = "ioc15app.ibmplatform.com" ;
		String h2  = "ioc15app" ;
		String l1= ip1 + "\t" + h1 + " " + h2 ;
		
		IpAddressMap iam = new IpAddressMap(l1) ;
		assertEquals(ip1, iam.getIpAddress()) ;

	}

}
