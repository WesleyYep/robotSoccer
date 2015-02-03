package ui;

import com.jidesoft.swing.RangeSlider;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Wesley on 3/02/2015.
 */
public class ColourSlider extends RangeSlider {
    private List<Integer> data = new ArrayList<Integer>();
    private int max = 255;
    private int min = 0;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      //  int scale = getWidth()/100;

        // Mark data points.
        g2.setPaint(Color.red);
        for(int i = 0; i < data.size(); i++) {
            double x = data.get(i);
            g2.fill(new Ellipse2D.Double(x, 0, 4, 4));
        }
    }

    public void addToData(int value) {
        double difference = max - min;
        value = (int)((value - min)/difference * getWidth());
        data.add(value);
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setMin(int min) {
        this.min = min;
    }
}
