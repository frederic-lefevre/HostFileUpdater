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

package org.fl.hostFileUpdater.hostFile ;

import java.util.ArrayList;
import java.util.List;

import org.fl.util.os.NetworkUtils;

public class LocalAddressesHostFile extends HostFile {

	private final static String LOCALHOST = "localhost";
	private final static String LOCALIP = "127.0.0.1";
	private final static char TABCHAR = '\t';

	public LocalAddressesHostFile(String[] otherLocalHostNames) {
		super();

		// Build the local host mappings
		// Get the current IP addresses and machine name for this host
		List<String> localHostMappingsString = new ArrayList<String>();
		localHostMappingsString.add("# Local addresses mappings");

		NetworkUtils nu = new NetworkUtils(true);
		List<String> hostCurrentIpAddresses = nu.getIPv4List();
		String machineName = nu.getMachineName();
		String[] additionnalHostNames = applyPattern(machineName, otherLocalHostNames);
		localHostMappingsString.add(LOCALIP + TABCHAR + LOCALHOST);
		localHostMappingsString.add(LOCALIP + TABCHAR + machineName);
		if ((additionnalHostNames != null) && (additionnalHostNames.length > 0)) {
			for (String host : additionnalHostNames) {
				localHostMappingsString.add(LOCALIP + TABCHAR + host);
			}
		}
		for (String ip : hostCurrentIpAddresses) {
			localHostMappingsString.add(ip + TABCHAR + LOCALHOST);
			localHostMappingsString.add(ip + TABCHAR + machineName);
			if ((additionnalHostNames != null) && (additionnalHostNames.length > 0)) {
				for (String host : additionnalHostNames) {
					localHostMappingsString.add(ip + TABCHAR + host);
				}
			}
		}
		addHostFileLines(localHostMappingsString);
	}

	private String[] applyPattern(String s, String[] as) {
		if ((as != null) && (as.length > 0)) {
			for (int i = 0; i < as.length; i++) {
				as[i] = as[i].replace("%m", s);
			}
		}
		return as;
	}

}
