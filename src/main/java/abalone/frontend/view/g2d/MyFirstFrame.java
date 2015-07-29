package abalone.frontend.view.g2d;


import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class MyFirstFrame extends Frame implements WindowListener {
	
	/**
	 * Needed for serialisation as Frame is serialisable - auto generated as version 1
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new MyFirstFrame();
	}

	// constructor
	public MyFirstFrame(){
		this.addWindowListener(this);
		this.add(new MySecondCanvas());
		this.setSize(600,500);
		this.setVisible(true);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
	}

}
