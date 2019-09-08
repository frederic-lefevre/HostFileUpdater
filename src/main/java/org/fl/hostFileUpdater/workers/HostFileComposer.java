package org.fl.hostFileUpdater.workers;

import java.awt.Color;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.fl.hostFileUpdater.HostFileUpdater;

public class HostFileComposer extends SwingWorker<String,String> {

	private final HostFileUpdater hostFileUpdater ;
	private final JEditorPane 	  hostFilePane ;
	private final JButton		  writeHostFileButton ;
	private final JTextArea		  infoArea ;
	private final Logger		  hLog ;
	
	public HostFileComposer(HostFileUpdater hfu, JEditorPane hfp, JButton jb, JTextArea ia, Logger l) {
		hLog				= l ;
		hostFileUpdater 	= hfu ;
		hostFilePane 		= hfp ;
		writeHostFileButton = jb ;
		infoArea			= ia ;
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
    		
			writeHostFileButton.setBackground(Color.ORANGE) ;
			writeHostFileButton.setText("Write result to Host File " + hostFileUpdater.getTargetHostFile().getFilePath());
			infoArea.append("\nResult host file display updated") ;
			
		} catch (InterruptedException | ExecutionException e) {
			hLog.log(Level.SEVERE, "Exception building result file", e) ;
		}
	}
}
