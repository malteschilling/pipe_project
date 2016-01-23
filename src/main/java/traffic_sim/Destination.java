package traffic_sim;

import java.awt.*;

/**
 * This class implements a destination any vehicle can have inside the simulation.
 * It is the data structure that holds all neccessary information needed for a vehicle
 * to get to a certain position and how long to stay there.
 *
 * @author srottschaefer.
 * @date 19.01.16
 */
public class Destination {

	private final Point position;
	private final double wait_sec;
	private double remaining_wait_sec;
	private VehicleConsumer consumer;

	public Destination(Point position, double wait_sec, VehicleConsumer consumer) {
		this.position = position;
		this.wait_sec = wait_sec;
		this.remaining_wait_sec = wait_sec;
		this.consumer = consumer;
	}

	public double getRemaining_wait_sec() {
		return remaining_wait_sec;
	}

	public void updateWait(double sec) {
		this.remaining_wait_sec -= sec;
		sec = sec < 0 ? 0 : sec;
	}

	public VehicleConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(VehicleConsumer consumer) {
		this.consumer = consumer;
	}

	public Point getPosition() {
		return position;
	}
}
