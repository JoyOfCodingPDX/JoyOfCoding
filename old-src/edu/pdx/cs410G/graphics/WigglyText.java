package edu.pdx.cs410G.graphics;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * An animation that draws wiggly text.
 *
 * @see WigglyTextApplet
 *
 * @author David Whitlock
 * @since Winter 2006
 */
public class WigglyText extends JPanel implements ActionListener {

  /** The rate at which frames are drawn (frames per second) */
  private static final double RATE = 4;

	/** A random number generator */
	private static final Random random = new Random();

	//////////////////////  Instance Fields  //////////////////////

  /** A <code>Timer</code> used to update the animation */
  private javax.swing.Timer timer;

	/** The <code>WigglyCharacter</code>s to be drawn */
	private ArrayList characters = new ArrayList();

	///////////////////////  Constructors  ///////////////////////

	/**
	 * Creates a new <code>WigglyTextApplet</code> with the given text
	 * to display.
	 */
	public WigglyText(String text) {
    int delay = (int) (1000.0 / RATE);
    this.timer = new javax.swing.Timer(delay, this);

		FontMetrics fm = this.getFontMetrics(this.getFont());

		int width = fm.stringWidth(text) + 10;
		int height = fm.getHeight() + 10;
		this.setPreferredSize(new Dimension(width, height));

		int y = (height / 2) - (fm.getHeight() / 2);
		int x = (width / 2) - (fm.stringWidth(text) / 2);
		this.characters.add(new WigglyCharacter(x, y, text.charAt(0)));
		
		for (int i = 1; i < text.length(); i++) {
			x += fm.charWidth(text.charAt(i));
			this.characters.add(new WigglyCharacter(x, y, text.charAt(i)));
		}

	}

	/////////////////////  Instance Methods  /////////////////////

	/**
	 * Draws the current frame of the animation
	 */
  public void actionPerformed(ActionEvent e) {
		for (Iterator iter = this.characters.iterator(); iter.hasNext(); ) {
			WigglyCharacter wc = (WigglyCharacter) iter.next();
			wc.move();
		}

    this.repaint();
	}

	/**
	 * Draws the wiggly text
	 */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

		for (Iterator iter = this.characters.iterator(); iter.hasNext(); ) {
			WigglyCharacter wc = (WigglyCharacter) iter.next();
			wc.draw(g);
		}
	}

  synchronized void startAnimation() {
    if (!this.timer.isRunning()) {
      this.timer.start();
    }
  }

  /**
   * Stops the PAC-DAVE animation
   */
  synchronized void stopAnimation() {
    if (this.timer.isRunning()) {
      this.timer.stop();
    }
  }

	//////////////////////  Inner Classes  //////////////////////

	/**
	 * A letter in a wiggly word
	 */
	private class WigglyCharacter {
		/** The character to be dispayed */
		private final String c;

		/** The current X coordinate of the character */
		private int x;

		/** The current Y coordinate of the character */
		private int y;

		//////////////////////  Constructors  //////////////////////

		/**
		 * Creates a new <code>WigglyText</code> for the given character
		 * with the given initial coordinates.
		 */
		WigglyCharacter(int x, int y, char c) {
			this.x = x;
			this.y = y;
			this.c = String.valueOf(c);
		}

		/////////////////////  Instance Methods  /////////////////////

		/**
		 * Draws this wiggly text to the given <code>Graphics</code>
		 */
		void draw(Graphics g) {
			g.drawString(this.c, this.x, this.y);
		}

		/**
		 * Moves this letter around by one pixel
		 */
		void move() {
			switch (random.nextInt(4)) {
			case 0:
				// Do nothing
				break;

			case 1:
				if (this.x >= WigglyText.this.getWidth()) {
					this.x--;

				} else {
					this.x++;
				}
				break;

			case 2:
				if (this.x <= 0) {
					this.x++;

				} else {
					this.x--;
				}
				break;

			case 3:
				if (this.y >= WigglyText.this.getHeight()) {
					this.y--;

				} else {
					this.y++;
				}
				break;

			case 4:
				if (this.y <= 0) {
					this.y++;

				} else {
					this.y--;
				}
				break;

			default:
				assert false;
			}
		}

		public String toString() {
			return this.c + "(" + this.x + ", " + this.y + ")";
		}

	}

	//////////////////////  Main Program  //////////////////////

	/**
	 * Tests wiggly text
	 */
  public static void main(String[] args) {
		String text = "This is Wiggly Text!";

		if (args.length > 0) {
			text = args[0];
		}

		final WigglyText wiggly = new WigglyText(text);
		wiggly.startAnimation();

		JFrame frame = new JFrame("Wiggly Text");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(wiggly);
		frame.pack();

    frame.setVisible(true);
	}
}