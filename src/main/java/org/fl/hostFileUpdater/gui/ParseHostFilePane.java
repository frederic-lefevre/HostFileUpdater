package org.fl.hostFileUpdater.gui;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fl.hostFileUpdater.HostFileUpdater;

public class ParseHostFilePane extends JPanel {

	private static final long serialVersionUID = 1L;

	public ParseHostFilePane(HostFileUpdater hfu) {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)) ;
		
		JTextArea parseInfo = new JTextArea(30, 80);
		parseInfo.append(hfu.parseHostFile());
		
		JLabel hfLabel = new JLabel("Present Host File content", JLabel.CENTER);
		JTextArea presentHostFile = new JTextArea(30, 80);
		presentHostFile.append(hfu.getPresentHostFile());
		JScrollPane parseScrollPane = new JScrollPane(presentHostFile); 
		
		add(parseInfo);
		add(hfLabel);
		add(parseScrollPane) ;
	}

	
}
