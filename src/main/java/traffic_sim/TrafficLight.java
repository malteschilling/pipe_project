package traffic_sim;

import java.awt.Point;

public class TrafficLight implements VehicleProducerInterface, VehicleConsumerInterface  {

    protected Lane lane_ends, lane_starts;
    private double time = 0.;
    private boolean green = false;

	private Point start_point, end_point;
    
    public TrafficLight() {
    	super();
    }
    
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
		//System.out.println("WAITING: " + waiting);
		return waiting;
    }
	
	public boolean tryToConsumeVehicle(Vehicle veh) {
		if ((green) & (this.lane_starts.spaceForNewCarAvailable()) ) {
    		//veh.removeVehicleFromLane();
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
    
}