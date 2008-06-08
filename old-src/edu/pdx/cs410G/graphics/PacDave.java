package edu.pdx.cs410G.graphics;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Demonstrates animation in Java by drawing a simple (and kind of
 * dumb) version of PAC-MAN.  Does anybody else have "Panama" by Van
 * Halen going through their head right now?
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Fall 2003
 *
 * @see Timer
 */
public class PacDave extends JPanel implements ActionListener {

  /** The amount (in degrees) to open/close <code>PAC-DAVE</code>
   * mouth in each frame */
  private static final int INCREMENT = 5;

  /** <code>PAC-DAVE</code>'s radius */
  private static final int RADIUS = 20;

  /** The rate at which frames are drawn (frames per second) */
  private static final double RATE = 20;

  /** The north direction */
  private static final int NORTH = 0;

  /** The south direction */
  private static final int SOUTH = 1;

  /** The east direction */
  private static final int EAST = 2;

  /** The west direction */
  private static final int WEST = 3;

  /////////////////////  Instance Fields  /////////////////////

  /** (Half) the angle at which <code>PAC-DAVE</code>'s mouth is open */
  private int angle;

  /** Is <code>PAC-DAVE</code>'s mouth opening? */
  private boolean opening;

  /** A <code>Timer</code> used to update the animation */
  private Timer timer;

  /** <code>PAC-DAVE</code>'s x coordinate */
  private int x;

  /** <code>PAC-DAVE</code>'s y coordinate */
  private int y;

  /** The direction <code>PAC-DAVE</code> is travelling */
  private int direction;

  /** The initial angle of <code>PAC-DAVE</code>'s mouth */
  private int initAngle;

  //////////////////////  Constructors  //////////////////////

  /**
   * Creates the board and initializes the position and direction of
   * <code>PAC-DAVE</code>. 
   */
  public PacDave() {
    this.setBackground(Color.BLACK);
    this.setForeground(Color.YELLOW);
    this.setPreferredSize(new Dimension(400, 200));

    this.setDirection(EAST);
    this.opening = true;

    int delay = (int) (1000.0 / RATE);
    this.timer = new Timer(delay, this);

    // Initialize PAC-DAVE's coordinates to be the center of the board
    this.addComponentListener(new ComponentAdapter() {
        private void resetPosition() {
          Dimension size = PacDave.this.getSize();
          PacDave.this.x = size.width / 2;
          PacDave.this.y = size.height / 2;
        }

        public void componentResized(ComponentEvent e) {
          resetPosition();
        }

        public void componentShown(ComponentEvent e) {
          resetPosition();
        }
      });

    // Let the arrow keys change PAC-DAVE's direction
    this.addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          switch (e.getKeyCode()) {
          case KeyEvent.VK_UP:
            PacDave.this.setDirection(NORTH);
            break;
          case KeyEvent.VK_DOWN:
            PacDave.this.setDirection(SOUTH);
            break;
          case KeyEvent.VK_RIGHT:
            PacDave.this.setDirection(EAST);
            break;
          case KeyEvent.VK_LEFT:
            PacDave.this.setDirection(WEST);
            break;
          }
        }
      });
  }

  /**
   * Starts the PAC-DAVE animation
   */
  synchronized void startAnimation() {
    if (!this.timer.isRunning()) {
      // Request that the JPanel have keyboard focus.  Otherwise, it
      // won't get KeyEvents
      this.requestFocus();
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

  /**
   * Sets a direction for <code>PAC-DAVE</code>
   */
  private void setDirection(int direction) {
    switch (direction) {
    case NORTH:
      this.initAngle = 90;
      break;
    case SOUTH:
      this.initAngle = 270;
      break;
    case EAST:
      this.initAngle = 0;
      break;
    default:
      assert(direction == WEST);
      this.initAngle = 180;
      break;
    }
    this.direction = direction;
  }

  /**
   * Invoked by the {@linkplain Timer timer} to update the animation's
   * state and schedule a {@link #repaint}.
   */
  public void actionPerformed(ActionEvent e) {
    if (opening) {
      this.angle += INCREMENT;

    } else {
      this.angle -= INCREMENT;
    }

    if (this.angle >= 45) {
      opening = false;

    } else if (this.angle <= 0) {
      opening = true;
    }

    switch (this.direction) {
    case NORTH:
      if (this.y - RADIUS > 0) {
        this.y--;
      }
      break;
    case SOUTH:
      if (this.y + RADIUS < this.getHeight()) {
        this.y++;
      }
      break;
    case EAST:
      if (this.x + RADIUS < this.getWidth()) {
        this.x++;
      }
      break;
    case WEST:
      if (this.x - RADIUS > 0) {
        this.x--;
      }
      break;
    }

    this.repaint();
  }

  /**
   * Draws <code>PAC-DAVE</code> on the board
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(this.getBackground());
    g.fillRect(this.x - RADIUS, this.y - RADIUS, RADIUS * 2,
               RADIUS * 2);

    g.setColor(this.getForeground());
    g.fillOval(this.x - RADIUS, this.y - RADIUS, RADIUS * 2,
               RADIUS * 2);

    g.setColor(this.getBackground());
    g.fillArc(this.x - RADIUS, this.y - RADIUS, RADIUS * 2,
              RADIUS * 2, this.initAngle + this.angle,
              -2 * this.angle);
  }

  //////////////////////  Main Program  //////////////////////

  /**
   * Creates a new <code>PacDave</code> and positions it on the
   * screen. 
   */
  public static void main(String[] args) {
    final PacDave dave = new PacDave();
    
    JPanel board = new JPanel();
    board.setLayout(new BorderLayout());
    board.add(dave, BorderLayout.CENTER);
    
    JButton start = new JButton("Start");
    start.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dave.startAnimation();
        }
      });

    JButton stop = new JButton("Stop");
    stop.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dave.stopAnimation();
        }
      });

    JPanel buttons = new JPanel();
    buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
    buttons.add(start);
    buttons.add(Box.createHorizontalGlue());
    buttons.add(stop);
    board.add(buttons, BorderLayout.SOUTH);

    JFrame frame = new JFrame("PAC-DAVE");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.getContentPane().add(board);
    frame.pack();

    Toolkit tk = frame.getToolkit();
    Dimension frameSize = frame.getSize();
    Dimension screenSize = tk.getScreenSize();
    int x = (frameSize.width / 2) + (screenSize.width / 2);
    int y = (frameSize.height / 2) + (screenSize.height / 2);
    frame.setLocation(x, y);

    frame.setVisible(true);
  }

}
