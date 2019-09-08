package org.fl.hostFileUpdater.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fl.hostFileUpdater.HostFileUpdater;
import org.fl.hostFileUpdater.hostFile.HostFile;

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
	private JList<HostFile> 	hostFileGuiList ;
	private JEditorPane 		resultFile ;
	private JButton 			saveHostFile ;
	private JTextArea 			infoArea ;
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
		JPanel updatePanel = new JPanel () ;
		updatePanel.setLayout(new BoxLayout(updatePanel, BoxLayout.Y_AXIS)) ;
		parsePanel.setLayout(new BoxLayout(parsePanel, BoxLayout.Y_AXIS)) ;
		
		// --------------------
		// First part Host file
		JPanel part1HostFilePanel = new JPanel() ;
		part1HostFilePanel.setLayout(new BoxLayout(part1HostFilePanel, BoxLayout.X_AXIS));
		
		// Host file header
		JPanel hostFileHeaderPanel = new JPanel() ;
		hostFileHeaderPanel.setLayout(new BoxLayout(hostFileHeaderPanel, BoxLayout.Y_AXIS));
		JLabel hostFileCommentLabel = new JLabel("1) Host file header comment", JLabel.CENTER);
		JTextArea headerHostFileContent = new JTextArea(30, 80);
		headerHostFileContent.setEditable(false);
		JScrollPane scrollPane2 = new JScrollPane(headerHostFileContent) ;
		headerHostFileContent.setText(hfu.getHostFileCommentHeader());
		hostFileHeaderPanel.add(hostFileCommentLabel) ;
		hostFileHeaderPanel.add(scrollPane2) ;
		
		// Local addresses
		JPanel hostFileLocAddrPanel = new JPanel() ;
		hostFileLocAddrPanel.setLayout(new BoxLayout(hostFileLocAddrPanel, BoxLayout.Y_AXIS));
		JLabel hostFileLocAddrLabel = new JLabel("2) Add current local addresses", JLabel.CENTER);
		JTextArea localAdressesContent = new JTextArea(30, 80);
		localAdressesContent.setEditable(false);
		JScrollPane scrollPane3 = new JScrollPane(localAdressesContent) ;
		localAdressesContent.setText(hfu.getLocalHostMappings());
		hostFileLocAddrPanel.add(hostFileLocAddrLabel) ;
		hostFileLocAddrPanel.add(scrollPane3) ;
		
		// Base host file
		JPanel baseHostFilePanel = new JPanel() ;
		baseHostFilePanel.setLayout(new BoxLayout(baseHostFilePanel, BoxLayout.Y_AXIS));
		JLabel baseHostFileLabel = new JLabel("3) Add base host file", JLabel.CENTER);
		JTextArea baseHostFileContent = new JTextArea(30, 80);
		baseHostFileContent.setEditable(false);
		JScrollPane scrollPane1 = new JScrollPane(baseHostFileContent) ;
		baseHostFileContent.setText(hfu.printBaseHostFile());
		baseHostFilePanel.add(baseHostFileLabel) ;
		baseHostFilePanel.add(scrollPane1) ;
		
		part1HostFilePanel.add(hostFileHeaderPanel) ;
		part1HostFilePanel.add(hostFileLocAddrPanel) ;
		part1HostFilePanel.add(baseHostFilePanel) ;
		
		// ----------------------------
		// Second part Result Host file
		JPanel resultHostFilePanel = new JPanel() ;
		resultHostFilePanel.setLayout(new BoxLayout(resultHostFilePanel, BoxLayout.X_AXIS));
				
		// List of host file parts to choose
		JPanel listHostFilesPanel = new JPanel() ;
		listHostFilesPanel.setLayout(new BoxLayout(listHostFilesPanel, BoxLayout.Y_AXIS));
		JLabel hostFileListLabel = new JLabel("4) Choose Host file parts to add", JLabel.CENTER);
		hostFileGuiList = new JList<HostFile>(hfu.getHostFilesArray());
		JScrollPane listScroller = new JScrollPane(hostFileGuiList);
		listScroller.setPreferredSize(new Dimension(30, 30)) ;
		listHostFilesPanel.add(hostFileListLabel) ;
		listHostFilesPanel.add(listScroller) ;
		resultHostFilePanel.add(listHostFilesPanel) ;
		hostFileGuiList.addListSelectionListener(new HostFileListSelectionHandler());
		
		// Result host file
		JPanel resultHFPanel = new JPanel() ;
		resultHFPanel.setLayout(new BoxLayout(resultHFPanel, BoxLayout.Y_AXIS));
		JLabel hostFileResultLabel = new JLabel("Result Host file (conflicts are in red, unreachable hosts in orange)", JLabel.CENTER);
		resultFile = new JEditorPane();
		resultFile.setContentType("text/html");
		JScrollPane scrollPane = new JScrollPane(resultFile); 
		resultFile.setEditable(false);
		resultFile.setText(hfu.buildResultHostFile(true));
		resultHFPanel.add(hostFileResultLabel) ;
		resultHFPanel.add(scrollPane) ;
		resultHostFilePanel.add(resultHFPanel) ;
		
		// -----------------------------
		// Third part : information and  saving to result to system host file
		JPanel infoAndSavePanel = new JPanel() ;
		infoAndSavePanel.setLayout(new BoxLayout(infoAndSavePanel, BoxLayout.Y_AXIS));
		
		// information panel
		JPanel infoPanel = new JPanel() ;
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		JLabel infoLabel = new JLabel("Information", JLabel.CENTER);
		infoArea = new JTextArea(30, 80);
		infoArea.setEditable(false);
		JScrollPane scrollPane4 = new JScrollPane(infoArea) ;
		infoArea.setText(hfu.printHostFileStatementsToBeLost());
		infoPanel.add(infoLabel) ;
		infoPanel.add(scrollPane4) ;
		
		saveHostFile = new JButton("Write result to Host File " + hfu.getTargetHostFile().getFilePath()) ;
		Font font = new Font("Verdana", Font.BOLD, 18);
		saveHostFile.setFont(font) ;
		saveHostFile.setBackground(Color.ORANGE) ;
		saveHostFile.setPreferredSize(new Dimension(400,150)) ;
		WriteHostFile whf = new WriteHostFile() ;
		saveHostFile.addActionListener(whf);
		
		JButton testHosts= new JButton("Re-Test reachable hosts") ;
		ReachableHostFile rhf = new ReachableHostFile() ;
		testHosts.addActionListener(rhf) ;
		
		infoAndSavePanel.add(infoPanel) ;
		infoAndSavePanel.add(saveHostFile) ;
		infoAndSavePanel.add(testHosts) ;
		
		updatePanel.add(part1HostFilePanel) ;
		updatePanel.add(resultHostFilePanel) ;
		updatePanel.add(infoAndSavePanel) ;
		
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
		hfTabs.add("Compose host file", 	  updatePanel) ;
		hfTabs.add("Application information", appInfoPane) ;
		hfTabs.add("Logs display", 			  lPane		 ) ;
		
		hfTabs.addChangeListener(new AppTabChangeListener());
		
		getContentPane().add(hfTabs) ;		
	}

	// List selection listener : action when a host file part is selected
	class HostFileListSelectionHandler implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
	    	if (e.getValueIsAdjusting() == false) {
	    		
	    		// Reset the list of chosen host file parts
	    		hfu.resetChosenHostFile();
	    		
	    		// Add the list of selected host file parts to the chosen host files
	    		hfu.addChosenHostFiles(hostFileGuiList.getSelectedValuesList());
	    		
	    		// Build and display the resulting host file
	    		resultFile.setText(hfu.buildResultHostFile(true));
	    		
	    		saveHostFile.setBackground(Color.ORANGE) ;
				saveHostFile.setText("Write result to Host File " + hfu.getTargetHostFile().getFilePath());
				infoArea.append("\nResult host file changed; write to target to be done") ;
	    	}
	    }
	}
	
	// Action listener for the "save host file" button
	class WriteHostFile implements ActionListener {
		
		public void actionPerformed(ActionEvent ae) {
			
			if (ae.getSource() == saveHostFile) {
				boolean success = hfu.saveResultHostFile();
				String msg ;
				if (success) {
					saveHostFile.setBackground(Color.GREEN) ;
					msg = "Results written to Host File " + hfu.getTargetHostFile().getFilePath() ;			
					
				} else {
					saveHostFile.setBackground(Color.RED) ;
					msg = ("Write failure to Host File " + hfu.getTargetHostFile().getFilePath()) ;
				}
				saveHostFile.setText(msg) ;
				infoArea.append("\n" + msg);
			}			
		}
	}
	
	// Action listener for the "show reachable hosts" button
	class ReachableHostFile implements ActionListener {
		
		public void actionPerformed(ActionEvent ae) {
						
			// Build and display the resulting host file
	    	resultFile.setText(hfu.buildResultHostFile(true));			
		}
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
