package org.fl.hostFileUpdater.hostFile ;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.ibm.lge.fl.util.os.NetworkUtils;

public class LocalAddressesHostFile extends HostFile {

	private final static String LOCALHOST 	 = "localhost" ;
	private final static String LOCALIP 	 = "127.0.0.1" ;
	private final static char 	TABCHAR 	 = '\t' ;

	public LocalAddressesHostFile(String[] otherLocalHostNames, Logger l) {
		super(l);
		
		// Build the local host mappings
		// Get the current IP addresses and machine name for this host
		List<String> localHostMappingsString = new ArrayList<String>() ;
		localHostMappingsString.add("# Local addresses mappings") ;
		
		NetworkUtils nu = new NetworkUtils() ;
		List<String> hostCurrentIpAddresses = nu.getIPv4List() ;
		String machineName = nu.getMachineName() ;
		String[] additionnalHostNames = applyPattern(machineName, otherLocalHostNames) ;
		localHostMappingsString.add(LOCALIP + TABCHAR + LOCALHOST) ;
		localHostMappingsString.add(LOCALIP + TABCHAR + machineName) ;
		if ( (additionnalHostNames != null) && (additionnalHostNames.length > 0) ) {
			for (String host : additionnalHostNames) {
				localHostMappingsString.add(LOCALIP + TABCHAR + host) ;
			}
		}
		for (String ip : hostCurrentIpAddresses) {
			localHostMappingsString.add(ip + TABCHAR + LOCALHOST) ;
			localHostMappingsString.add(ip + TABCHAR + machineName) ;
			if ( (additionnalHostNames != null) && (additionnalHostNames.length > 0) ) {
				for (String host : additionnalHostNames) {
					localHostMappingsString.add(ip + TABCHAR + host) ;
				}
			}
		}
		addHostFileLines(localHostMappingsString) ;
	}

	private String[] applyPattern(String s, String[] as) {
		if ( (as != null) && (as.length > 0)) {
			for (int i=0; i < as.length; i++) {
				as[i] = as[i].replace("%m", s) ;
			}
		}
		return as ;
	}

}
