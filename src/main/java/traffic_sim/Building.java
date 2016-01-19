package traffic_sim;


import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by hagbard on 1/12/16.
 */
public class Building extends TemporalTrafficObject implements VehicleConsumerInterface, VehicleProducerInterface,Drawable {
    private Point position;
    private Map<Vehicle, Integer> vehicles;
    private String name;
    private Lane startLane, endLane;
    private int sizeX,sizeY;


    private Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

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
        this.sizeX = 10;
        this.sizeY = 10;
        this.color = Color.BLACK;
    }

    @Override
    public void updateStep(double duration) {
        for (Iterator<Map.Entry<Vehicle, Integer>> iterator = vehicles.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<Vehicle, Integer> entry = iterator.next();
            if (entry.getValue() > 0 && this.startLane.spaceForNewCarAvailable()) {
                entry.getKey().switchToLane(this.startLane);
                vehicles.remove(entry.getKey());
            } else entry.setValue(entry.getValue() - (int) duration);//TODO: change
        }
    }

    @Override
    public void redraw(Graphics2D g2d) {
        g2d.setPaint(this.color);
        g2d.fillRect(position.x-(sizeX/2), position.y-(sizeY/2), sizeX, sizeY);
    }

    @Override
    public Integer priority() {
        return 2;
    }

    @Override
    public boolean tryToConsumeVehicle(Vehicle veh) {
        if (vehicles.keySet().contains(veh)) {
            System.err.println("Trying to consume a vehicle that is already on this place");
            System.err.println(name);
            return false;
        }
        vehicles.put(veh, new Random().nextInt(10000) + 5000); //TODO: use sensible value
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
