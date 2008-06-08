package edu.pdx.cs410G.graphics;

import javax.swing.*;

/**
 * A Java applet that display text that wiggles around the screen.
 * This is all what people thought Java was good for in 1998.
 *
 * @author David Whitlock
 * @since Winter 2006
 */
public class WigglyTextApplet extends JApplet {

	/** The wiggly text */
	private WigglyText wiggly;

	////////////////////  Instance Methods  ////////////////////

	/**
	 * Initializes the state of this applet
	 */
	public void init() {
		String text = this.getParameter("text");
		if (text == null) {
			text = "Wiggly Text!";
		}

		this.wiggly = new WigglyText(text);
		this.wiggly.setBackground(java.awt.Color.WHITE);
	}

	/**
	 * Starts the wiggly text animation
	 */
	public void start() {
		this.wiggly.startAnimation();
	}

	/**
	 * Stops the wiggly text animation
	 */
	public void stop() {
		this.wiggly.stopAnimation();
	}

}