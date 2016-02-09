package traffic_sim;

import java.awt.*;
import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.Collections;

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
    // Point for graphic visualization
    private Point start_point;

	public enum TrafficSituation {
		LOW(40), MEDIUM(25), RUSHHOUR(15);
		private int vehicleProductionRate;

		private static final List<TrafficSituation> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
 
		private TrafficSituation(int value) {
			this.vehicleProductionRate = value;
		}
		
		private static final int SIZE = VALUES.size();
		private static final Random RANDOM = new Random();

		public static TrafficSituation randomTrafficSituation()  {
			return VALUES.get(RANDOM.nextInt(SIZE));
  		}
	}
	
	private TrafficSituation currentTrafficSituation = TrafficSituation.MEDIUM;
    private double time = 0.;
    private Random randomGenerator = new Random();
    private double nextTrafficSituationChangeTime = (double) (randomGenerator.nextInt(20));

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
		if (time > nextTrafficSituationChangeTime) {
			currentTrafficSituation = TrafficSituation.randomTrafficSituation();
			//System.out.println("Switched Traffic Situation: " + currentTrafficSituation);
			nextTrafficSituationChangeTime = time + (double) (randomGenerator.nextInt(20));
		}
		if (randomGenerator.nextInt(currentTrafficSituation.vehicleProductionRate) == 0) {
			if (this.lane_starts.spaceForNewCarAvailable() ) {
				new Vehicle( ("Car_" + time), this.lane_starts );
			}
		}
		time += duration;
	}

	/*
	 * Get the currentTrafficSituation for this Producer:
	 * the current traffic situation describes how many cars will be set on the outgoing
	 * lane.
	 */
	public TrafficSituation getCurrentTrafficSituation() {
		return currentTrafficSituation;
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