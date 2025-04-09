/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IpAddressMap {

	private static final Logger log = Logger.getLogger(IpAddressMap.class.getName());
	
	public enum Reachable {
		UNKNOWN, TRUE, FALSE
	};

	private final String ipAddress;
	private final InetAddress inet;
	private Set<String> hostNames;
	private Reachable reachable;

	// Mapping between an IP address and a list of host names
	// The host file line must not be a comment line, but may includes a comment
	public IpAddressMap(String hostFileLine) {

		reachable = Reachable.UNKNOWN;

		String[] items = hostFileLine.trim().split(" |\t");
		hostNames = new HashSet<String>();
		if ((items != null) && (items.length > 1)) {

			String ipAddTemp = items[0].trim();
			InetAddress inetTemp = null;
			try {
				inetTemp = InetAddress.getByName(ipAddTemp);
			} catch (Exception e) {
				ipAddTemp = null;
				log.log(Level.SEVERE, "Wrong IpAddressMap line: " + hostFileLine, e);
			}
			ipAddress = ipAddTemp;
			inet = inetTemp;

			boolean comment = false;
			for (int i = 1; (i < items.length) && (!comment); i++) {
				String host = items[i].trim();
				if (!host.startsWith("#")) {
					if (!host.isEmpty()) {
						hostNames.add(host.toLowerCase());
					}
				} else {
					comment = true;
				}
			}
		} else {
			ipAddress = null;
			inet = null;
			log.severe("Wrong IpAddressMap line: " + hostFileLine);
		}
	}

	// Add a host name the the mapping
	public IpAddressMap addHostName(String hostName) {
		if (ipAddress != null) {
			hostNames.add(hostName) ;
		}
		return this ;
	}
	
	// True if the IP address maps have the same IP and the same set of names 
	public boolean isTheSameAs(IpAddressMap ipam) {
		
		if (ipAddress == null) {
			return (ipam.getIpAddress() == null) ;
		} else if (ipam.getIpAddress() == null) {
			return false ;
		} else {
			return ( ipam.getIpAddress().equals(ipAddress) && ipam.getHostNames().equals(hostNames) ) ;
		}
	}
	
	// True if the IP address maps have not the same IP but have the same set of names
	public boolean hasSameHostNamesWithDiffentAddress(IpAddressMap ipam) {
		if ((ipAddress == null) || (ipam.getIpAddress() == null)) {
			return false ;
		} else {
			return ( (! ipam.getIpAddress().equals(ipAddress)) && ipam.getHostNames().equals(hostNames) ) ;
		}
	}
	
	// True if the IP address maps have not the same IP but have a name in common
	public boolean containSameHostNameWithDiffentAddress(IpAddressMap ipam) {
		if ((ipAddress == null) || (ipam.getIpAddress() == null)) {
			return false ;
		} else {
			return ( (! ipam.getIpAddress().equals(ipAddress)) && (! Collections.disjoint(ipam.getHostNames(), hostNames)) ) ; 
		}
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public Set<String> getHostNames() {
		return hostNames;
	}

	public boolean testReachable() {
			
		boolean hasBeenReach = false ;
		if (ipAddress != null) {
			try {
				if (inet != null) {
					hasBeenReach = inet.isReachable(3000) ;
				} 
			} catch (IOException e) {
				// means unreachable
			}
			if (hasBeenReach) {
				reachable = Reachable.TRUE ;
			} else {
				reachable = Reachable.FALSE ;
			}
		}
		return hasBeenReach ;
	}

	public Reachable getReachable() {
		return reachable;
	}
	
}
