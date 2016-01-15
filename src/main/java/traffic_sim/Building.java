package traffic_sim;


import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by hagbard on 1/12/16.
 */
public class Building extends TemporalTrafficObject implements VehicleConsumerInterface, VehicleProducerInterface {
    private Point position;
    private Map<Vehicle, Integer> vehicles;
    private String name;
    private Lane startLane, endLane;

    public Building(int x, int y, String name) {
        this(new Point(x, y), name);
    }

    public Building(Point position) {
        this(position, "");
    }

    public Building(Point position, String name) {
        this.position = position;
        this.name = name;
        this.vehicles = new HashMap<>();
    }

    @Override
    public void updateStep(double duration) {
        for (Iterator<Map.Entry<Vehicle, Integer>> iterator = vehicles.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Vehicle, Integer> entry = iterator.next();
            if (entry.getValue() > 0 && this.startLane.spaceForNewCarAvailable()) {
                entry.getKey().switchToLane(this.startLane);
                vehicles.remove(entry.getKey());
            } else entry.setValue(entry.getValue() - 1);
        }
    }

    @Override
    public void redraw(Graphics2D g2d) {
        g2d.drawRect(position.x, position.y, 10, 10);
    }

    @Override
    public boolean tryToConsumeVehicle(Vehicle veh) {
        if (vehicles.keySet().contains(veh)) {
            System.err.println("Trying to consume a vehicle that is already on this place");
            System.err.println(name);
            return false;
        }
        vehicles.put(veh, new Random().nextInt(10000) + 5000);
        return true;
    }

    @Override
    public void setLaneEnding(Lane lane) {
        this.endLane = lane;
    }

    @Override
    public Lane getLaneEnding() {
        return this.endLane;
    }

    @Override
    public Point getEndPoint() {
        return position;
    }

    @Override
    public void setEndPoint(int x, int y) {
        this.position = new Point(x, y);
    }

    @Override
    public void setLaneStarting(Lane lane) {
        this.startLane = lane;
    }

    @Override
    public Lane getLaneStarting() {
        return this.startLane;
    }

    @Override
    public Point getStartPoint() {
        return position;
    }

    @Override
    public void setStartPoint(int x, int y) {
        this.position = new Point(x, y);
    }
}
