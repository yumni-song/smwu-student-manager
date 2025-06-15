package main;

import javax.swing.SwingUtilities; // ✅ SwingUtilities import
import javax.swing.JFrame; 

public class Main {
	public static void main(String[] args) {
	    SwingUtilities.invokeLater(() -> {
	        MainFrame frame = new  MainFrame();
	        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	        frame.setVisible(true);
	    });
	}

}
