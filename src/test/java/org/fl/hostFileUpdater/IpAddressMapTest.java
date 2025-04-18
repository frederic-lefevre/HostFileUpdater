/*
 * MIT License

Copyright (c) 2017, 2024 Frederic Lefevre

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

import static org.assertj.core.api.Assertions.*;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.util.FilterCounter;
import org.fl.util.FilterCounter.LogRecordCounter;
import org.junit.jupiter.api.Test;

class IpAddressMapTest {

	@Test
	void test() {

		String ip1 = "192.168.65.101";
		String h1 = "ioc15app.ibmplatform.com";
		String h2 = "ioc15app";
		String l1 = ip1 + "\t" + h1 + " " + h2;

		String l2 = ip1 + "\t" + h2 + " \t" + h1;

		IpAddressMap iam = new IpAddressMap(l1);
		assertThat(iam.getIpAddress()).isEqualTo(ip1);

		IpAddressMap iam2 = new IpAddressMap(l2);
		assertThat(iam.isTheSameAs(iam2)).isTrue();

		Set<String> hostNames = iam.getHostNames();
		assertThat(hostNames).contains(h1, h2);

		String h3 = "ioc51app";
		assertThat(hostNames).doesNotContain(h3);
		iam.addHostName(h3);
		assertThat(hostNames).contains(h3);

		assertThat(iam.containSameHostNameWithDiffentAddress(iam2)).isFalse();

		String ip2 = "192.168.65.103";
		String l3 = ip2 + " " + h1;
		IpAddressMap iam3 = new IpAddressMap(l3);
		assertThat(iam.containSameHostNameWithDiffentAddress(iam3)).isTrue();
		assertThat(iam.hasSameHostNamesWithDiffentAddress(iam3)).isFalse();
		iam3.addHostName(h2).addHostName(h3);
		assertThat(iam.hasSameHostNamesWithDiffentAddress(iam3)).isTrue();
	}

	@Test
	void testWrongIpam() {

		LogRecordCounter logFilterCounter = FilterCounter.getLogRecordCounter(Logger.getLogger(IpAddressMap.class.getName()));
		IpAddressMap iam = new IpAddressMap("  bidon \t ");
		assertThat(iam.getIpAddress()).isNull();
		
		assertThat(logFilterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logFilterCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}

	@Test
	void testWrongIpam2() {

		LogRecordCounter logFilterCounter = FilterCounter.getLogRecordCounter(Logger.getLogger(IpAddressMap.class.getName()));
		
		IpAddressMap iam = new IpAddressMap("\t\t \t  \t  bidon \t ");
		assertThat(iam.getIpAddress()).isNull();

		assertThat(logFilterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logFilterCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
		
		IpAddressMap iam2 = new IpAddressMap("  bidon \t ");
		assertThat(iam.containSameHostNameWithDiffentAddress(iam2)).isFalse();
		assertThat(iam.hasSameHostNamesWithDiffentAddress(iam2)).isFalse();
		assertThat(iam.isTheSameAs(iam2)).isTrue();
		
		assertThat(logFilterCounter.getLogRecordCount()).isEqualTo(2);
		assertThat(logFilterCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(2);
	}

	@Test
	void testWrongIpam3() {

		LogRecordCounter logFilterCounter = FilterCounter.getLogRecordCounter(Logger.getLogger(IpAddressMap.class.getName()));
		
		IpAddressMap iam = new IpAddressMap("\t\t \t  \t  bidon \t ");
		assertThat(iam.getIpAddress()).isNull();

		IpAddressMap iam2 = new IpAddressMap("9.100.8.9 myHost");

		assertThat(iam.containSameHostNameWithDiffentAddress(iam2)).isFalse();
		assertThat(iam.hasSameHostNamesWithDiffentAddress(iam2)).isFalse();
		assertThat(iam.isTheSameAs(iam2)).isFalse();
		
		assertThat(logFilterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logFilterCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}

	@Test
	void testWrongIpam4() {

		LogRecordCounter logFilterCounter = FilterCounter.getLogRecordCounter(Logger.getLogger(IpAddressMap.class.getName()));
		
		IpAddressMap iam = new IpAddressMap("9.100.8.9 myHost");
		IpAddressMap iam2 = new IpAddressMap("\t\t \t  \t  bidon \t ");

		assertThat(iam.containSameHostNameWithDiffentAddress(iam2)).isFalse();
		assertThat(iam.hasSameHostNamesWithDiffentAddress(iam2)).isFalse();
		assertThat(iam.isTheSameAs(iam2)).isFalse();
		
		assertThat(logFilterCounter.getLogRecordCount()).isEqualTo(1);
		assertThat(logFilterCounter.getLogRecordCount(Level.SEVERE)).isEqualTo(1);
	}
}
