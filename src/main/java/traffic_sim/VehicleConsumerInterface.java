package traffic_sim;

import java.awt.Point;

/**
 * VehicleConsumerInterface
 *
 * VehicleConsumer are endpoints of lanes.
 * The derived class must implement when cars are allowed to leave the lane
 * and in the same step remove them from the lane. 
 */
public interface VehicleConsumerInterface {
    
	/*
     * A VehicleConsumer has to implement this method to check
     * if vehicles are allowed to leave a lane (e.g. for a traffic light this 
     * is not always the case).
     */
    public boolean tryToConsumeVehicle(Vehicle veh);
    
	public void setLaneEnding(Lane lane);
	public Lane getLaneEnding();
	
	public Point getEndPoint();
	public void setEndPoint(int x, int y);
    
}