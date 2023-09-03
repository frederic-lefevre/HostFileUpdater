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

package org.fl.hostFileUpdater.workers;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.fl.hostFileUpdater.Control;
import org.fl.hostFileUpdater.HostFileUpdater;
import org.fl.hostFileUpdater.hostFile.HostFile;

public class HostFileComposer extends SwingWorker<String,String> {

	private static final Logger log = Control.getLogger();
	
	private final HostFileUpdater hostFileUpdater ;
	private final JEditorPane 	  hostFilePane ;
	private final JButton		  writeHostFileButton ;
	private final JTextArea		  infoArea ;
	private final JList<HostFile> hostFileGuiList ;
	
	public HostFileComposer(HostFileUpdater hfu, JEditorPane hfp, JButton jb, JTextArea ia, JList<HostFile> hfgl) {
		hostFileUpdater 	= hfu ;
		hostFilePane 		= hfp ;
		writeHostFileButton = jb ;
		infoArea			= ia ;
		hostFileGuiList		= hfgl ;
	}
	
	@Override
	protected String doInBackground() throws Exception {
		
		return hostFileUpdater.buildResultHostFile();
	}
	
	@Override 
	public void done() {
		try {
			hostFilePane.setText(get());
			writeHostFileButton.setEnabled(true) ;
			hostFileGuiList.setEnabled(true);
    		
			writeHostFileButton.setBackground(Color.ORANGE) ;
			writeHostFileButton.setText("Write result to Host File " + hostFileUpdater.getTargetHostFile().getFilePath());
			infoArea.append("\nResult host file display updated") ;
			
		} catch (InterruptedException | ExecutionException e) {
			log.log(Level.SEVERE, "Exception building result file", e) ;
		}
	}
}
