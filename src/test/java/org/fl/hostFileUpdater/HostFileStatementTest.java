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

import org.fl.hostFileUpdater.IpAddressMap.Reachable;
import org.junit.jupiter.api.Test;

class HostFileStatementTest {

	@Test
	void test() {

		String ip1 = "192.168.65.101";
		String h1 = "ioc15app.ibmplatform.com";
		String h2 = "ioc15app";
		String l1 = ip1 + "\t" + h1 + " " + h2;

		String l2 = ip1 + "\t" + h2 + " \t" + h1;

		String l3 = "# this is a comment";

		HostFileStatement hfs1 = new HostFileStatement(l1);
		assertThat(hfs1.isCommentLine()).isFalse();

		HostFileStatement hfs2 = new HostFileStatement(l2);
		assertThat(hfs2.containSameHostNameWithDiffentAddress(hfs1)).isFalse();

		HostFileStatement hfs3 = new HostFileStatement(l3);
		assertThat(hfs3.isCommentLine()).isTrue();

		String ip2 = "192.168.65.103";
		String l4 = ip2 + " " + h1 + "\t" + h2;
		HostFileStatement hfs4 = new HostFileStatement(l4);
		assertThat(hfs1.containSameHostNameWithDiffentAddress(hfs4)).isTrue();

		assertThat(hfs4.getReachable()).isEqualTo(Reachable.UNKNOWN);

		hfs4.testReachable();
		assertThat(hfs4.getReachable()).isEqualTo(Reachable.FALSE);
	}

}
