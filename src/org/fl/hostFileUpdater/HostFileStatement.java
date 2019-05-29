package org.fl.hostFileUpdater;

public class HostFileStatement {

	private final static String COMMENT_START = "#" ;
	
	private boolean isHostDuplicate ;
	private final boolean 	   isCommentLine ;
	private final String 	   line ;
	private final IpAddressMap ipAddressMap ;
	
	public HostFileStatement(String hostFileLine) {
		
		super();
		line = hostFileLine ;
		isHostDuplicate = false ;
		isCommentLine = checkCommentLine(line) ;
		if (isCommentLine) {
			ipAddressMap = null ;
		} else {
			ipAddressMap = new IpAddressMap(hostFileLine) ;
		}
	}

	public HostFileStatement(HostFileStatement hf) {
		
		super();
		line = hf.line ;
		isHostDuplicate = hf.isHostDuplicate ;
		isCommentLine = checkCommentLine(line) ;
		if (isCommentLine) {
			ipAddressMap = null ;
		} else {
			ipAddressMap = new IpAddressMap(line) ;
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

	public boolean isHostDuplicate() {
		return isHostDuplicate;
	}

	public void setHostDuplicate(boolean isHostDuplicate) {
		this.isHostDuplicate = isHostDuplicate;
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
	
	public boolean isReachable() {
		return ipAddressMap.isReachable() ;
	}
	
}
