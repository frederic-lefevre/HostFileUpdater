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

package org.fl.hostFileUpdater;

import java.util.logging.Logger;

import org.fl.hostFileUpdater.IpAddressMap.Reachable;

public class HostFileStatement {
	
	private final static String COMMENT_START = "#" ;
	
	private final boolean 	   isCommentLine ;
	private final String 	   line ;
	private final IpAddressMap ipAddressMap ;
	
	public HostFileStatement(String hostFileLine, Logger log) {
		
		super();
		line = hostFileLine ;
		String trimLine = line.trim() ;
		isCommentLine = checkCommentLine(trimLine) ;
		if ((isCommentLine) || (trimLine.isEmpty())) {
			ipAddressMap = null ;
		} else {
			ipAddressMap = new IpAddressMap(trimLine) ;
		}
	}

	private boolean checkCommentLine(String l) {		
		return ((l == null) || (l.startsWith(COMMENT_START))) ;			
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
