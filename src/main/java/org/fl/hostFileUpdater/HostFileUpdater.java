package org.fl.hostFileUpdater ;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.hostFileUpdater.hostFile.HostFile;
import org.fl.hostFileUpdater.hostFile.LocalAddressesHostFile;

import com.ibm.lge.fl.util.AdvancedProperties;
import com.ibm.lge.fl.util.FileSet;

public class HostFileUpdater {

	private final static String NEWLINE 	 = System.getProperty("line.separator");
	private final static String NEWLINE_HTML = "<br/>" ; 
	
	// system host file to update
	private final HostFile targetHostFile ;
	
	// base host file that must be present
	private final HostFile baseHostFile ;
	
	// comment at the begining of the host file
	private final HostFile hostFileCommentHeader ;
	
	// result host file
	private HostFile resultHostFile ;
	
	// list of host file to choose from
	private final List<HostFile> hostFileList ;
	
	// list of chosen host file
	private List<HostFile> chosenHostFileList ;
	
	// File path where to save the old host file
	private final Path backupHostFile ;
	
	// list of target host file file that are not present in the base host file or in any of the host file to choose
	// So if the target host file is updated, they will be lost
	private List<HostFileStatement> hostFileStatementsToBeLost ;
	
	private HostFile 	 localHostMappings ;
	private Logger 		 hLog ;
	
	public HostFileUpdater(AdvancedProperties props, Logger l) {
		
		hLog = l ;
		String hostFileStyle = props.getProperty("hostFileUpdate.cssFilePath") ;
		HostFile.setCssStyleDefinition(hostFileStyle, hLog);
		
		// Get the target host file and the host file base
		Path pComment  = props.getPathFromURI("hostFileUpdate.hostFileCommentHeader"  ) ;
		Path pBase	   = props.getPathFromURI("hostFileUpdate.hostFileBase"  ) ;
		Path pTarget   = props.getPathFromURI("hostFileUpdate.hostFileTarget") ;
		backupHostFile = props.getPathFromURI("hostFileUpdate.backupHosts") ;
		
		hostFileCommentHeader = new HostFile(pComment, hLog) ;
		baseHostFile   		  = new HostFile(pBase,    hLog) ;
		targetHostFile 		  = new HostFile(pTarget,  hLog) ;
		
		// Get the list of host file parts
		String hfPartsDir = props.getProperty("hostFileUpdate.hostFileDir") ;
		FileSet hfPartsSet = new FileSet(hfPartsDir, hLog) ;
		ArrayList<Path> hostFilePartsPaths = hfPartsSet.getFileList() ;
		hostFileList = new ArrayList<HostFile>() ;
		chosenHostFileList = new ArrayList<HostFile>() ;
		
		// Build the local host mappings
		String[] additionnalHostNames = props.getArrayOfString("hostFileUpdate.localHostNames", ";") ;
		localHostMappings = new LocalAddressesHostFile(additionnalHostNames, hLog) ;
		
		// Build the totalHostFile to find the statements that will be lost if the host file is saved
		HostFile totalHostFile = new HostFile(pBase,    hLog) ;
		totalHostFile.append(localHostMappings) ;
		for (Path hPartPath : hostFilePartsPaths) {
			HostFile hf = new HostFile(hPartPath, hLog) ;
			hostFileList.add(hf) ;
			totalHostFile.append(hf) ;
		}
		
		// Host file statement to be lost if the host file is updated
		hostFileStatementsToBeLost = totalHostFile.getNotIncludedStatements(targetHostFile) ;
	}
	
	// Get all the possible host file parts to choose from
	public HostFile[] getHostFilesArray() {
		if (hostFileList != null) {
			return hostFileList.toArray(new HostFile[hostFileList.size()]) ;
		} else {
			return null ;
		}
	}
	
	// add a list of host files to the chosen host files
	public void addChosenHostFiles(List<HostFile> choosenHostFile) {
		chosenHostFileList.addAll(choosenHostFile) ;
	}

	// Reset the list of chosen files
	public void resetChosenHostFile() {
		chosenHostFileList = new ArrayList<HostFile>() ;
	}
	
	// Build the result host file and return it as a string in HTML format, with conflicts highlighted
	public String buildResultHostFile(boolean testReachable) {
		
		// 1) Generate the host file result
		generateResultHostFile() ;
		
		
		// 2) Print (HTML format) the host file result (rebuild it, do not use the host file generated in step 1,
		//    because we do not want to highlight conflicts in the local mappings ("normal conflict"))
		StringBuilder resultHostFile = new StringBuilder() ;
		
		// print the header comment and the local mappings without highlighting conflict
		resultHostFile.append(HostFile.getHtmlBegin()) ;
		resultHostFile.append(hostFileCommentHeader.getHtmlBody(false)).append(NEWLINE_HTML).append(NEWLINE) ;
		resultHostFile.append(localHostMappings.getHtmlBody(false)).append(NEWLINE) ;

		// generate a temporary host file for the remaining : base host file + choosen host files
		HostFile bodyHostFile = new HostFile(hLog) ;
		bodyHostFile.append(baseHostFile) ;
		for (HostFile hf : chosenHostFileList) {
			bodyHostFile.append(hf) ;
		}
		if (testReachable) {
			bodyHostFile.testReachableHosts() ; 
		}
		// print this temporary host file with highlight for conflicts
		resultHostFile.append(bodyHostFile.getHtmlBody(true)) ;
		
		// html end tags
		resultHostFile.append(HostFile.getHtmlEnd()) ;
		
		return resultHostFile.toString() ;
	}
	
	// Return the base host file as a string
	public String printBaseHostFile() {
		return baseHostFile.getContent().toString() ;
	}

	// Return the host file header comments as a string
	public String getHostFileCommentHeader() {
		return hostFileCommentHeader.getContent().toString();
	}

	// Return the local address mappings as a string
	public String getLocalHostMappings() {
		return localHostMappings.getContent().toString();
	}
	
	// Get the target host file (normally /etc/hosts in linux or C:\windows\system32\drivers\etc\hosts in windows)
	public HostFile getTargetHostFile() {
		return targetHostFile;
	}

	// Save result host file to target host file (normally /etc/hosts in linux or C:\windows\system32\drivers\etc\hosts in windows)
	// (the current hosts file will be backed up with ".old" extension)
	public boolean saveResultHostFile() {
		
		// Back up target
		Path hostFilePath = targetHostFile.getFilePath() ;
		boolean res = true ;
		try {
			Files.copy(hostFilePath, backupHostFile,  StandardCopyOption.REPLACE_EXISTING) ;
		} catch (IOException e1) {
			hLog.log(Level.SEVERE, "IOException saving old target host file " + targetHostFile.getFilePath(), e1);
		}
		
		// Save new host file to target
		try {
			Files.write(targetHostFile.getFilePath(), resultHostFile.getContent().toString().getBytes()) ;
		} catch (IOException e) {
			hLog.log(Level.SEVERE, "IOException writing to target host file " + targetHostFile.getFilePath(), e);
			res = false ;
		} catch (Exception e) {
			hLog.log(Level.SEVERE, "Exception writing to target host file " + targetHostFile.getFilePath(), e);
			res = false ;
		}
		return res ;
	}
	
	// Generate the result host file
	private HostFile generateResultHostFile() {
		
		resultHostFile = new HostFile(hLog) ;
		resultHostFile.append(hostFileCommentHeader) ;
		resultHostFile.append(localHostMappings) ;
		resultHostFile.append(baseHostFile) ;
		for (HostFile hf : chosenHostFileList) {
			resultHostFile.append(hf) ;
		}
		return resultHostFile ;
	}

	// Print the current host file statements that will be lost, whatever host file parts are chosen
	// These are the statements that appears in the target host file but are not present in the base host file or any of the host file part to choose from
	public String printHostFileStatementsToBeLost() {
		
		StringBuilder result = new StringBuilder() ;
		result.append("Host file statements that are present in the host file and will be lost, whatever your choices: ").append(NEWLINE).append(NEWLINE) ;
		
		if (hostFileStatementsToBeLost.isEmpty()) {
			result.append("None") ;
		} else {
			for (HostFileStatement hfs : hostFileStatementsToBeLost) {
				result.append(hfs.getLine()).append(NEWLINE) ;
			}
		}
		return result.toString();
	}
	
	public String parseHostFile() {

		StringBuilder buff = new StringBuilder() ;
		
		buff.append("The present host file ") ;
		if (targetHostFile.includes(localHostMappings)) {
			buff.append("includes ") ;
		} else {
			buff.append("does not includes ") ;
		}
		buff.append("current local addresses mappings\n") ;
		
		buff.append("The present host file ") ;
		if (targetHostFile.includes(baseHostFile)) {
			buff.append("includes ") ;
		} else {
			buff.append("do not includes ") ;
		}
		buff.append("base mappings\n\nParticular mappings included:\n") ;
		List<HostFile> includedHostFile = targetHostFile.getIncludedHostFiles(hostFileList) ;		 
		for (HostFile ihf : includedHostFile) {
			buff.append(ihf.toString()).append("\n") ;
		}
		
		buff.append("\n").append(printHostFileStatementsToBeLost()) ;
		
		return buff.toString() ;
		
	}
	
	public String getPresentHostFile() {
		return targetHostFile.getContent().toString() ;
	}
}
