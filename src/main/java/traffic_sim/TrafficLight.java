package traffic_sim;

import java.awt.Point;
import java.util.ArrayList;

/**
 * A TrafficLight.
 *
 * It connects two lanes - one lane ends at the traffic light and one starts there.
 * The traffic light regulates when vehicles are allowed to be consumed from
 * that lane and are put on the lane which starts at the traffic light.
 */
public class TrafficLight implements VehicleProducerInterface, VehicleConsumerInterface  {

    protected Lane lane_ends, lane_starts;
	// The current state of the traffic light - right now only green (true) or 
	// red (false).
    private boolean green = false;
    
    // A list of all traffic lights - used for visualization.
    protected static ArrayList<TrafficLight> trafficLights = new ArrayList<>();

	// Visualization points.
	private Point start_point, end_point, graphic_pos;
    
    public TrafficLight() {
    	super();
    	trafficLights.add(this);
    }
    
    /*
	 * Counts number of cars directly in front of the traffic light.
	 * Goes through the ending lane and tests if each spot in front of the light
	 * is occupied.
	 */
    public int getNumberOfWaitingCars() {
    	int waiting = 0;
    	double closest_position = lane_ends.getLength();
    	for (Vehicle vehicleCheck : lane_ends.vehiclesOnLane) {
			if (closest_position == vehicleCheck.getPositionInLane() ) {
				waiting += 1;
			} else {
				break;
			}
			closest_position -= Lane.min_car_distance;
		}
		return waiting;
    }
	
	/*
	 * Overrides the VehicleConsumer method.
	 * Checks if vehicles are allowed to pass and if so puts them 
	 * on the lane leading away from the traffic light.
	 */
	public boolean tryToConsumeVehicle(Vehicle veh) {
		if ((green) & (this.lane_starts.spaceForNewCarAvailable()) ) {
    		veh.switchToLane(this.lane_starts);
	    	return true;
	    } else {
	    	return false;
	    }
    }
    
	public void setLaneEnding(Lane lane) {
		lane_ends = lane;
	}
	
	public Lane getLaneEnding() {
		return lane_ends;
	}
	
	public void setLaneStarting(Lane lane) {
		this.lane_starts = lane;
	}
	
	public Lane getLaneStarting() {
		return this.lane_starts;
	}
	
	/*
	 * StartPoint, EndPoint are points used vor visualization
	 * (where should the lanes be drawn) 
	 */
	public Point getStartPoint() {
		return start_point;
	}
	
	public void setStartPoint(int x, int y) {
		start_point = new Point(x, y);
	}
	
	public Point getEndPoint() {
		return end_point;
	}
	
	public void setEndPoint(int x, int y) {
		end_point = new Point(x, y);
	}
	
	public void setTrafficLightGreen(boolean new_state) {
		this.green = new_state;
	}

	public boolean isTrafficLightGreen() {
		return this.green;
	}
	
	/*
	 * graphic_pos is a point used vor visualization
	 * (where should the traffic light be drawn) 
	 */
	public Point getTrafficLightPosition() {
		return this.graphic_pos;
	} 
	
	public void setTrafficLightPosition(int x, int y) {
		this.graphic_pos = new Point(x, y);
	}    
}