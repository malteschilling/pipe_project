package pipe_project;

import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.AbstractTransition;
import uk.ac.imperial.pipe.models.petrinet.DiscreteExternalTransition;
import uk.ac.imperial.pipe.models.petrinet.DiscreteExternalTransitionVisitor;
import uk.ac.imperial.pipe.models.petrinet.ExternalTransitionProvider;
import uk.ac.imperial.pipe.models.petrinet.Transition;
import uk.ac.imperial.pipe.models.petrinet.TransitionVisitor;
import uk.ac.imperial.pipe.visitor.TransitionCloner;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

import traffic_sim.TrafficLightTransitionCall;

/**
 * An external transition wrapping an attached external action which should be called
 * when the (connected) transition is fired.
 *
 * In modular PetriNets for execution cloned instances are called.
 * This requires the indirect approach to attach an external action to the transition
 * which can have references and access directly to simulation objects.
 */
public class DiscreteExternalActionCallTransition extends DiscreteExternalTransition {
	
	// The coupled external action (has to implement the ExternalActionInterface)
	protected ExternalActionInterface externalAction;

	// A copy constructor is required.
	public DiscreteExternalActionCallTransition(DiscreteExternalActionCallTransition transition) {
		super(transition);
		this.externalAction = transition.externalAction;
	}

	public DiscreteExternalActionCallTransition(String id, String name, TrafficLightTransitionCall extAct) {
		// Here the real transition is build as a StartActionExternalTransition.
		super(id, name, "pipe_project.StartActionExternalTransition");
		this.externalAction = extAct;
	}

	// Setting the external action which shall be called when the transition is fired.	
	public void setExternalAction(ExternalActionInterface extAct) {
		this.externalAction = extAct;
	}
	
	public ExternalActionInterface getExternalAction() {
		return this.externalAction;
	}
	
	/**
     * the visitor is a {@link uk.ac.imperial.pipe.models.petrinet.DiscreteTransitionVisitor} or a
     * {@link uk.ac.imperial.pipe.models.petrinet.TransitionVisitor}.
     * @param visitor
     */
    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        if (visitor instanceof TransitionVisitor) {
            ((TransitionVisitor) visitor).visit(this);
        }
        if (visitor instanceof TransitionCloner) {
        	((TransitionCloner) visitor).cloned = new DiscreteExternalActionCallTransition(this);
        }
    }

}
