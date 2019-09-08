package org.fl.hostFileUpdater.workers;

import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.SwingWorker;

import org.fl.hostFileUpdater.HostFileUpdater;

public class HostFileComposer extends SwingWorker<String,String> {

	private final HostFileUpdater hostFileUpdater ;
	private final JEditorPane 	  hostFilePane ;
	private final JButton		  writeHostFileButton ;
	private final Logger		  hLog ;
	
	public HostFileComposer(HostFileUpdater hfu, JEditorPane hfp, JButton jb, Logger l) {
		hLog				= l ;
		hostFileUpdater 	= hfu ;
		hostFilePane 		= hfp ;
		writeHostFileButton = jb ;
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
		} catch (InterruptedException | ExecutionException e) {
			hLog.log(Level.SEVERE, "Exception building result file", e) ;
		}
	}
}
