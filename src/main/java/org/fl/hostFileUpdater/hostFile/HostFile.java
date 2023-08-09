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

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.fl.hostFileUpdater.HostFileStatement;
import org.fl.hostFileUpdater.IpAddressMap;
import org.fl.hostFileUpdater.IpAddressMap.Reachable;

public class HostFile {

	private final static String NEWLINE 	  	 = System.getProperty("line.separator");
	private final static String ENDLINE_HTML  	 = "</span><br/>" ; 
	private final static String SPAN_COMMENT  	 = "<span class=\"comment\">" ;
	private final static String SPAN_CONFLICT 	 = "<span class=\"conflict\">" ;
	private final static String SPAN_UNREACHABLE = "<span class=\"broken\">" ;
	private final static String SPAN_NORMAL   	 = "<span class=\"normal\">" ;
	
	private class StatementOfThisFile {	
		
		public boolean 			 inConflict ;
		public HostFileStatement hostFileStatement ;
		public StatementOfThisFile(boolean inConflict, HostFileStatement hostFileStatement) {
			super();
			this.inConflict		   = inConflict;
			this.hostFileStatement = hostFileStatement;
		}
		public boolean inConflictWith(StatementOfThisFile anotherStatement) {
			return (this.hostFileStatement.containSameHostNameWithDiffentAddress(anotherStatement.hostFileStatement)) ;
		}
	}
	
	private List<StatementOfThisFile> StatementsOfThisFile ;

	private final Path filePath ;
	
	private final Logger hLog ;
	
	private static String htmlFileBegin =  "<html><body>" ;
	
	// Create an empty HostFile
	public HostFile(Logger l) {
		filePath 	   		 = null ;
		hLog 		   		 = l ;		
		StatementsOfThisFile = new ArrayList<StatementOfThisFile>() ;
	}
	
	// Create a HostFile from a path (the corresponding file is read and parsed)
	public HostFile(Path pf, Logger l) {
		
		filePath 	   		 = pf ;
		hLog 		   		 = l ;		
		StatementsOfThisFile = new ArrayList<StatementOfThisFile>() ;
		
		try {
			List<String> fileContent = Files.readAllLines(filePath, Charset.defaultCharset()) ;
			addHostFileLines(fileContent);
		} catch (IOException e) {
			hLog.log(Level.SEVERE, "IO Exception when reading file " + filePath, e);
		} catch (Exception e) {
			hLog.log(Level.SEVERE, "Exception when reading file " + filePath, e);
		}		
	}
	
	//  Create a HostFile from a list of String
	public HostFile(List<String> fc, Logger l) {
		
		filePath 	   		 = null ;
		hLog 		   		 = l ;		
		StatementsOfThisFile = new ArrayList<StatementOfThisFile>() ;
		addHostFileLines(fc);	
	}
	
	// Add a HostFileStatement to the HostFile
	private void addOneHostFileStatement(StatementOfThisFile newStatementOfThisFile) {
		for (StatementOfThisFile fileStatement : StatementsOfThisFile) {
			if (fileStatement.inConflictWith(newStatementOfThisFile)) {
				fileStatement.inConflict 		  = true ;
				newStatementOfThisFile.inConflict = true ;
			}
		}
		StatementsOfThisFile.add(newStatementOfThisFile) ;
	}
	
	// Add one line to the HostFile
	private void addOneLineToHostFile(String line) {	
		addOneHostFileStatement(new StatementOfThisFile(false, new HostFileStatement(line))) ;		
	}
	
	// Add a list of lines the HostFile
	protected void addHostFileLines(List<String> fileContent) {
		for (String line : fileContent) {
			try {
				addOneLineToHostFile(line) ;
			} catch (Exception e) {
				hLog.log(Level.SEVERE, "Exception when reading line \"" + line + "\"", e);
			}
		}
	}
	
	// Add a list of HostFileStatements to the HostFile
	private void addHostFileStatements(List<StatementOfThisFile> fileStatements) {
		for (StatementOfThisFile fileStatement : fileStatements) {
			addOneHostFileStatement(new StatementOfThisFile(fileStatement.inConflict, fileStatement.hostFileStatement)) ;
		}
	}
	
	// File path to string : must have a toString method to be able to be displayed in a JList
	@Override
	public String toString() {
		if (filePath != null) {
			return filePath.getFileName().toString() ;
		} else {
			return "" ;
		}
	}
	
	// Get the content of the HostFile as a StringBuffer
	public StringBuilder getContent() {
		
		StringBuilder res = new StringBuilder() ;
		for (StatementOfThisFile fileStatement : StatementsOfThisFile) {
			res.append(fileStatement.hostFileStatement.getLine()).append(NEWLINE) ;
		}
		return res ;
	}
	
	public Path getFilePath() {
		return filePath;
	}

	// Get the content of the HostFile as a StringBuffer, in HTML format, with or without highlighting conflicts
	public StringBuilder getHtmlContent(boolean showConflict) {
		
		StringBuilder res = new StringBuilder() ;
		res.append(getHtmlBegin()) ;
		res.append(getHtmlBody(showConflict)) ;
		res.append(getHtmlEnd()) ;
		return res ;
	}

	public static StringBuilder getHtmlBegin() {
		return new StringBuilder(htmlFileBegin) ;
	}
	
	// Get the content of the HostFile as a StringBuffer, in HTML format, with or without highlighting conflicts
	// the HTML format is returned without the "html", "body" ... etc tags so that it can be inserted inside a HTML page
	public StringBuilder getHtmlBody(boolean showConflict) {
		
		StringBuilder res = new StringBuilder() ;
		for (StatementOfThisFile fileStatement : StatementsOfThisFile) {
			String spanTag = SPAN_NORMAL ;
			HostFileStatement statement = fileStatement.hostFileStatement ;
			if (statement.isCommentLine()) {
				spanTag = SPAN_COMMENT ;
			} else if (showConflict && (fileStatement.inConflict)) {
				spanTag = SPAN_CONFLICT ;
			} else if (statement.getReachable().equals(Reachable.FALSE)) {
				spanTag = SPAN_UNREACHABLE ;
			}
			res.append(spanTag).append(statement.getLine()).append(ENDLINE_HTML) ;
		}
		return res ;
	}

	public static StringBuilder getHtmlEnd() {
		return new StringBuilder("</body></html>") ;
	}
	
	// Set the css style part of HTML print
	public static void setCssStyleDefinition(String cssStyleDefinitionFile, Logger l) {
		String cssStyleDefinition ;
		try {
			cssStyleDefinition =  new String(Files.readAllBytes(Paths.get(URI.create(cssStyleDefinitionFile)))) ;
		} catch (IOException e) {
			l.log(Level.SEVERE, "IO Exception when reading css file " + cssStyleDefinitionFile, e);
			cssStyleDefinition = "" ;
		} catch (Exception e) {
			l.log(Level.SEVERE, "Exception when reading css file " + cssStyleDefinitionFile, e);
			cssStyleDefinition = "" ;
		}	
		htmlFileBegin = "<html><head><style>" + cssStyleDefinition + "</style></head><body>" ;
	}
	
	// Append a HostFile to this HostFile
	public HostFile append(HostFile hf) {	
//		addOneLineToHostFile("");
		addHostFileStatements(hf.StatementsOfThisFile);
		return this ;
	}
	
	// Get the host file statements of hf host file which are not included in this host file
	public List<HostFileStatement> getNotIncludedStatements(HostFile hf) {
		
		List<HostFileStatement> result = new ArrayList<HostFileStatement>() ;
		
		for (StatementOfThisFile fileStatement : hf.StatementsOfThisFile) {
			IpAddressMap m = fileStatement.hostFileStatement.getIpAddressMap() ;
			if ((m != null) && (! includes(m))) {
				result.add(fileStatement.hostFileStatement) ;
			}
		}
		return result ;
	}
	
	// Returns true if the IP address map is part of this host file 
	private boolean includes(IpAddressMap ipam) {
		
		for (StatementOfThisFile fileStatement : StatementsOfThisFile) {
			IpAddressMap m = fileStatement.hostFileStatement.getIpAddressMap() ;
			if ((m != null) && (m.isTheSameAs(ipam))) {
				return true ;
			}
		}
		return false ;
	}
	
	// Returns true if all the IP address maps of the host file in parameter are part of this host file 
	public boolean includes(HostFile hf) {
		
		boolean result = true ;
		for (StatementOfThisFile fileStatement : hf.StatementsOfThisFile) {
			IpAddressMap m = fileStatement.hostFileStatement.getIpAddressMap() ;
			if ((m != null) && (! includes(m))) {
				result = false ;
			}
		}
		return result ;
	}
	
	// Return the list of host files (from the list of files in parameter) included in this host file 
	public List<HostFile> getIncludedHostFiles(List<HostFile> hostFiles) {
		return hostFiles.stream().filter(this::includes).collect(Collectors.toList()) ;
	}

	public void testReachableHosts() {
		StatementsOfThisFile.parallelStream().forEach(fileStatement -> fileStatement.hostFileStatement.testReachable());		
	}
}
