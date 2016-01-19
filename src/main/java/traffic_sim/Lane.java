package traffic_sim;

import java.awt.*;
import java.awt.geom.Line2D;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A Traffic Lane.
 * <p>
 * Lanes are part of the structure of the layout:
 * They connect the end (VehicleConsumer) and start (VehicleProducer) points
 * Vehicles are put onto lanes and are moved along those lanes.
 * <p>
 * A Lane is a TemporalTrafficObject - it is therefore called with the simulation
 * update cycle. In the update step, vehicles are moved along.
 */
public class Lane extends TemporalTrafficObject implements Drawable {

    private String name;
    // Minimum distance between cars
    protected static double min_car_distance = 10;
    // Start of the lane
    protected VehicleProducerInterface start_connection;
    // End of the lane
    protected VehicleConsumerInterface end_connection;
    // Length of the Lane
    // and free_until is used during the update step: it is iterated over the vehicles
    // from the end of the lane (the car which entered first is evaluated first)
    // and the position of the last car is registered.
    protected double length, free_until;
    // All cars inside the lane.
    protected ConcurrentLinkedQueue<Vehicle> vehiclesOnLane = new ConcurrentLinkedQueue<>();

    /**
     * Constructor for a Lane, needs name, start and end connection.
     */
    public Lane(String name, VehicleProducerInterface prod, VehicleConsumerInterface cons) {
        super();
        this.name = name;
        this.start_connection = prod;
        this.start_connection.setLaneStarting(this);
        this.end_connection = cons;
        this.end_connection.setLaneEnding(this);
        length = 100;
        free_until = length;
    }

    /**
     * Add a vehicle to the lane at the beginning of the lane.
     */
    public void addVehicleToLane(Vehicle veh) {
        vehiclesOnLane.add(veh);
        veh.setCurrentLane(this);
        free_until = 0;
    }

    /**
     * Set where the Lane ends (a VehicleConsumer).
     */
    public void setEndConnection(VehicleConsumer consumer) {
        this.end_connection = consumer;
    }

    /**
     * Set where the Lane starts (a VehicleProducer).
     */
    public void setStartConnection(VehicleProducer producer) {
        this.start_connection = producer;
    }

    /**
     * Update simulation step.
     * <p>
     * Starting from the end of the lane and iterating over the different cars:
     * vehicle positions are updated
     */
    public void updateStep(double duration) {
        //System.out.println("Update Lane " + vehiclesOnLane + " - " + this.name);
        // free_until indicates how far back the end of the traffic is and is used to
        // calculate how far a car is allowed to move.
        free_until = length;
        for (Vehicle vehicleUpdate : vehiclesOnLane) {
            tryToMoveVehicle(vehicleUpdate, duration);
        }
    }

    @Override
    public void redraw(Graphics2D g2d) {
        // Draw the lane
        g2d.setPaint(Color.gray);
        Point start = this.start_connection.getStartPoint();
        Point end = this.end_connection.getEndPoint();
        g2d.setStroke(new BasicStroke(30, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
        g2d.draw(new Line2D.Float(start.x, start.y, end.x, end.y));

        // Draw vehicles on the lane as blue rectangles.
        g2d.setPaint(Color.blue);
        for (Vehicle veh : this.vehiclesOnLane) {
            double posPerc = veh.getPositionInLane() / this.getLength();
            int x = ((int) (start.x * (1 - posPerc) + end.x * posPerc));
            int y = ((int) (start.y * (1 - posPerc) + end.y * posPerc));
            g2d.fillOval((x - 5), (y - 5), 10, 10);
        }
    }

    @Override
    public Integer priority() {
        return 1;
    }

    /**
     * Move vehicle if possible.
     * <p>
     * Updates position of the vehicle after check if there is no other vehicle blocking
     * or (at the end of the lane) if it can leave the lane.
     */
    public void tryToMoveVehicle(Vehicle vehicle, double duration) {
        double newPos = vehicle.getPositionInLane() + vehicle.getCurrentVelocity() * duration;
        if ((newPos > length) && (free_until == length)) {
            if (!(end_connection.tryToConsumeVehicle(vehicle))) {
                newPos = length;
                free_until = length - min_car_distance;
                vehicle.setPositionInLane(newPos);
            }
        } else {
            if (newPos > free_until) {
                newPos = free_until;
            }
            free_until = newPos - min_car_distance;
            vehicle.setPositionInLane(newPos);
        }
    }

    /**
     * Check if a new car can be put on the lane (at the beginning).
     */
    public boolean spaceForNewCarAvailable() {
        if (free_until < min_car_distance) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Remove vehicle from Lane.
     */
    protected void removeVehicleFromLane(Vehicle veh) {
        vehiclesOnLane.remove(veh);
    }

    /**
     * Get distance to travel for a vehicle position until the end of the lane.
     */
    public double getDistanceToEnd(double veh_pos) {
        return (length - veh_pos);
    }

    /**
     * Get length of the lane.
     */
    public double getLength() {
        return length;
    }

    /**
     * Set length of the lane.
     */
    public void setLength(double length) {
        this.length = length;
    }

	/**
     * Returns the vehicle in front of the given one, null if there is none.
     * @param vehicle The vehicle
     * @return The vehicle in front of the given vehicle or null if there is none in front
     */
    public Vehicle getVehicleInFront(Vehicle vehicle) {
        Vehicle inFront = null;
        for (Vehicle v : vehiclesOnLane) {
            if (v.equals(vehicle)) {
                break;
            }
            else {
                inFront = v;
            }
        }
        return inFront;
    }

	/**
     * Returns the number of vehicles in front of the given one.
     * @param vehicle The vehicle
     * @return Number of vehicles in front of the given one
     */
    public int getVehicleNumberInFront(Vehicle vehicle) {
        int inFront = 0;
        for (Vehicle v : vehiclesOnLane) {
            if (v.equals(vehicle)) {
                break;
            } else {
                inFront++;
            }
        }
        return inFront;
    }
}