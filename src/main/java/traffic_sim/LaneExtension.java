package traffic_sim;

import java.awt.Point;

import java.util.ArrayList;
import java.util.List;

public class LaneExtension implements VehicleConsumerInterface,
									  VehicleProducerInterface {
	// All positions are the same right now.
	// Better: change VehicleProducerInterface and VehicleConsumerInterface
	// to prohibit setting of start/end points.
	private Point position, startPosition, endPosition;
	private List<Lane> outgoingLanes;
	private Lane endLane;

	public LaneExtension(Point position) {
		setPositions(position);
		outgoingLanes = new ArrayList<Lane>();
	}

	public LaneExtension(int xPosition, int yPosition) {
		setPositions(new Point(xPosition, yPosition));
		outgoingLanes = new ArrayList<Lane>();
	}

	public LaneExtension(Point position, List<Lane> outgoingLanes,
			Lane endLane) {
		setPositions(position);

		this.outgoingLanes = outgoingLanes;
		this.endLane = endLane;
	}

	public LaneExtension(int xPosition, int yPosition,
			List<Lane> outgoingLanes, Lane endLane) {
		setPositions(new Point(xPosition, yPosition));

		this.outgoingLanes = outgoingLanes;
		this.endLane = endLane;
	}

	private void setPositions (Point position) {
		this.position = position;
		this.startPosition = position;
		this.endPosition = position;
	}

	@Override
	public boolean tryToConsumeVehicle(Vehicle veh) {
		int numberOfOutgoingLanes = outgoingLanes.size();
		if (numberOfOutgoingLanes > 0) {
			return veh.switchToLane(outgoingLanes);
		} else {
			veh.removeVehicleFromLane();
			return true;
		}
//		veh.removeVehicleFromLane();
	}

	@Override
	public void setLaneEnding(Lane lane) {
		endLane = lane;
	}

	@Override
	public Lane getLaneEnding() {
		return endLane;
	}

	@Override
	public Point getEndPoint() {
		return endPosition;
	}

	@Override
	public void setEndPoint(int x, int y) {
		this.endPosition = new Point(x, y);
	}

	@Override
	public void setLaneStarting(Lane lane) {
		// Does not really set the lane but rather adds it to a list of possible
		// lanes. Better: Change VehicleProducerInterface instead.
		this.addOutgoingLanes(lane);
	}

	@Override
	public Lane getLaneStarting() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getStartPoint() {
		return startPosition;
	}

	@Override
	public void setStartPoint(int x, int y) {
		this.startPosition = new Point(x, y);
	}

	public List<Lane> getOutgoingLanes() {
		return outgoingLanes;
	}

	public void setOutgoingLanes(List<Lane> incomingLanes) {
		this.outgoingLanes = incomingLanes;
	}

	public void addOutgoingLanes(Lane ougoingLane) {
		this.outgoingLanes.add(ougoingLane);
	}

}
