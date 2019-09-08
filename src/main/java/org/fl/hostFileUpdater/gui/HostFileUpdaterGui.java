package org.fl.hostFileUpdater.gui;

import java.awt.EventQueue;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.fl.hostFileUpdater.HostFileUpdater;

import com.ibm.lge.fl.util.RunningContext;
import com.ibm.lge.fl.util.swing.ApplicationInfoPane;
import com.ibm.lge.fl.util.swing.LogsDisplayPane;

public class HostFileUpdaterGui   extends JFrame {

	private static final String DEFAULT_PROP_FILE = "hostFileUpdater.properties";
	
	private static final long serialVersionUID = 1384102217727660509L;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HostFileUpdaterGui window = new HostFileUpdaterGui();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private final HostFileUpdater 	hfu ;
	
	private final JTabbedPane hfTabs ;
	
	private final ParseHostFilePane   parsePanel ;
	private final ComposeHostFilePane composePanel ;		
	private final ApplicationInfoPane appInfoPane ;
	private final LogsDisplayPane 	  lPane ;

	public HostFileUpdaterGui() {
		
		RunningContext runningContext = new RunningContext("HostFileUpdater", null, DEFAULT_PROP_FILE);
		Logger hLog = runningContext.getpLog() ;
		
		hfu = new HostFileUpdater(runningContext.getProps(), hLog) ;
		
   		// init main window
   		setBounds(50, 50, 1500, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Host File Updater") ;
		
		hfTabs  = new JTabbedPane() ;
				
		parsePanel 	 = new ParseHostFilePane(hfu) ;
		composePanel = new ComposeHostFilePane(hfu, hLog) ;		
		appInfoPane  = new ApplicationInfoPane(runningContext) ;
		lPane 		 = new LogsDisplayPane(hLog) ;
		
		hfTabs.add("Analyse host file", 	  parsePanel ) ;
		hfTabs.add("Compose host file", 	  composePanel) ;
		hfTabs.add("Application information", appInfoPane) ;
		hfTabs.add("Logs display", 			  lPane		 ) ;
		
		hfTabs.addChangeListener(new AppTabChangeListener());
		
		getContentPane().add(hfTabs) ;		
	}


	private class AppTabChangeListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent arg0) {
			
			if (hfTabs.getSelectedComponent().equals(appInfoPane)) {
				appInfoPane.setInfos();
			}			
		}
	}
}
