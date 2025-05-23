/*
 * MIT License

Copyright (c) 2017, 2025 Frederic Lefevre

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

import java.awt.EventQueue;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.fl.hostFileUpdater.Control;
import org.fl.hostFileUpdater.HostFileUpdater;
import org.fl.util.RunningContext;
import org.fl.util.swing.ApplicationTabbedPane;

public class HostFileUpdaterGui extends JFrame {
	
	private static final long serialVersionUID = 1384102217727660509L;

	private static final Logger logger = Logger.getLogger(HostFileUpdaterGui.class.getName());
	
	private static final String DEFAULT_PROP_FILE = "hostFileUpdater.properties";
	
	public static void main(String[] args) {
		
		Control.init(DEFAULT_PROP_FILE);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HostFileUpdaterGui window = new HostFileUpdaterGui();
					window.setVisible(true);
				} catch (Exception e) {
					logger.log(Level.SEVERE, "Exception in main", e);
				}
			}
		});
	}

	public static String getPropertyFile() {
		return DEFAULT_PROP_FILE;
	}
	
	private HostFileUpdaterGui() throws URISyntaxException {

		RunningContext context = Control.getRunningContext();

		HostFileUpdater hfu = new HostFileUpdater(context.getProps());

		// init main window
		setBounds(50, 50, 1500, 1000);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Host File Updater");

		ApplicationTabbedPane hfTabs = new ApplicationTabbedPane(context);

		ParseHostFilePane parsePanel = new ParseHostFilePane(hfu);
		ComposeHostFilePane composePanel = new ComposeHostFilePane(hfu);

		hfTabs.add(parsePanel, "Analyse host file", 0);
		hfTabs.add(composePanel, "Compose host file", 1);

		hfTabs.setSelectedIndex(0);

		getContentPane().add(hfTabs);
	}
}
