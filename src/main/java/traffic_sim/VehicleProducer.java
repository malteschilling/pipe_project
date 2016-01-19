package traffic_sim;

import java.awt.*;

/**
 * VehicleProducer
 * 
 * Generic implementation of the VehicleProducerInterface.
 * VehicleProducer are startpoints of lanes, in this case
 * cars simply are appearing every couple of time steps - therefore it is a 
 * TemporalTrafficObject which are all called in simulation update steps.
 */
public class VehicleProducer extends TemporalTrafficObject implements VehicleProducerInterface {

	// the connected lane
    protected Lane lane_starts;
    private double time = 0.;
    // Point for graphic visualization
    private Point start_point;
    
    public VehicleProducer() {
    	super();
    	start_point = new Point();
    }
    
    /*
     * Update Step of the simulation.
     */
    public void updateStep(double duration) {
		if (time % 5 == 0.) {
			if (this.lane_starts.spaceForNewCarAvailable() ) {
				new Vehicle( ("Car_" + time), this.lane_starts );
			}
		}
		time += duration;
	}

	public void setLaneStarting(Lane lane) {
		this.lane_starts = lane;
	}
	
	public Lane getLaneStarting() {
		return this.lane_starts;
	}
	
	public Point getStartPoint() {
		return start_point;
	}
	
	public void setStartPoint(int x, int y) {
		start_point = new Point(x, y);
	}
   
}