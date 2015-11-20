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
			if (trObj instanceof Lane) {
				// Draw the lane
				g2d.setPaint(Color.gray);
				Lane tempLane = (Lane) trObj;
				Point start = tempLane.start_connection.getStartPoint();
				Point end = tempLane.end_connection.getEndPoint();
				g2d.setStroke(new BasicStroke(30, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
                g2d.draw(new Line2D.Float(start.x, start.y, end.x, end.y) );
                
                // Draw vehicles on the lane as blue rectangles.
                g2d.setPaint(Color.blue);
                for (Vehicle veh : tempLane.vehiclesOnLane) {
                	double posPerc = veh.getPositionInLane() / tempLane.getLength();
                	int x = ( (int) (start.x * (1-posPerc) + end.x * posPerc ) );
                	int y = ( (int) (start.y * (1-posPerc) + end.y * posPerc ) );
					g2d.fillOval( (x-5), (y-5), 10, 10);
				}
			}	
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