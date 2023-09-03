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

package org.fl.hostFileUpdater.hostFile;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.fl.hostFileUpdater.HostFileStatement;
import org.junit.jupiter.api.Test;

class HostFileTest {

	@Test
	void test() {

		HostFile hf1 = new HostFile();

		assertThat(hf1).hasToString("");

		List<String> statements1 = Arrays.asList("127.0.0.1	localhost\r\n", "127.0.0.1	LAPTOP-4LB058J2\r\n",
				"# DMZ pureflex access\r\n", "192.168.113.134 ESWPURE04.ipfs.cloud.ibm.com\r\n",
				"192.168.113.135 ESWPURE05.ipfs.cloud.ibm.com\r\n");

		HostFile hf2 = new HostFile(statements1);

		assertThat(hf2).hasToString("");

		assertThat(hf2.getFilePath()).isNull();

		assertThat(hf1.includes(hf2)).isFalse();
		assertThat(hf2.includes(hf1)).isTrue();

		List<String> statements2 = Arrays.asList("127.0.0.1	localhost\r\n",
				"192.168.113.134 ESWPURE04.ipfs.cloud.ibm.com\r\n", "192.168.113.135 ESWPURE05.ipfs.cloud.ibm.com\r\n");

		HostFile hf3 = new HostFile(statements2);

		hf1.append(hf3);

		assertThat(hf2.includes(hf3)).isTrue();
		assertThat(hf2.includes(hf1)).isTrue();
		assertThat(hf1.includes(hf2)).isFalse();

		List<String> statements3 = Arrays.asList("127.0.0.1	LAPTOP-4LB058J2\r\n");
		HostFile hf4 = new HostFile(statements3);
		HostFile hf5 = new HostFile();
		hf5.append(hf3).append(hf4);
		assertThat(hf2.includes(hf5)).isTrue();
		assertThat(hf5.includes(hf4)).isTrue();

		List<HostFileStatement> emptyList = hf5.getNotIncludedStatements(hf4);
		assertThat(emptyList).isEmpty();

		List<HostFileStatement> stList = hf3.getNotIncludedStatements(hf2);
		assertThat(stList).hasSize(1);

		HostFileStatement hfs1 = stList.get(0);
		HostFileStatement hfs2 = new HostFileStatement("127.0.0.1	LAPTOP-4LB058J2\r\n");
		assertThat(hfs1.getIpAddressMap().isTheSameAs(hfs2.getIpAddressMap())).isTrue();

		List<String> statements4 = Arrays.asList("127.0.0.1	SomeThingElse\n");
		HostFile hf6 = new HostFile(statements4);

		List<HostFile> hfList = new ArrayList<HostFile>();
		hfList.add(hf3);
		hfList.add(hf4);
		hfList.add(hf6);

		List<HostFile> includuedHf = hf2.getIncludedHostFiles(hfList);

		assertThat(includuedHf).hasSize(2);
		assertThat(includuedHf.contains(hf6)).isFalse();

		assertThat(includuedHf.contains(hf3)).isTrue();
		assertThat(includuedHf.contains(hf4)).isTrue();
	}

}
