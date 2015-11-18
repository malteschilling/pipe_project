package traffic_sim;

import java.util.ArrayList;

/**
 * Vehicles are moving around in the traffic sim.
 * 
 * There could be more distinct types (cars, bikes  ...).
 * A vehicle is always related to a current lane and the position
 * is one dimensional with respect to that lane.
 */
public class Vehicle {

    private String name;
    // This is currently the target velocity,
    // there is no acceleration - vehicles will simply have this velocity if possible.
    private double current_velocity;
    // Position in the current lane.
    private double position;
    // The lane of the vehicle.
    // In the simulations, the lane calls the update of positions for the vehicles in 
    // that lane.
    private Lane current_lane;
    protected static ArrayList<Vehicle> vehicles = new ArrayList<>();
    
    /*
     * Constructor - a vehicle always requires a lane.
     */
    public Vehicle(String name, Lane lane){
    	super();
        this.name = name;
        vehicles.add(this);
        current_lane = lane;
        lane.addVehicleToLane(this);
        this.position = 0;
        this.current_velocity = 40.;
    }

	/*
	 * Get the current position in the related lane.
	 */	
	public double getPositionInLane() {
		return this.position;
	}
	
	/* 
	 * Update the position in the lane.
	 */
	public void setPositionInLane(double pos) {
		this.position = pos;
		//System.out.println("POS : " + this.position + " - " + this.name);
	}
	
	/* 
	 * Remove a vehicle from a lane.
	 */
	public void removeVehicleFromLane() {
		current_lane.removeVehicleFromLane(this);
		this.current_lane = null;
	}
	
	/*
	 * Remove a vehicle from one lane and put it onto another.
	 */
	public void switchToLane(Lane newLane) {
		//this.position = velocity - ( current_lane.getDistanceToEnd( position ) );
		current_lane.removeVehicleFromLane(this);
		newLane.addVehicleToLane(this);
		this.position = 0;
	}
	
	/*
	 * Set current lane.
	 */
	public void setCurrentLane(Lane lane) {
		this.current_lane = lane;
	}
	
	/* 
	 * Set a new current target velocity.
	 */
	public void setCurrentVelocity(double velocity) {
		this.current_velocity = velocity;
	}
	
	/*
	 * Return the current target velocity of the vehicle.
	 */
	public double getCurrentVelocity() {
		return this.current_velocity;
	}
    
}