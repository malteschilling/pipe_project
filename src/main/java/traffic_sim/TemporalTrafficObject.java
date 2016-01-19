package traffic_sim;

import java.awt.*;
import java.util.ArrayList;

/**
 * AbstractClass for all simulation objects that require updates.
 *
 * updateStep has to be implemented and is called on all those objects for
 * every basic call to updateAll().
 */
public abstract class TemporalTrafficObject {
    
    // Collection of all simulation objects that require updates.
    protected static ArrayList<TemporalTrafficObject> updateList = new ArrayList<>();
    
    public TemporalTrafficObject() {
        updateList.add(this);
    }

	/**
	 * UpdateStep is called every simulation iteration.
	 *
	 * Has to be implemented by derived classes.
	 */
	public abstract void updateStep(double duration);
	
	/**
	 * Main simulation step.
	 *
	 * Calls all updateStep() for all simulation objects and forces a repaint.
	 */
	public static void updateAll(double duration) {
		for (TemporalTrafficObject updObj : updateList) {
			updObj.updateStep(duration);
		}
	}
    
}