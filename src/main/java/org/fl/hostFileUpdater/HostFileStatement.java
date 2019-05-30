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
		if (isCommentLine) {
			ipAddressMap = null ;
		} else {
			ipAddressMap = new IpAddressMap(hostFileLine) ;
		}
	}

	private boolean checkCommentLine(String line) {
		
		boolean res = true ;
		String l = line.trim() ;
		if ((l != null) && (! l.isEmpty())) {
			if (! l.startsWith(COMMENT_START)) {
				res = false ;
			}
		}
		return res ;				
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
		
		if (isCommentLine || hfs.isCommentLine) {
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
