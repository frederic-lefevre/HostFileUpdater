/*
 * MIT License

Copyright (c) 2017, 2026 Frederic Lefevre

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

import java.nio.file.Path;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fl.hostFileUpdater.gui.HostFileUpdaterGui;
import org.fl.hostFileUpdater.hostFile.HostFile;
import org.fl.util.AdvancedProperties;
import org.fl.util.RunningContext;
import org.fl.util.file.FilesUtils;

public class Control {
	
	private static final Logger logger = Logger.getLogger(Control.class.getName());
	
	private static Control instance;
	
	private Path backupHostFile;
	private Path pComment;
	private Path pBase;
	private Path pTarget;
	private Path hfPartsDir;
	private String[] additionnalHostNames;
	
	private static Control getInstance() {
		if (instance == null) {
			instance = new Control(HostFileUpdaterGui.getRunningContext());
		}
		return instance;
	}
	
	private Control() {
	}

	private Control(RunningContext runningContext) {
		
		try {
			AdvancedProperties props = runningContext.getProps();

			String hostFileStyle = props.getProperty("hostFileUpdate.cssFilePath");
			HostFile.setCssStyleDefinition(hostFileStyle);

			// Get the target host file and the host file base
			pComment = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileCommentHeader"));
			pBase = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileBase"));
			pTarget = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileTarget"));
			backupHostFile = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.backupHosts"));
			
			hfPartsDir = FilesUtils.uriStringToAbsolutePath(props.getProperty("hostFileUpdate.hostFileDir"));
			
			additionnalHostNames = props.getArrayOfString("hostFileUpdate.localHostNames", ";");
			
		} catch (Exception e) {
			logger.log(Level.SEVERE,  "Exception during inintialisation, property file="  + Objects.toString(runningContext.getPropertiesLocation()), e);
		}
	}

	public static Path getBackupHostFile() {
		return getInstance().backupHostFile;
	}
	
	public static Path getPComment() {
		return getInstance().pComment;
	}
	
	public static Path getPBase() {
		return getInstance().pBase;
	}
	
	public static Path getPTarget() {
		return getInstance().pTarget;
	}
	
	public static Path getHfPartsDir() {
		return getInstance().hfPartsDir;
	}
	
	public static String[] getAdditionnalHostNames() {
		return getInstance().additionnalHostNames;
	}
}
