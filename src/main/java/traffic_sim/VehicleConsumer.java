package traffic_sim;

import java.awt.Point;

/**
 * VehicleConsumer
 * 
 * Generic implementation of the VehicleConsumerInterface.
 * VehicleConsumer are endpoints of lanes, in this case
 * cars simply are removed from the lane and disappear.
 */
public class VehicleConsumer implements VehicleConsumerInterface {

	// the connected lane that ends in the consumer.
    protected Lane lane_ends;
    // Point for graphic visualization
    private Point end_point;
    
    public VehicleConsumer() {
    	end_point = new Point();
    }
    
    /*
     * A VehicleConsumer has to implement this method to check
     * if vehicles are allowed to leave a lane (e.g. for a traffic light this 
     * is not always the case).
     */
    public boolean tryToConsumeVehicle(Vehicle veh) {
    	veh.removeVehicleFromLane();	
    	return true;
    }
    
    /*
     * Set connected lane.
     */
	public void setLaneEnding(Lane lane) {
		lane_ends = lane;
	}
	
	/*
	 * Get connected lane.
	 */
	public Lane getLaneEnding() {
		return lane_ends;
	}
	
	public Point getEndPoint() {
		return end_point;
	}
	
	public void setEndPoint(int x, int y) {
		end_point = new Point(x, y);
	}
    
}