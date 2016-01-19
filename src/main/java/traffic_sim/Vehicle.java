package traffic_sim;

import java.awt.*;
import java.util.Map;

/**
 * Vehicles are moving around in the traffic sim.
 * <p/>
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
	/**
	 * Destination the car would like to reach. Should be as close as possible to the real destination.
	 */
	private Point destination;
	/**
	 * Consumer which is the car's destination.
	 */
	private VehicleConsumer destination_consumer;
	/**
	 * Car's acceleration.
	 */
	private final double ACCEL;
	/**
	 * Car's maximal velocity.
	 */
	private final double MAX_VELOCITY;
	/**
	 * Car's global position.
	 */
	private Point global_position;
	/**
	 * Direction the car is driving in in degrees [-180,180]
	 */
	private double current_direction;
	/**
	 * Minimal distance to preceding car
	 */
	private final double MIN_DIST;

	//TODO If distance to the car ahead is smaller than min_distance decelerate to preceding car's velocity.
	//TODO Only accelerate if the distance to the car in front is 2*min_distance

	/*
	 * Constructor - a vehicle always requires a lane.
	 */
	public Vehicle(String name, Lane lane) {
		super();
		this.name = name;

		current_lane = lane;
		lane.addVehicleToLane(this);
		this.position = 0;
		this.current_velocity = 40.;
		this.ACCEL = 7;
		this.MAX_VELOCITY = 40;
		this.MIN_DIST = MAX_VELOCITY;
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
	 * Return the current target velocity of the vehicle. If car ahaed is too close reduce velocity.
	 */
	public double getCurrentVelocity() {
		return this.current_velocity;
	}

	/**
	 * Accelerate the car. New velocity is determined by fixed acceleration and the elapsed time.
	 * If the car ahead is too close don't accelerate.
	 *
	 * @param timedelta Elapsed time since last acceleration call
	 */
	public void accelerate(double timedelta) {
		current_velocity += timedelta * ACCEL;
		current_velocity = current_velocity > MAX_VELOCITY ? MAX_VELOCITY : current_velocity;
	}

	/**
	 * Immediately decelerate to given velocity. Can only decrease velocity.
	 *
	 * @param goal_velocity Velocity to decelerate to
	 */
	public void decelerate(double goal_velocity) {
		current_velocity = goal_velocity < current_velocity ? goal_velocity : current_velocity;
	}

	/**
	 * Stop the car immediately.
	 */
	public void stop() {
		current_velocity = 0;
	}

	public Point getGlobal_position() {
		return global_position;
	}

	public void setGlobal_position(Point global_position) {
		this.global_position = global_position;
	}

	public double getCurrent_direction() {
		return current_direction;
	}

	/**
	 * Set new direction for the car in degrees [-180, 180].
	 * 0 for going right, positive angles for going clockwise, negative for counter-clockwise.
	 *
	 * @param direction Direction in degree.
	 *                  Angles greater than 180 or smaller than -180 are converted back into range.
	 */
	public void setCurrent_direction(double direction) {
		direction %= 360;
		direction = direction > 180 ? direction - 360 : direction;
		direction = -direction > 180 ? direction + 360 : direction;
		current_direction = direction;
	}

	public VehicleConsumer getDestination_consumer() {
		return destination_consumer;
	}

	public void setDestination_consumer(VehicleConsumer destination_consumer) {
		this.destination_consumer = destination_consumer;
	}

	public Point getDestination() {
		return destination;
	}

	public void setDestination(Point destination) {
		this.destination = destination;
	}

	/**
	 * Should be called when the car reaches a crossing. Determines where to go.
	 *
	 * @param possible_decisions For each possible lane the change of direction in degrees
	 * @return The decision the car has taken.
	 */
	public Lane getDecision(Map<Lane, Double> possible_decisions) {
		if (possible_decisions.isEmpty()) {
			return null;
		}
		Point dest_delta = new Point(destination.x - global_position.x, destination.y - global_position.y);
		double dest_global_angle = Math.atan2(dest_delta.y, dest_delta.x);
		double dest_rel_angle = current_direction - dest_global_angle;
		//If angle is greater than 180 it is shorter to turn the other way around
		if (dest_rel_angle > 180) {
			dest_rel_angle -= 360;
		} else if (dest_rel_angle < -180) {
			dest_rel_angle += 360;
		}
		Lane bestLane = null;
		double min_delta = 360;
		for (Map.Entry<Lane, Double> decision : possible_decisions.entrySet()) {
			double remaining_angle = dest_rel_angle - decision.getValue();
			if (remaining_angle < min_delta) {
				min_delta = remaining_angle;
				bestLane = decision.getKey();
			}
		}
		return bestLane;
	}

	/**
	 * Returns the distance to the car in front
	 * @return Distance to car in front or 4*<code>MIN_DIST</code>
	 */
	public double getFrontVehicleDistance() {
		Vehicle inFront = current_lane.getVehicleInFront(this);
		if (inFront != null) {
			return Math.abs(inFront.getPositionInLane() - this.getPositionInLane());
		}
		else {
			return 4*MIN_DIST;
		}
	}
}