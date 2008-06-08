package edu.pdx.cs410G.swing;

import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This program demonstrates the Swing {@link JSlider} widget by
 * computing the Heat Index based on a temperature and percent
 * humidity.
 */
public class HeatIndex extends JPanel {

  public HeatIndex() {
    this.setLayout(new BorderLayout());

    JPanel p = new JPanel();
    p.setLayout(new GridLayout(4, 1));

    JLabel tempLabel = new JLabel("Temperature", JLabel.CENTER);
    p.add(tempLabel);
    final JSlider temp = new JSlider(-20, 120, 65);
    temp.setMajorTickSpacing(20);
    temp.setMinorTickSpacing(5);
    temp.setPaintTicks(true);
    temp.setLabelTable(temp.createStandardLabels(20));
    temp.setPaintLabels(true);
    p.add(temp);

    JLabel humidityLabel = new JLabel("Percent Relative Humidity",
                                      JLabel.CENTER);
    p.add(humidityLabel);
    final JSlider humidity = new JSlider(0, 100, 50);
    humidity.setMajorTickSpacing(20);
    humidity.setMinorTickSpacing(5);
    humidity.setPaintTicks(true);
    humidity.setLabelTable(humidity.createStandardLabels(20));
    humidity.setPaintLabels(true);
    p.add(humidity);

    this.add(p, BorderLayout.WEST);
    
    final JSlider heatIndex = new JSlider(50, 150);
    heatIndex.setOrientation(JSlider.VERTICAL);
    heatIndex.setMajorTickSpacing(20);
    heatIndex.setMinorTickSpacing(5);
    heatIndex.setPaintTicks(true);
    Hashtable labels = new Hashtable();
    labels.put(new Integer(80), new JLabel("Caution"));
    labels.put(new Integer(91), new JLabel("Extreme Caution"));
    labels.put(new Integer(105), new JLabel("Danger"));
    labels.put(new Integer(129), new JLabel("Extreme Danger"));
    heatIndex.setLabelTable(labels);
    heatIndex.setPaintLabels(true);
    heatIndex.setValue((int) computeHeatIndex((double) temp.getValue(),
                                              (double) humidity.getValue()));
    this.add(heatIndex, BorderLayout.EAST);

    temp.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          int hi = (int) computeHeatIndex((double) temp.getValue(),
                                          (double) humidity.getValue());
          heatIndex.setValue(hi);
        }
      });

    humidity.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          int hi = (int) computeHeatIndex((double) temp.getValue(),
                                          (double) humidity.getValue());
          heatIndex.setValue(hi);
        }
      });
  }

  /**
   * Compute the heat index.  I cannot explain the constants used in
   * this computation.
   *
   * @param temp
   *        The temperature in degrees farenheit
   * @param humidity
   *        The relative humidity
   */
  private double computeHeatIndex(double temp, double humidity) {
    double heatIndex = 
      -42.379 
      + (2.04901523 * temp) 
      + (10.14333127 * humidity) 
      - (0.22475541 * temp * humidity)
      - (6.83783e-3 * Math.pow(temp, 2.0)) 
      - (5.481717e-2 * Math.pow(humidity, 2.0))
      + (1.22874e-3 * Math.pow(temp, 2.0) * humidity) 
      + (8.5282e-4 * temp * Math.pow(humidity, 2.0)) 
      - (1.99e-6 * Math.pow(temp, 2.0) * Math.pow(humidity, 2.0));
    return heatIndex;
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("JSlider Example: Heat Index");
    JPanel panel = new HeatIndex();
    frame.getContentPane().add(panel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }
 
}
