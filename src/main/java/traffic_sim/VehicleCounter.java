package traffic_sim;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * A sensor that can be added to a lane. It counts passing vehicles with a certain accuracy
 * and gives a relative frequency of passing vehicles per turn.
 *
 * @author srottschaefer.
 * @date 29.01.16
 */
public class VehicleCounter extends TemporalTrafficObject{

	private final Lane lane;
	private final double position;
	private long total_vehicles;
	private int[] vehicle_history;
	private int hist_pos = 0;
	private final float accuracy;
	private final Set<Vehicle> already_counted;
	private final Random random;

	public VehicleCounter(Lane lane, double position, float accuracy, int hist_length) {
		this.lane = lane;
		if (position <= lane.getLength()) {
			this.position = position;
		}
		else {
			this.position = lane.getLength();
		}
		this.accuracy = accuracy > 0 ? accuracy : 0.5f;
		total_vehicles = 0;
		vehicle_history = new int[hist_length > 0 ? hist_length : 1];
		already_counted = new HashSet<>();
		random = new Random(System.currentTimeMillis());
	}

	@Override
	public synchronized void updateStep(double duration) {
		int counter = 0;
		for (Vehicle v : lane.vehiclesOnLane) {
			if (v.getPositionInLane() > position && !already_counted.contains(v)) {
				if (random.nextDouble() <= accuracy) {
					counter++;
				}
				already_counted.add(v);
			}
		}
		total_vehicles += counter;
		already_counted.retainAll(lane.vehiclesOnLane);
		vehicle_history[hist_pos] = counter;
		hist_pos++;
		hist_pos = hist_pos == vehicle_history.length ? 0 : hist_pos;
	}

	public synchronized long getTotal_vehicles() {
		return total_vehicles;
	}

	public synchronized double getRelativeFreq() {
		return ((double) IntStream.of(vehicle_history).sum()) / vehicle_history.length;
	}

	//public synchronized SomeState getFreqAsState()
}
