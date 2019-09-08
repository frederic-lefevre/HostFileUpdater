package org.fl.hostFileUpdater.gui;

import java.awt.EventQueue;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
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
	
	private HostFileUpdater 	hfu ;
	private ApplicationInfoPane appInfoPane ;
	private JTabbedPane 		hfTabs ;
	
	public HostFileUpdaterGui() {
		
		RunningContext runningContext = new RunningContext("HostFileUpdater", null, DEFAULT_PROP_FILE);
		Logger hLog = runningContext.getpLog() ;
		
		hfu = new HostFileUpdater(runningContext.getProps(), hLog) ;
		
   		// init main window
   		setBounds(50, 50, 1500, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Host File Updater") ;
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		
		hfTabs  = new JTabbedPane() ;
		
		JPanel parsePanel  = new JPanel () ;
		parsePanel.setLayout(new BoxLayout(parsePanel, BoxLayout.Y_AXIS)) ;
		
		ComposeHostFilePane composePanel = new ComposeHostFilePane(hfu) ;
		
		JTextArea parseInfo = new JTextArea(30, 80);
		parseInfo.append(hfu.parseHostFile());
		JLabel hfLabel = new JLabel("Present Host File content", JLabel.CENTER);
		JTextArea presentHostFile = new JTextArea(30, 80);
		presentHostFile.append(hfu.getPresentHostFile());
		JScrollPane parseScrollPane = new JScrollPane(presentHostFile); 
		parsePanel.add(parseInfo);
		parsePanel.add(hfLabel);
		parsePanel.add(parseScrollPane) ;
		
		appInfoPane = new ApplicationInfoPane(runningContext) ;
		
		// Tabbed Panel for logs display
		LogsDisplayPane lPane = new LogsDisplayPane(hLog) ;
		
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
