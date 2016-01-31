package traffic_sim;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
/**
 * Cam
 * 
 * Can be linked to a Lane as a sensor. It counts the vehicles in front of the 
 * TrafficLight.
 * Because of intended measurement inaccuracy it depends on the vehicle's distance and velocity.
 *
 */
//TODO: Different parameters for each Cam?
public class Cam extends TemporalTrafficObject{

	/**
	 * The Lane that Cam is linked to.
	 */
	private Lane lane;
	/**
	 * Length of the Lane.
	 */
	//TODO: What to do if LANE_LENGTH <  OBSERVATION_DISTANCE?
	private double LANE_LENGTH;
	/**
	 * Expected number of Vehicles in front of the TrafficLight.
	 */
	private int numberOfVehicles;
	/**
	 * Maximal distance the Cam is allowed to observe.
	 */
	private final double OBSERVATION_DISTANCE = 50.;
	/**
	 * Maximal velocity the Cam is able to record.
	 */
	private final double MAX_OBSERVABLE_VELOCITY = 100;
	//TODO: Find correct parameter for PARAM1_DIST and PARAM1_VELO.
	/**
	 * Parameter to change the influence of the vehicle's distance.
	 */
	private final double PARAM1_DIST = 50;
	/**
	 * Parameter to change the influence of the vehicle's velocity.
	 */
	private final double PARAM1_VELO = 40;
	//TODO: Set seed?
	private static Random rand = new Random();
	/**
	 * Constructor
	 * Cam is linked to one Lane.
	 */
	public Cam(Lane lane) {
		this.lane = lane;
		this.LANE_LENGTH = this.lane.getLength();
	//TODO: ?! Integrate other lanes for that case: LANE_LENGTH < OBSERVATION_DISTANCE
		
	}
	
	/**
	 * Set the expected number of vehicles in front of the TrafficLight until {@link #OBSERVATION_DISTANCE}.
	 * Depends on the position and velocity of each single vehicle.
	 */
	public void countVehicleInFront() {
		
		ConcurrentLinkedQueue<Vehicle> vehicleOnLane = lane.getVehiclesOnLane();
		int count = 0;
		for(Vehicle vehicle : vehicleOnLane) {
			count = shouldCount(vehicle) ? ++count : count; 
		}
		this.numberOfVehicles = count;
	}
	/**
	 * Decides to count a vehicle or not.
	 * Depends on the position and velocity of the vehicle.
	 * 
	 * Normal distribution used for both.
	 * Checks the threshold of maximal distance and velocity.
	 * 
	 * @param vehicle
	 * @return true or false
	 */
	private boolean shouldCount(Vehicle vehicle) {
		double position = vehicle.getPositionInLane();
		double velocity = vehicle.getCurrentVelocity();
		
		if (LANE_LENGTH - position > OBSERVATION_DISTANCE && velocity > MAX_OBSERVABLE_VELOCITY) {
			return false;
		} else if ( Math.abs(rand.nextGaussian()*PARAM1_DIST) > LANE_LENGTH - position && 
				Math.abs(rand.nextGaussian()*PARAM1_VELO) > velocity) {
			return true;
		} else {
			return false;
		}
		
	}
	
	/**
	 * Get expected number of vehicles.
	 * 
	 * @return numberOFVehicles
	 */
	public int getNumberOfVehicles() {
		return numberOfVehicles;
	}
	
	@Override
	public void updateStep(double duration) {
		countVehicleInFront();
		//System.out.println("Expected Number Of cars: "+getNumberOfVehicles());
		//System.out.println("Number Of cars: "+getNumberOfVehicles()+"Lane: "+lane.getVehiclesOnLane().size());
	}
	
}

