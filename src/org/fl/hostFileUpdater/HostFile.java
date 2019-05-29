package org.fl.hostFileUpdater;

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

public class HostFile {

	private final static String NEWLINE 	  	 = System.getProperty("line.separator");
	private final static String ENDLINE_HTML  	 = "</span><br/>" ; 
	private final static String SPAN_COMMENT  	 = "<span class=\"comment\">" ;
	private final static String SPAN_CONFLICT 	 = "<span class=\"conflict\">" ;
	private final static String SPAN_UNREACHABLE = "<span class=\"broken\">" ;
	private final static String SPAN_NORMAL   	 = "<span class=\"normal\">" ;
	
	private final Path filePath ;
	private List<HostFileStatement> hostFileStatements ;
	private Logger hLog ;
	
	private static String htmlFileBegin =  "<html><body>" ;
	
	// Create an empty HostFile
	public HostFile(Logger l) {
		filePath = null ;
		hLog = l ;		
		hostFileStatements = new ArrayList<HostFileStatement>() ;
	}
	
	// Create a HostFile from a path (the corresponding file is read and parsed)
	public HostFile(Path pf, Logger l) {
		
		filePath = pf ;
		hLog = l ;
		
		hostFileStatements = new ArrayList<HostFileStatement>() ;
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
		
		filePath = null ;
		hLog = l ;
		hostFileStatements = new ArrayList<HostFileStatement>() ;
		addHostFileLines(fc);
	
	}
	
	// Add one line to the HostFile
	private void addOneLineToHostFile(String line) {
		
		HostFileStatement statement = new HostFileStatement(line) ;
		for (HostFileStatement hfs : hostFileStatements) {
			if (hfs.containSameHostNameWithDiffentAddress(statement)) {
				hfs.setHostDuplicate(true);
				statement.setHostDuplicate(true);
			}
		}
		hostFileStatements.add(statement) ;
	}
	
	// Add a list of lines the HostFile
	private void addHostFileLines(List<String> fileContent) {
		for (String line : fileContent) {
			addOneLineToHostFile(line) ;
		}
	}
	
	// Add a list of HostFileStatements to the HostFile
	private void addHostFileStatements(List<HostFileStatement> fileStatements) {
		for (HostFileStatement statement : fileStatements) {
			HostFileStatement newStatement = new HostFileStatement(statement) ;
			for (HostFileStatement hfs : hostFileStatements) {
				if (hfs.containSameHostNameWithDiffentAddress(newStatement)) {
					hfs.setHostDuplicate(true);
					newStatement.setHostDuplicate(true);
				}
			}
			hostFileStatements.add(newStatement) ;
		}
	}
	
	// File path to string
	public String getHostFileName() {
		if (filePath != null) {
			return filePath.getFileName().toString() ;
		} else {
			return "" ;
		}
	}
	
	// Get the content of the HostFile as a StringBuffer
	public StringBuilder getContent() {
		
		StringBuilder res = new StringBuilder() ;
		for (HostFileStatement statement : hostFileStatements) {
			res.append(statement.getLine()).append(NEWLINE) ;
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
		for (HostFileStatement statement : hostFileStatements) {
			String spanTag ;
			if (statement.isCommentLine()) {
				spanTag = SPAN_COMMENT ;
			} else if (showConflict && (statement.isHostDuplicate())) {
				spanTag = SPAN_CONFLICT ;
			} else if (!statement.isReachable()) {
				spanTag = SPAN_UNREACHABLE ;
			} else {
				spanTag = SPAN_NORMAL ;
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
		addOneLineToHostFile("");
		addHostFileStatements(hf.hostFileStatements);
		return this ;
	}
	
	// Get the host file statements of hf host file which are not included in this host file
	public ArrayList<HostFileStatement> getNotIncludedStatements(HostFile hf) {
		
		ArrayList<HostFileStatement> result = new ArrayList<HostFileStatement>() ;
		
		for (HostFileStatement hfs : hf.hostFileStatements) {
			IpAddressMap m = hfs.getIpAddressMap() ;
			if ((m != null) && (! includes(m))) {
				result.add(hfs) ;
			}
		}
		return result ;
	}
	
	// Returns true if the IP address map is part of this host file 
	public boolean includes(IpAddressMap ipam) {
		
		for (HostFileStatement hfs : hostFileStatements) {
			IpAddressMap m = hfs.getIpAddressMap() ;
			if ((m != null) && (m.isTheSameAs(ipam))) {
				return true ;
			}
		}
		return false ;
	}
	
	// Returns true if all the IP address maps of the host file in parameter are part of this host file 
	public boolean includes(HostFile hf) {
		
		boolean result = true ;
		for (HostFileStatement hfs : hf.getHostFileStatements()) {
			IpAddressMap m = hfs.getIpAddressMap() ;
			if ((m != null) && (! includes(m))) {
				result = false ;
			}
		}
		return result ;
	}
	
	// Return the list of host files (from the list of files in parameter) included in this host file 
	public ArrayList<HostFile> getIncludedHostFiles(ArrayList<HostFile> hostFiles) {
		
		ArrayList<HostFile> resultHostFiles = new ArrayList<HostFile>() ;
		
		for (HostFile hf : hostFiles) {
			if (this.includes(hf)) {
				resultHostFiles.add(hf) ;
			}
		}
		return resultHostFiles ;
	}

	public List<HostFileStatement> getHostFileStatements() {
		return hostFileStatements;
	}

}
