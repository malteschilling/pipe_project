package traffic_sim;

import uk.ac.imperial.pipe.models.petrinet.AbstractExternalTransition;
import pipe_project.ExternalActionInterface;

/**
 * Switch the traffic light to green.
 *
 * Connected to an external transition (DiscreteExternalActionCallTransition)
 * which when fired calls the invokeExternalAction method - which
 * has to be implemented by this class and by every external action.
 */
public class TrafficLightTransitionCall implements ExternalActionInterface {

	// The connected traffic light which shall be switched.
	private TrafficLight traffic_light;

	public TrafficLightTransitionCall(TrafficLight tl) {
		this.traffic_light = tl;
	}

	public void invokeExternalAction() {
		System.out.println("Turned traffic light to green");
		traffic_light.setTrafficLightGreen(true);
	}
    
}