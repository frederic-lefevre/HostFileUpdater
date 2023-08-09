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
