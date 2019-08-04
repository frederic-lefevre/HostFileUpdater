package org.fl.hostFileUpdater;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class IpAddressMap {

	public enum Reachable { UNKNOWN, TRUE, FALSE } ;
	
	private final String ipAddress ;
	private Set<String>  hostNames ;
	private Reachable 	 reachable ;
	
	// Mapping between an IP address and a list of host names
	// The host file line must not be a comment line, but may includes a comment
	public IpAddressMap(String hostFileLine) {
		
		reachable = Reachable.UNKNOWN ;
		
		String[] items = hostFileLine.split(" |\t") ;
		hostNames = new HashSet<String>() ;
		if ((items != null) && (items.length > 1)) {
			ipAddress = items[0].trim() ; 
				
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
		} else {
			ipAddress = null ;
		}
	}

	// Add a host name the the mapping
	public IpAddressMap addHostName(String hostName) {
		hostNames.add(hostName) ;
		return this ;
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

	public boolean testReachable() {
			
		boolean hasBeenReach = false ;
		InetAddress inet;
		try {
			inet = InetAddress.getByName(ipAddress);
	
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
		return hasBeenReach ;
	}

	public Reachable getReachable() {
		return reachable;
	}
	
}
