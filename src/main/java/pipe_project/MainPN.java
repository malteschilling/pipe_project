package pipe_project;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.runner.JsonParameters;
import uk.ac.imperial.pipe.runner.TimedPetriNetRunner;
import uk.ac.imperial.pipe.runner.RealTimePetriNetRunner;
import uk.ac.imperial.pipe.dsl.ANormalArc;
import uk.ac.imperial.pipe.dsl.APetriNet;
import uk.ac.imperial.pipe.dsl.APlace;
import uk.ac.imperial.pipe.dsl.AToken;
import uk.ac.imperial.pipe.dsl.AnImmediateTransition;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.exceptions.IncludeException;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.IncludeHierarchy;

import uk.ac.imperial.pipe.models.petrinet.*;

import uk.ac.imperial.pipe.dsl.AnExternalTransition;

import pn_visualization.*;
import traffic_sim.*;

import javax.swing.JFrame;

import java.awt.Color;

import javax.swing.SwingUtilities;

/**
 * Running and constructing a petri network.
 */
public class MainPN {
	
	public static RealTimePetriNetRunner runner;
	
    public static void main( String[] args )
    {
        System.out.println( "PetriNet Project started" );
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
                View view = new View("-"); 
                TrafficGuiController controller = new TrafficGuiController(view);
                controller.control();
            }
        });  
        
        // Construct traffic simulation scene
        VehicleProducer prod = new VehicleProducer();
        prod.setStartPoint(100, 100);
        VehicleConsumer cons = new VehicleConsumer();
        cons.setEndPoint(510, 100);
        TrafficLight tl = new TrafficLight();
        tl.setStartPoint(310, 100);
        tl.setEndPoint(300, 100);
        Lane lane_east = new Lane("VorAmpel", prod, tl);
        Lane lane_east_2 = new Lane("NachAmpel", tl, cons);
        TrafficLightObserverPlace tlWaiting = new TrafficLightObserverPlace( tl );
        tlWaiting.setActionPNTargetPlace( "WAITING" );
        
        try {
        	PetriNet currentPN = buildNet( tl );
        	Collection<Place> placesPN = ( currentPN ).getPlaces();
	    	for (Place singlePlaceObj : placesPN) {
	    		System.out.println("Places: " + singlePlaceObj.getId() + " = " + singlePlaceObj.getNumberOfTokensStored() );
   			}
	    	
    		runner = new RealTimePetriNetRunner( currentPN );
			FiringGenericActionListener firedTrans = new FiringGenericActionListener();
			runner.addPropertyChangeListener(firedTrans);
			
			runner.startRealTimeClock();
			
			// Here: Approach is started manually
/*			Thread testThread = new Thread() {
				public void run() {
					try {
						Thread.sleep(2100);
			        	RealTimePetriNetRunner.getPetriNetRunnerSemaphore().acquire();
			        	
			    		//actionary.setStartParameterForPN("{\"hand_position\":4}", currentXSchema.getIncludeHierarchy(), runner);
			        	runner.markPlace("approach.ENABLED", "Default", 1);
			        	
			        	RealTimePetriNetRunner.getPetriNetRunnerSemaphore().release();
			        } catch (Exception e) {
			        	e.printStackTrace();
			        }
				} };
			testThread.start();*/
			
			VisualizationPetriNetFrame frame = new VisualizationPetriNetFrame( currentPN );
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(640, 480);
			frame.setVisible(true);
			
			/*try {
				runner.markPlace("WAITING", "Default", 1);
				System.out.println("Marked place");
			} catch (Exception e) {
				e.printStackTrace();
			}*/
			
			while (true) {
				// Stepping the Petri Net every second
				// and for the rest of the time the thread sleeps.
	            runner.stepPetriNetSynchronized(1000);
	            TemporalTrafficObject.updateAll( 1. );
	        }

    	} catch (Exception e) {
	    		e.printStackTrace();
    	}

    }

    /**
     * Build test Petri networks for testing the runner of the petri network.
     */     
    private static PetriNet buildNetBuilder() throws PetriNetComponentException {
    	PetriNet net = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(APlace.withId("WAITING").externallyAccessible()).and(
    		APlace.withId("GREEN")).and(
    		AnExternalTransition.withId("SwitchToGreen").andExternalClass("traffic_sim.TrafficLightTransitionCall")).and(
            ANormalArc.withSource("WAITING").andTarget("SwitchToGreen").with("1", "Default").token()).andFinally(
            ANormalArc.withSource("SwitchToGreen").andTarget("GREEN").with("1", "Default").token());
        System.out.println("Build net ");

		return net;
	}
	
	private static PetriNet buildNet(TrafficLight tl) throws PetriNetComponentException {
		// PetriNet with Name - this creates an include hierarchy 
		// which has to be referenced from here on.
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
    	TrafficLightTransitionCall extAct = new TrafficLightTransitionCall( tl );    	
    	DiscreteExternalActionCallTransition newTransition = new DiscreteExternalActionCallTransition( "SwitchToGreen", "SwitchToGreen", extAct );
    	petriNet.addTransition(newTransition);
		
		Map<String, String> weights = new HashMap<>();
		weights.put("Default", "1");
		Map<String, String> weights2 = new HashMap<>();
		weights2.put("Default", "1");
    	petriNet.addArc( new InboundNormalArc( startPlace, newTransition, weights ) );
    	petriNet.addArc( new OutboundNormalArc( newTransition, endPlace, weights2 ) );
    	
        System.out.println("Build net ");

		return petriNet;
	}

}
