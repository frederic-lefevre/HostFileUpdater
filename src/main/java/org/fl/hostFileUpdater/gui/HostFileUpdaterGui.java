package org.fl.hostFileUpdater.gui;

import java.awt.EventQueue;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.fl.hostFileUpdater.HostFileUpdater;

import com.ibm.lge.fl.util.RunningContext;
import com.ibm.lge.fl.util.swing.ApplicationTabbedPane;

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
	
	public HostFileUpdaterGui() {
		
		RunningContext runningContext = new RunningContext("HostFileUpdater", null, DEFAULT_PROP_FILE);
		Logger hLog = runningContext.getpLog() ;
		
		HostFileUpdater hfu = new HostFileUpdater(runningContext.getProps(), hLog) ;
		
   		// init main window
   		setBounds(50, 50, 1500, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Host File Updater") ;
		
		ApplicationTabbedPane hfTabs = new ApplicationTabbedPane(runningContext) ;
				
		ParseHostFilePane   parsePanel 	 = new ParseHostFilePane(hfu) ;
		ComposeHostFilePane composePanel = new ComposeHostFilePane(hfu, hLog) ;		
		
		hfTabs.add(parsePanel,  "Analyse host file", 0) ;
		hfTabs.add(composePanel,"Compose host file", 1) ;
		
		hfTabs.setSelectedIndex(0) ;
		
		getContentPane().add(hfTabs) ;		
	}
}
