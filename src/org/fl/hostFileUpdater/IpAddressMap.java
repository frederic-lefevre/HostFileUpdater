package org.fl.hostFileUpdater;

import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IpAddressMap {

	private String ipAddress ;
	private Set<String> hostNames ;
	private boolean reachable ;
	
	// Mapping between an IP address and a list of host names
	// The host file line must not be a comment line, but may includes a comment
	public IpAddressMap(String hostFileLine) {
		
		String[] items = hostFileLine.split(" |\t") ;
		hostNames = new HashSet<String>() ;
		if ((items != null) && (items.length > 1)) {
			ipAddress = items[0].trim() ; 
		}
		
		boolean comment = false ;
		for (int i=1; (i < items.length) && (! comment); i++) {
			String host = items[i].trim() ;
			if (! host.startsWith("#")) {
				if (! host.isEmpty()) {
					hostNames.add(host.toLowerCase()) ;
				}
			} else {
				comment = true ;
			}
		}
		reachable = true ;
	}

	// Add a host name the the mapping
	public void addHostName(String hostName) {
		hostNames.add(hostName) ;
	}
	
	// True if the IP address maps have the same IP and the same set of names 
	public boolean isTheSameAs(IpAddressMap ipam) {
		return ( ipam.getIpAddress().equals(ipAddress) && ipam.getHostNames().equals(hostNames) ) ;
	}
	
	// True if the IP address maps have not the same IP but have the same set of names
	public boolean hasSameHostNamesWithDiffentAddress(IpAddressMap ipam) {
		return ( (! ipam.getIpAddress().equals(ipAddress)) && ipam.getHostNames().equals(hostNames) ) ;
	}
	
	// True if the IP address maps have not the same IP but have a name in common
	public boolean containSameHostNameWithDiffentAddress(IpAddressMap ipam) {
		return ( (! ipam.getIpAddress().equals(ipAddress)) && (! Collections.disjoint(ipam.getHostNames(), hostNames)) ) ; 
	}
	
	public String getIpAddress() {
		return ipAddress;
	}

	public Set<String> getHostNames() {
		return hostNames;
	}

	public boolean isReachable() {
		
		return reachable ;
	}
	
	public boolean testReachable() {
		
		InetAddress inet;
		try {
			inet = InetAddress.getByName(ipAddress);
	
			if (inet != null) {
				reachable = inet.isReachable(3000) ;
			} else {
				reachable = false ;
			}
		} catch (Exception e) {
			reachable = false ;
		}
		return reachable ;
	}
}
