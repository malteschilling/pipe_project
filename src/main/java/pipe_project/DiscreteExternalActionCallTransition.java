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

public class DiscreteExternalActionCallTransition extends DiscreteExternalTransition {
	
	protected ExternalActionInterface externalAction;

	public DiscreteExternalActionCallTransition(DiscreteExternalActionCallTransition transition) {
		super(transition);
		this.externalAction = transition.externalAction;
	}

	public DiscreteExternalActionCallTransition(String id, String name, TrafficLightTransitionCall extAct) {
		super(id, name, "pipe_project.StartActionExternalTransition");
		this.externalAction = extAct;
	}
	
	public void setExternalAction(ExternalActionInterface extAct) {
		this.externalAction = extAct;
	}
	
	public ExternalActionInterface getExternalAction() {
		return this.externalAction;
	}
	
	/**
     * visits the visitor of it is a {@link uk.ac.imperial.pipe.models.petrinet.DiscreteTransitionVisitor} or a
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
