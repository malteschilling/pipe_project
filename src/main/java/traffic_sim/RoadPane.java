package traffic_sim;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Collections;
import java.util.PriorityQueue;
import java.util.TreeSet;
import javax.swing.JPanel;

/**
 * RoadPane
 * 
 * Simple visualization of the traffic scene.
 */
public class RoadPane extends JPanel {
	private Collection<Drawable> drawables;
	
	/**
	 * Construct a visualization.
     */
	public RoadPane() {
		drawables = new PriorityQueue<>(new DrawableComparator());
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
		for (Drawable drawable : drawables) {
			drawable.redraw(g2d);
		}

		g2d.dispose();			
	}

	public void addDrawable(Drawable d){
		this.drawables.add(d);
	}

	public void addAllDrawables(Drawable... drawables){
		Collections.addAll(this.drawables, drawables);
	}

}