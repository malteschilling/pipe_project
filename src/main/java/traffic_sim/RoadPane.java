package traffic_sim;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import javax.swing.JPanel;

/**
 * RoadPane
 * 
 * Simple visualization of the traffic scene.
 */
public class RoadPane extends JPanel {
	
	/**
	 * Construct a visualization.
     */
	public RoadPane() {
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(800, 350);
	}

	/**
 	 * paintComponent describes the visualization -
 	 * is called through the repaint() call.
 	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		
		for (TemporalTrafficObject trObj : TemporalTrafficObject.updateList) {
			trObj.redraw(g2d);
		}
		// Draw traffic lights as dots.
    	for (TrafficLight tl : TrafficLight.trafficLights) {
    		if (tl.isTrafficLightGreen()) {
	    		g2d.setPaint(Color.green);
	    	} else {
	    		g2d.setPaint(Color.red);
	    	}
	    	Point tl_pos = tl.getTrafficLightPosition();
			g2d.fillOval( tl_pos.x, tl_pos.y, 15, 15);
		}
			
		g2d.dispose();			
	}

}