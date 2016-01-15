package pipe_project;

import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

import uk.ac.imperial.pipe.runner.RealTimePetriNetRunner;
import uk.ac.imperial.pipe.dsl.APetriNet;
import uk.ac.imperial.pipe.dsl.APlace;
import uk.ac.imperial.pipe.dsl.AToken;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.*;

import traffic_sim.*;

import java.awt.Color;
import javax.swing.SwingUtilities;

/**
 * Running and constructing a petri network for the traffic simulation:
 * 
 * Starts the simplistic traffic simulation and a petri net that controls a traffic
 * light. The main purpose of this program is to learn how to integrate external 
 * inputs, how to synchronize petri nets and an internal simulation and
 * how to cause actions in the external program.
 *
 * The initial program is minimalistic and provides a simple solution to those 
 * questions, but only in a more complex scenario the advantage of such an approach
 * can become apparent.
 * Right now:
 * when there are 3 or more cars in front of the traffic light 
 * (this is tested in the TrafficLightObserver) the PetriNet place for WAITING is marked
 *  which causes the following transition to fire. This external transition is linked 
 * to an external action which switches the traffic light.
 *
 * The TASK: 
 * - extend the simple traffic simulation to a complete crossing (two streets crossing
 *   each other with traffic on both streets in both direction)
 * - introduce meaningful predicates in the simulation which you pull in order
 *   to drive/influence the petri net (maybe also use timed transitions)
 * - You can (and should) change the simulation behavior at least in one setting:
 *   e.g. introduce different (probably random) rates with which vehicles are set onto the 
 *   different lanes and those might change over time. As a consequence, you might want
 *   to introduce new predicates pulled from the simulation (when a car is arriving ...).
 * - Construct a Petri network to control the traffic lights.
 *   You should work it out for one crossing and then duplicate those.
 *   It might be helpful to look up the include hierarchies which allow for modular nets.
 * And of course you are allowed to extend the traffic situation further, e.g. introduce
 * a pedestrian crossing ... 
 */
public class MainPN {
	
	public static RealTimePetriNetRunner runner;
	
    public static void main( String[] args )
    {
     	// 1. Construct main window showing traffic situation (and exit button)   
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
                View view = new View("-"); 
                TrafficGuiController controller = new TrafficGuiController(view);
                controller.control();
            }
        });  
        
        // 2. Construct traffic simulation scene
		// for example: on the left side a VehicleProducer sets vehicles onto the first lane
        VehicleProducer prod = new VehicleProducer();
        // has to define a position for graphic layout in visualization
        prod.setStartPoint(100, 100);
        // on right hand: vehicles disappear in this VehicleConsumer
        VehicleConsumer cons = new VehicleConsumer();
        cons.setEndPoint(500, 500);
        // Traffic light in the middle (shown at end of first lane)
		Building building = new Building(500, 100, "testPoint");
		TrafficLight tl = new TrafficLight();
        // again, points have to be defined for the visualization in order to define where
        // the lane should be drawn
        tl.setStartPoint(310, 100);
        tl.setEndPoint(300, 100);
        // and a position for the traffic light itself has to be defined
        tl.setTrafficLightPosition( 290, 120);
        // The lanes connecting the different producers-consumers
        Lane lane_east = new Lane("VorAmpel", prod, tl);
        Lane lane_east_2 = new Lane("NachAmpel", tl, building);
		Lane nachBuilding = new Lane("NachBuilding", building, cons);

		// An object observing the simulator state (is pulled each simulation update)
        // and can change the marking in the Petri Network (in the target place)
        TrafficLightObserver tlWaiting = new TrafficLightObserver( tl );
        tlWaiting.setActionPNTargetPlace( "WAITING" );
        
        try {
        	// 3. Build the simple Petri network controlling traffic:
        	// when there are 3 or more cars in front of the traffic light 
        	// (this is tested in the TrafficLightObserver above)
        	// the PetriNet place for WAITING is marked which causes 
        	// the following transition to fire.
        	// This external transition is linked to an external action which switches
        	// the traffic light (this seems overly complicated in this trivial scenario
        	// but becomes important when one has to deal with many concurrent ongoing
        	// and possibly temporal processes depending on the petri net).
        	PetriNet currentPN = buildNet( tl );
        	Collection<Place> placesPN = ( currentPN ).getPlaces();
	    	for (Place singlePlaceObj : placesPN) {
	    		System.out.println("Places: " + singlePlaceObj.getId() + " = " + singlePlaceObj.getNumberOfTokensStored() );
   			}
	    	
	    	// Start running of the PN
    		runner = new RealTimePetriNetRunner( currentPN );
			FiringGenericActionListener firedTrans = new FiringGenericActionListener();
			runner.addPropertyChangeListener(firedTrans);
			
			runner.startRealTimeClock();
			
			// Visualization of the PN
			/*VisualizationPetriNetFrame frame = new VisualizationPetriNetFrame( currentPN );
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(640, 480);
			frame.setVisible(true);*/
				
			while (true) {
				// Stepping the Petri Net every second (this should be tuned to a lower value 
				// when your system is running).
				// and for the rest of the time the thread sleeps.
	            runner.stepPetriNetSynchronized(1000);
	            // And calling an update on the traffic simulation.
	            TemporalTrafficObject.updateAll( 1. );
	        }

    	} catch (Exception e) {
	    		e.printStackTrace();
    	}

    }

    /**
     * Build test Petri network for traffic light.
     */     
	private static PetriNet buildNet(TrafficLight tl) throws PetriNetComponentException {
		// Create a Petri Net.
		PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("WAITING").externallyAccessible()).andFinally(
    		APlace.withId("GREEN"));
    	Collection<Place> placesPN = petriNet.getPlaces();
    	Place startPlace = null;
    	Place endPlace = null;
	    for (Place singlePlaceObj : placesPN) {
	    	if ( singlePlaceObj.getId().equals("WAITING") ) {
	    		startPlace = singlePlaceObj;
	    	} else {
	    		endPlace = singlePlaceObj;
	    	}
   		}
		
		// Here is an ExternalAction coupled to a transition.
		// First, the externalAction object is created.
		// The invokeExternalAction will be called when the transition is fired
		// (internally this is a little bit complicated as a given PN structure
		// gets copied for each instantiation of a new petri net runner (which advances
		// the state of a PN) and therefore we can not simply derive our own type
		// of transition, but have to have a construct of DiscreteExternalActionCallTransition
		// (which I have built) and the external action objects which you
		// can customize (see TrafficLightTransitionCall as an example).
    	TrafficLightTransitionCall extAct = new TrafficLightTransitionCall( tl );    	
    	DiscreteExternalActionCallTransition newTransition = new DiscreteExternalActionCallTransition( "SwitchToGreen", "SwitchToGreen", extAct );
    	petriNet.addTransition(newTransition);
		
		// Connecting the network.
		Map<String, String> weights = new HashMap<>();
		weights.put("Default", "1");
		Map<String, String> weights2 = new HashMap<>();
		weights2.put("Default", "1");
    	petriNet.addArc( new InboundNormalArc( startPlace, newTransition, weights ) );
    	petriNet.addArc( new OutboundNormalArc( newTransition, endPlace, weights2 ) );
    
		return petriNet;
	}

}
