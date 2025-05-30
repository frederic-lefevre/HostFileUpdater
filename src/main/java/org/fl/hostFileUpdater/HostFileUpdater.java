/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

package org.fl.hostFileUpdater ;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.hostFileUpdater.hostFile.HostFile;
import org.fl.hostFileUpdater.hostFile.LocalAddressesHostFile;
import org.fl.util.AdvancedProperties;
import org.fl.util.FileSet;
import org.fl.util.file.FilesUtils;

public class HostFileUpdater {

	private static final Logger log = Logger.getLogger(HostFileUpdater.class.getName());
	
	private static final String NEWLINE = System.getProperty("line.separator");
	private static final String NEWLINE_HTML = "<br/>"; 
	
	// system host file to update
	private final HostFile targetHostFile;

	// base host file that must be present
	private final HostFile baseHostFile;

	// comment at the begining of the host file
	private final HostFile hostFileCommentHeader;

	// result host file
	private HostFile resultHostFile;

	// list of host file to choose from
	private final List<HostFile> hostFileList;

	// list of chosen host file
	private List<HostFile> chosenHostFileList;

	// File path where to save the old host file
	private final Path backupHostFile;

	// list of target host file file that are not present in the base host file or
	// in any of the host file to choose
	// So if the target host file is updated, they will be lost
	private final List<HostFileStatement> hostFileStatementsToBeLost;

	private final HostFile localHostMappings;

	public HostFileUpdater(AdvancedProperties props) throws URISyntaxException {

		String hostFileStyle = props.getProperty("hostFileUpdate.cssFilePath");
		HostFile.setCssStyleDefinition(hostFileStyle);

		// Get the target host file and the host file base
		Path pComment = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileCommentHeader"));
		Path pBase = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileBase"));
		Path pTarget = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileTarget"));
		backupHostFile = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.backupHosts"));

		hostFileCommentHeader = new HostFile(pComment);
		baseHostFile = new HostFile(pBase);
		targetHostFile = new HostFile(pTarget);

		// Get the list of host file parts
		Path hfPartsDir = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileDir"));
		FileSet hfPartsSet = new FileSet(hfPartsDir, log);
		List<Path> hostFilePartsPaths = hfPartsSet.getFileList();
		hostFileList = new ArrayList<HostFile>();
		chosenHostFileList = new ArrayList<HostFile>();

		// Build the local host mappings
		String[] additionnalHostNames = props.getArrayOfString("hostFileUpdate.localHostNames", ";");
		localHostMappings = new LocalAddressesHostFile(additionnalHostNames);

		// Build the totalHostFile to find the statements that will be lost if the host
		// file is saved
		HostFile totalHostFile = new HostFile(pBase);
		totalHostFile.append(localHostMappings);
		for (Path hPartPath : hostFilePartsPaths) {
			HostFile hf = new HostFile(hPartPath);
			hostFileList.add(hf);
			totalHostFile.append(hf);
		}

		// Host file statement to be lost if the host file is updated
		hostFileStatementsToBeLost = totalHostFile.getNotIncludedStatements(targetHostFile);
	}
	
	// Get all the possible host file parts to choose from
	public HostFile[] getHostFilesArray() {
		if (hostFileList != null) {
			return hostFileList.toArray(new HostFile[hostFileList.size()]);
		} else {
			return null;
		}
	}

	// add a list of host files to the chosen host files
	public void addChosenHostFiles(List<HostFile> choosenHostFile) {
		chosenHostFileList.addAll(choosenHostFile);
	}

	// Reset the list of chosen files
	public void resetChosenHostFile() {
		chosenHostFileList = new ArrayList<HostFile>();
	}
	
	// Build the result host file and return it as a string in HTML format, with
	// conflicts highlighted
	public String buildResultHostFile() {

		// 1) Generate the host file result
		generateResultHostFile();

		// 2) Print (HTML format) the host file result (rebuild it, do not use the host
		// file generated in step 1,
		// because we do not want to highlight conflicts in the local mappings ("normal
		// conflict"))
		StringBuilder resultHostFile = new StringBuilder();

		// print the header comment and the local mappings without highlighting conflict
		resultHostFile.append(HostFile.getHtmlBegin());
		resultHostFile.append(hostFileCommentHeader.getHtmlBody(false)).append(NEWLINE_HTML).append(NEWLINE);
		resultHostFile.append(localHostMappings.getHtmlBody(false)).append(NEWLINE);

		// generate a temporary host file for the remaining : base host file + choosen
		// host files
		HostFile bodyHostFile = new HostFile();
		bodyHostFile.append(baseHostFile);
		for (HostFile hf : chosenHostFileList) {
			bodyHostFile.append(hf);
		}
		bodyHostFile.testReachableHosts();

		// print this temporary host file with highlight for conflicts
		resultHostFile.append(bodyHostFile.getHtmlBody(true));

		// html end tags
		resultHostFile.append(HostFile.getHtmlEnd());

		return resultHostFile.toString();
	}
	
	// Return the base host file as a string
	public String printBaseHostFile() {
		return baseHostFile.getContent().toString();
	}

	// Return the host file header comments as a string
	public String getHostFileCommentHeader() {
		return hostFileCommentHeader.getContent().toString();
	}

	// Return the local address mappings as a string
	public String getLocalHostMappings() {
		return localHostMappings.getContent().toString();
	}

	// Get the target host file (normally /etc/hosts in linux or
	// C:\windows\system32\drivers\etc\hosts in windows)
	public HostFile getTargetHostFile() {
		return targetHostFile;
	}

	// Save result host file to target host file (normally /etc/hosts in linux or
	// C:\windows\system32\drivers\etc\hosts in windows)
	// (the current hosts file will be backed up with ".old" extension)
	public boolean saveResultHostFile() {

		// Back up target
		Path hostFilePath = targetHostFile.getFilePath();
		boolean res = true;
		try {
			Files.copy(hostFilePath, backupHostFile, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e1) {
			log.log(Level.SEVERE, "IOException saving old target host file " + targetHostFile.getFilePath(), e1);
		}

		// Save new host file to target
		try {
			Files.write(targetHostFile.getFilePath(), resultHostFile.getContent().toString().getBytes());
		} catch (IOException e) {
			log.log(Level.SEVERE, "IOException writing to target host file " + targetHostFile.getFilePath(), e);
			res = false;
		} catch (Exception e) {
			log.log(Level.SEVERE, "Exception writing to target host file " + targetHostFile.getFilePath(), e);
			res = false;
		}
		return res;
	}

	// Generate the result host file
	private HostFile generateResultHostFile() {

		resultHostFile = new HostFile();
		resultHostFile.append(hostFileCommentHeader);
		resultHostFile.append(localHostMappings);
		resultHostFile.append(baseHostFile);
		for (HostFile hf : chosenHostFileList) {
			resultHostFile.append(hf);
		}
		return resultHostFile;
	}

	// Print the current host file statements that will be lost, whatever host file parts are chosen
	// These are the statements that appears in the target host file but are not present in the base host file or any of the host file part to choose from
	public String printHostFileStatementsToBeLost() {
		
		StringBuilder result = new StringBuilder();
		result.append("Host file statements that are present in the host file and will be lost, whatever your choices: ").append(NEWLINE).append(NEWLINE);
		
		if (hostFileStatementsToBeLost.isEmpty()) {
			result.append("None");
		} else {
			for (HostFileStatement hfs : hostFileStatementsToBeLost) {
				result.append(hfs.getLine()).append(NEWLINE);
			}
		}
		return result.toString();
	}
	
	public String parseHostFile() {

		StringBuilder buff = new StringBuilder();

		buff.append("The present host file ");
		if (targetHostFile.includes(localHostMappings)) {
			buff.append("includes ");
		} else {
			buff.append("does not includes ");
		}
		buff.append("current local addresses mappings\n");

		buff.append("The present host file ");
		if (targetHostFile.includes(baseHostFile)) {
			buff.append("includes ");
		} else {
			buff.append("do not includes ");
		}
		buff.append("base mappings\n\nParticular mappings included:\n");
		List<HostFile> includedHostFile = targetHostFile.getIncludedHostFiles(hostFileList);
		for (HostFile ihf : includedHostFile) {
			buff.append(ihf.toString()).append("\n");
		}

		buff.append("\n").append(printHostFileStatementsToBeLost());

		return buff.toString();
	}

	public String getPresentHostFile() {
		return targetHostFile.getContent().toString();
	}

}
