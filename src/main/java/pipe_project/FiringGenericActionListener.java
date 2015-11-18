package pipe_project;

import java.beans.PropertyChangeEvent;

import java.beans.PropertyChangeListener;

import uk.ac.imperial.pipe.runner.Firing;
import uk.ac.imperial.pipe.runner.PetriNetRunner;
import uk.ac.imperial.pipe.runner.RealTimePetriNetRunner;

public class FiringGenericActionListener implements PropertyChangeListener {

	public static String[] genericActionTransitionKeywords = {"START", "FINISH"};
    public static String[] actionTransitionKeywords = {"START", "GRASP", "MOVE", "PLACE", "FINISH"};

	public FiringGenericActionListener() {
		System.out.println("Created listener for firings");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals(PetriNetRunner.UPDATED_STATE)) {
			if (event.getNewValue() instanceof Firing) {
				String[] transition_name = ((Firing) event.getNewValue()).transition.split("\\.");
				System.out.println("Fired transition " + transition_name[0] + 
						" - time: " + ( (RealTimePetriNetRunner) event.getSource()).getPNTimeSinceStart() + " / " + ( (RealTimePetriNetRunner) event.getSource()).getRealTimeSinceStart() );
			}
		}
	}

}
