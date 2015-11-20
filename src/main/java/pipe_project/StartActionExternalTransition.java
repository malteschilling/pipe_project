package pipe_project;

import uk.ac.imperial.pipe.models.petrinet.AbstractExternalTransition;

import javax.json.Json;
import javax.json.JsonObject;

import uk.ac.imperial.pipe.models.petrinet.AbstractTransitionJsonParameters;
import uk.ac.imperial.pipe.runner.JsonParameters;

/**
 * The fired transition connecting to the external action.
 */
public class StartActionExternalTransition extends AbstractExternalTransition {
	@Override
	public void fire() {
		((DiscreteExternalActionCallTransition) this.getExternalTransitionProvider()).getExternalAction().invokeExternalAction();
	}

}