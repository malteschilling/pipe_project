package traffic_sim;

import pipe_project.MainPN;

public class TrafficLightObserverPlace extends TemporalTrafficObject {

	protected String target_place = "none";
	private TrafficLight traffic_light;
	
	public TrafficLightObserverPlace(TrafficLight tl) {
		this.traffic_light = tl;
	}

	public void setActionPNTargetPlace(String targetPlaceName) {
		this.target_place = targetPlaceName;
	}

    public void updateStep(double duration) {
		if (!(target_place.equals("none"))) {
    		try {
    			if (traffic_light.getNumberOfWaitingCars() > 2) {
	    			MainPN.runner.markPlace( target_place, "Default", 1 );
    			}
    		} catch (Exception e) {
				e.printStackTrace();
	        }
    	}	
	}
}