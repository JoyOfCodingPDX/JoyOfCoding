package edu.pdx.cs410G.graphics;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.print.*;
import java.util.*;
import javax.swing.*;

/**
 * Demonstrates how Java's {@linkplain java.awt printing API} deal
 * with printing documents that span multiple pages.
 *
 * @author David Whitlock
 * @version $Revision: 1.1 $
 * @since Fall 2003
 */
public class StoryTime extends JFrame {

  /** Are we debuginng? */
  private static final boolean DEBUG =
    Boolean.getBoolean("StoryTime.DEBUG");

  /**
   * Creates and initializes a new <code>StoryTime</code>
   */
  public StoryTime() {
    super("Story Time");

    JButton print = new JButton("Print me a story");
    print.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          PrinterJob job = PrinterJob.getPrinterJob();
          PageFormat format = job.pageDialog(job.defaultPage());
          Book book = new Book();
          book.append(new Page("Once upon a time..."), format);
          book.append(new Page("The End."), format);

          if (DEBUG) {
            PrintPreview preview =
              new PrintPreview(book, StoryTime.this);
            preview.pack();
            preview.setVisible(true);

          } else {
            job.setPageable(book);
            if (job.printDialog()) {
              try {
                job.print();
                
              } catch (PrinterException ex) {
                ex.printStackTrace(System.err);
              }
            }
          }
        }
      });
    this.getContentPane().add(print, BorderLayout.CENTER);
  }

  //////////////////////  Main Program  //////////////////////

  /**
   * Displays a <code>StoryTime</code>
   */
  public static void main(String[] args) {
    JFrame frame = new StoryTime();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  //////////////////////  Inner Classes  //////////////////////

  /**
   * A page of a story that contains some text
   */
  public static class Page implements Printable {
    /** The text to be printed */
    private String text;

    /**
     * Creates a new <code>Page</code> that contains the given text
     */
    public Page(String text) {
      this.text = text;
    }

    public int print(Graphics g, PageFormat format, int pageIndex)
      throws PrinterException {

      System.out.println("Printing page " + pageIndex);

      Graphics2D g2 = (Graphics2D) g;

      double x = format.getImageableX();
      double y = format.getImageableY();
      double width = format.getImageableWidth();
      double height = format.getImageableHeight();

      Font font = g.getFont().deriveFont(24.0f);
      
      // Use Java2D's text layout tools because they know how to deal
      // with points (1/72nd of an inch)
      TextLayout layout =
        new TextLayout(this.text, font, g2.getFontRenderContext());
      Rectangle2D bounds = layout.getBounds();

      float x2 = (float) (x + width/2 - bounds.getWidth()/2.0);
      float y2 = (float) (y + height/2.0);

      System.out.println("Drawing at " + x2 + ", " + y2);

      layout.draw(g2, x2, y2);

      return Printable.PAGE_EXISTS;
    }
  }

  /**
   * Displays the contents of a <code>Book</code> in a dialog box.
   * This should save a few trees.
   */
  public static class PrintPreview extends JDialog {
    /** The book being previewed */
    private Book book;
    
    /** The page we've previewing */
    private int pageNumber;

    //////////////////////  Constructors  //////////////////////

    /**
     * Creates a new <code>PrintPreview</code> that previews the given
     * book.
     *
     * @param b
     *        The book to be previewed
     * @param owner
     *        The owner of this dialog box
     */
    public PrintPreview(Book b, Frame owner) {
      super(owner, "Print preview", true /* modal */);
      this.pageNumber = 0;
      this.book = b;
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

      this.getContentPane().setLayout(new BorderLayout());
      JPanel preview = new JPanel() {
          public void paintComponent(Graphics g) {
            super.paintComponent(g);

            Printable page = book.getPrintable(pageNumber);
            PageFormat format = book.getPageFormat(pageNumber);
            try {
              page.print(g, format, pageNumber);

            } catch (PrinterException ex) {
              ex.printStackTrace(System.err);
            }
          }
        };
      preview.setPreferredSize(new Dimension(400, 400));
      this.getContentPane().add(preview);

      JPanel p = new JPanel();
      p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
      JButton prev = new JButton("Previous page");
      prev.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (pageNumber > 0) {
              pageNumber--;
              PrintPreview.this.repaint();
            }
          }
        });
      p.add(prev);
      p.add(Box.createHorizontalGlue());

      JButton next = new JButton("Next page");
      next.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            if (pageNumber < book.getNumberOfPages()) {
              pageNumber++;
              PrintPreview.this.repaint();
            }
          }
        });
      p.add(next);
      this.getContentPane().add(p, BorderLayout.SOUTH);
    }
  }

}
