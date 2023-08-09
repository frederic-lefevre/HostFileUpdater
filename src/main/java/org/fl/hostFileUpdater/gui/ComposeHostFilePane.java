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

package org.fl.hostFileUpdater.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fl.hostFileUpdater.HostFileUpdater;
import org.fl.hostFileUpdater.hostFile.HostFile;
import org.fl.hostFileUpdater.workers.HostFileComposer;

public class ComposeHostFilePane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private final JList<HostFile> hostFileGuiList ;
	private final JTextArea 	  infoArea ;
	private final JButton 		  saveHostFile ;
	private final JEditorPane 	  resultFile ;	
	private final HostFileUpdater hfu ;
	private final Logger 		  hLog ;
	
	public ComposeHostFilePane(HostFileUpdater hfu, Logger l) {
		
		super();
		hLog = l ;
		this.hfu = hfu ;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)) ;
		
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
		saveHostFile.setEnabled(false);
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
		
		add(part1HostFilePanel) ;
		add(resultHostFilePanel) ;
		add(infoAndSavePanel) ;
		
		// Build and display the resulting host file
		hostFileGuiList.setEnabled(false);
		HostFileComposer hfc = new HostFileComposer(hfu, resultFile, saveHostFile, infoArea, hostFileGuiList, hLog) ;
		hfc.execute() ;	
	}

	// List selection listener : action when a host file part is selected
	class HostFileListSelectionHandler implements ListSelectionListener {
	    public void valueChanged(ListSelectionEvent e) {
	    	if (e.getValueIsAdjusting() == false) {
	    		
	    		infoArea.append("\nUpdating result host file content...") ;
	    		saveHostFile.setEnabled(false);
	    		hostFileGuiList.setEnabled(false);
	    		
	    		// Reset the list of chosen host file parts
	    		hfu.resetChosenHostFile();
	    		
	    		// Add the list of selected host file parts to the chosen host files
	    		hfu.addChosenHostFiles(hostFileGuiList.getSelectedValuesList());
	    		
	    		// Build and display the resulting host file
				HostFileComposer hfc = new HostFileComposer(hfu, resultFile, saveHostFile, infoArea, hostFileGuiList, hLog) ;
				hfc.execute() ;	

	    	}
	    }
	}
	
	// Action listener for the "save host file" button
	class WriteHostFile implements ActionListener {
		
		public void actionPerformed(ActionEvent ae) {
			
			if (ae.getSource() == saveHostFile) {
				saveHostFile.setEnabled(false);
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
				saveHostFile.setEnabled(true);
			}			
		}
	}
	
	// Action listener for the "show reachable hosts" button
	class ReachableHostFile implements ActionListener {
		
		public void actionPerformed(ActionEvent ae) {
				
			infoArea.append("\nUpdating reachable host...") ;
			hostFileGuiList.setEnabled(false);
			
			// Build and display the resulting host file
			HostFileComposer hfc = new HostFileComposer(hfu, resultFile, saveHostFile, infoArea, hostFileGuiList, hLog) ;
			hfc.execute() ;			
		}
	}
}
