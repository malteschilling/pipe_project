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

    public VehicleProducer(Point position) {
    	super();
    	this.start_point = position;
    }

    public VehicleProducer(int xCoordiante, int yCoordinate) {
    	super();
    	start_point = new Point(xCoordiante, yCoordinate);
    }

    /*
     * Update Step of the simulation.
     */
    @Override
	public void updateStep(double duration) {
		if (time % 5 == 0.) {
			if (this.lane_starts.spaceForNewCarAvailable() ) {
				new Vehicle( ("Car_" + time), this.lane_starts );
			}
		}
		time += duration;
	}

	@Override
	public void setLaneStarting(Lane lane) {
		this.lane_starts = lane;
	}

	@Override
	public Lane getLaneStarting() {
		return this.lane_starts;
	}

	@Override
	public Point getStartPoint() {
		return start_point;
	}

	@Override
	public void setStartPoint(int x, int y) {
		start_point = new Point(x, y);
	}

}