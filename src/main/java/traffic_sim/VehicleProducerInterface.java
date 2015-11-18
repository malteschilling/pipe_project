package traffic_sim;

import java.awt.Point;

/**
 * VehicleProducerInterface
 *
 * VehicleProducer are startpoints of lanes, where
 * cars are appearing.
 */
public interface VehicleProducerInterface {

	public void setLaneStarting(Lane lane);
	public Lane getLaneStarting();
	
	public Point getStartPoint();
	public void setStartPoint(int x, int y);
 
}