package org.fl.hostFileUpdater;

import org.fl.hostFileUpdater.IpAddressMap.Reachable;

public class HostFileStatement {

	private final static String COMMENT_START = "#" ;
	
	private final boolean 	   isCommentLine ;
	private final String 	   line ;
	private final IpAddressMap ipAddressMap ;
	
	public HostFileStatement(String hostFileLine) {
		
		super();
		line = hostFileLine ;
		isCommentLine = checkCommentLine(line) ;
		if ((isCommentLine) || (line.isEmpty())) {
			ipAddressMap = null ;
		} else {
			ipAddressMap = new IpAddressMap(hostFileLine) ;
		}
	}

	private boolean checkCommentLine(String line) {
		
		String l = line.trim() ;
		if ((l != null) 	&& 
			(! l.startsWith(COMMENT_START))) {
				return false ;
		} else {
			return true ;
		}					
	}

	public boolean isCommentLine() {
		return isCommentLine;
	}

	public String getLine() {
		return line;
	}
	
	public IpAddressMap getIpAddressMap() {
		return ipAddressMap;
	}

	// True if the IP address maps have not the same IP but have a name in common
	public boolean containSameHostNameWithDiffentAddress(HostFileStatement hfs) {
		
		if ((ipAddressMap == null) || (hfs.getIpAddressMap() == null)) {
			return false ;
		} else {
			return ipAddressMap.containSameHostNameWithDiffentAddress(hfs.getIpAddressMap()) ;
		}
	}
	
	public void testReachable() {
		if (ipAddressMap != null) {
			ipAddressMap.testReachable() ;
		}
	}
	
	public Reachable getReachable() {
		if (ipAddressMap != null) {
			return ipAddressMap.getReachable() ;
		} else {
			return Reachable.UNKNOWN ;
		}
	}
}
