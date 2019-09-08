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

import org.fl.hostFileUpdater.HostFileUpdater;
import org.fl.hostFileUpdater.hostFile.HostFile;

public class HostFileComposer extends SwingWorker<String,String> {

	private final HostFileUpdater hostFileUpdater ;
	private final JEditorPane 	  hostFilePane ;
	private final JButton		  writeHostFileButton ;
	private final JTextArea		  infoArea ;
	private final JList<HostFile> hostFileGuiList ;
	private final Logger		  hLog ;
	
	public HostFileComposer(HostFileUpdater hfu, JEditorPane hfp, JButton jb, JTextArea ia, JList<HostFile> hfgl, Logger l) {
		hLog				= l ;
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
			hLog.log(Level.SEVERE, "Exception building result file", e) ;
		}
	}
}
