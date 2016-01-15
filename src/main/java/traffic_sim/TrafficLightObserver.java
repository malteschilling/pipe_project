package traffic_sim;

import pipe_project.MainPN;

import java.awt.*;

/**
 * Observes the simulation and marks a connected Place in the PetriNet.
 *
 * Input to the petri net is set from this object - it is called during each
 * simulation step (it is a TemporalTrafficObject which implements the update step).
 */
public class TrafficLightObserver extends TemporalTrafficObject {

	protected String target_place = "none";
	// The observed traffic light.
	private TrafficLight traffic_light;
	
	public TrafficLightObserver(TrafficLight tl) {
		this.traffic_light = tl;
	}

	public void setActionPNTargetPlace(String targetPlaceName) {
		this.target_place = targetPlaceName;
	}

    public void updateStep(double duration) {
		if (!(target_place.equals("none"))) {
    		try {
    			// Marking of Places in PIPE is done locally
    			// = for a specific PetriNetRunner (in practice each runner
    			// has its own copy of a petri net). As we are only interested in
    			// advancing nets right now (and not in deriving all possible successor 
    			// states), there can simple be one main petri net runner.
    			if (traffic_light.getNumberOfWaitingCars() > 2) {
	    			MainPN.runner.markPlace( target_place, "Default", 1 );
    			}
    		} catch (Exception e) {
				e.printStackTrace();
	        }
    	}	
	}

	@Override
	public void redraw(Graphics2D g2d) {

	}
}