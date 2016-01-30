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

    public static void main(String[] args) {
     	// 1. Construct main window showing traffic situation (and exit button)
		final TrafficController controller = new TrafficController();

    	// 2. Construct traffic simulation scene
		TrafficLight tl = createTwoLaneIntersection(controller);


		// An object observing the simulator state (is pulled each simulation update)
        // and can change the marking in the Petri Network (in the target place)
        TrafficLightObserver tlWaiting = new TrafficLightObserver(tl);
        tlWaiting.setActionPNTargetPlace("WAITING");

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
        	PetriNet currentPN = buildNet(tl);
        	Collection<Place> placesPN = (currentPN).getPlaces();
	    	for (Place singlePlaceObj : placesPN) {
	    		System.out.println("Places: " + singlePlaceObj.getId() + " = "
	    				+ singlePlaceObj.getNumberOfTokensStored() );
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
			controller.control(runner);
    	} catch (Exception e) {
	    		e.printStackTrace();
    	}

    }


    /**
     * Create a simple traffic scene that consists of one road and
     * one traffic light.
     *
     * This methods serves as a simple example of how a traffic scene is build.
     *
     * At the beginning of the road a VehicleProducer sets cars onto the road.
     * At the end of the road a VehicleConsumer takes the cars from the road.
     * The road consists of two lanes. One lane before the traffic light and
     * one lane after the traffic light. The TrafficLight object itself thus
     * connects the two lanes, which comprise the road.
     *
     * @param controller
     * @return The contained traffic light
     */
    private static TrafficLight createTrafficLightSceneExample(
    		TrafficController controller) {

		// for example: on the left side a VehicleProducer sets vehicles onto the first lane
        VehicleProducer prod = new VehicleProducer();
        // has to define a position for graphic layout in visualization
        prod.setStartPoint(100, 100);
        // on right hand: vehicles disappear in this VehicleConsumer
        VehicleConsumer cons = new VehicleConsumer();
        cons.setEndPoint(500, 100);
        // Traffic light in the middle (shown at end of first lane)


        // Create a traffic light
		TrafficLight trafficLight = new TrafficLight();
        // again, points have to be defined for the visualization in order to define where
        // the lane should be drawn
		trafficLight.setStartPoint(310, 100);
		trafficLight.setEndPoint(300, 100);
        // and a position for the traffic light itself has to be defined
		trafficLight.setTrafficLightPosition( 290, 120);

		// The lanes connecting the different producers-consumers
        Lane laneBeforeTrafficLight = new Lane("VorAmpel", prod, trafficLight);
		Lane laneAfterTrafficLight = new Lane("NachBuilding", trafficLight, cons);

		controller.getView().getTrafficView().addAllDrawables(trafficLight,
			laneBeforeTrafficLight, laneAfterTrafficLight);

		return trafficLight;
    }


    /**
     * Create a traffic simulation scene that consists of two roads,
     * a traffic light and a building.
     *
     * @param controller
     * @return The traffic light object in the scene
     */
    private static TrafficLight createTrafficLightSceneWithBuilding(
    		TrafficController controller) {

        VehicleProducer prod = new VehicleProducer();
        prod.setStartPoint(100, 100);

        VehicleConsumer cons = new VehicleConsumer();
        cons.setEndPoint(500, 500);

		TrafficLight tl = new TrafficLight();
        tl.setStartPoint(310, 100);
        tl.setEndPoint(300, 100);
        tl.setTrafficLightPosition( 290, 120);

		Building building = new Building(500, 100, "testPoint");

        Lane lane_east = new Lane("VorAmpel", prod, tl);
        Lane lane_east_2 = new Lane("NachAmpel", tl, building);
		Lane nachBuilding = new Lane("NachBuilding", building, cons);

		controller.getView().getTrafficView().addAllDrawables(
				lane_east, lane_east_2, nachBuilding, tl, building);

		return tl;
    }


    /**
     * Creates a traffic scene that consists of an intersection that consists
     * of two extended lanes, i.e. the lane is extended by a turn left lane
     * and a turn right lane.
     * @param controller
     * @return The traffic light object in the scene
     */
    private static TrafficLight createTwoLaneIntersection(
    		TrafficController controller) {

		// All lanes starting in the west
		VehicleProducer westStart = new VehicleProducer();
		westStart.setStartPoint(600, 300);

		LaneExtension extension = new LaneExtension(700, 300);

		VehicleConsumer destination1 = new VehicleConsumer();
		destination1.setEndPoint(800, 120);
		VehicleConsumer destination2 = new VehicleConsumer();
		destination2.setEndPoint(1000, 300);
		VehicleConsumer destination3 = new VehicleConsumer();
		destination3.setEndPoint(800, 550);

		Lane westIncomingLane = new Lane("BeforeExtension", westStart, extension);

		// Starting in the west, turning left
		TrafficLight westTurnNorthTL = new TrafficLight();
		westTurnNorthTL.setStartPoint(800, 250);
		westTurnNorthTL.setEndPoint(800, 250);
		westTurnNorthTL.setTrafficLightPosition(775, 265);

		LaneExtension westTurnNorthExtension = new LaneExtension(750, 250);
		Lane westTurnNorthLane1 = new Lane("TurnNorthLane1",
				extension, westTurnNorthExtension);
		Lane westTurnNorthLane2 = new Lane("TurnNorthLane2",
				westTurnNorthExtension, westTurnNorthTL);
		Lane westTurnNorthLane3 = new Lane("TurnNorthLane3",
				westTurnNorthTL, destination1);

		// Starting in the west, going straight
		TrafficLight westStraightTL = new TrafficLight();
		westStraightTL.setStartPoint(800, 300);
		westStraightTL.setEndPoint(800, 300);
		westStraightTL.setTrafficLightPosition(775, 315);

		Lane westStraightBeforeTL = new Lane("GoStraightLane",
				extension, westStraightTL);
		Lane westStraightAfterTL = new Lane("GoStraightLane",
				westStraightTL, destination2);

		// Starting in the west, turning south
		TrafficLight westTurnSouthTL = new TrafficLight();
		westTurnSouthTL.setStartPoint(800, 350);
		westTurnSouthTL.setEndPoint(800, 350);
		westTurnSouthTL.setTrafficLightPosition(775, 365);

		LaneExtension westTurnSouthExtension = new LaneExtension(750, 350);
		Lane westTurnSouthLane1 = new Lane("TurnSouthLane1",
				extension, westTurnSouthExtension);
		Lane westTurnSouthLane2 = new Lane("TurnSouthLane2",
				westTurnSouthExtension, westTurnSouthTL);
		Lane westTurnSouthLane3 = new Lane("TurnSouthLane3",
				westTurnSouthTL, destination3);


		// All lanes starting in the EAST
		VehicleProducer eastStart = new VehicleProducer();
		eastStart.setStartPoint(1100, 200);
		LaneExtension eastExtension = new LaneExtension(1000, 200);

		VehicleConsumer eastDestination1 = new VehicleConsumer();
		eastDestination1.setEndPoint(900, 20);
		VehicleConsumer eastDestination2 = new VehicleConsumer();
		eastDestination2.setEndPoint(600, 200);
		VehicleConsumer eastDestination3 = new VehicleConsumer();
		eastDestination3.setEndPoint(900, 450);

		Lane eastIncomingLane = new Lane("EastIncoming",
				eastStart, eastExtension);

		// Starting in the east, turning north
		TrafficLight eastTurnNorthTL = new TrafficLight();
		eastTurnNorthTL.setStartPoint(900, 150);
		eastTurnNorthTL.setEndPoint(900, 150);
		eastTurnNorthTL.setTrafficLightPosition(875, 165);

		LaneExtension eastTurnNorthExtension = new LaneExtension(950, 150);
		Lane eastTurnNorthLane1 = new Lane("EastTurnNorthLane1",
				eastExtension, eastTurnNorthExtension);
		Lane eastTurnNorthLane2 = new Lane("EastTurnNorthLane2",
				eastTurnNorthExtension, eastTurnNorthTL);
		Lane eastTurnNorthLane3 = new Lane("EastTurnNorthLane3",
				eastTurnNorthTL, eastDestination1);

		// Starting in the east, going straight
		TrafficLight eastStraightTL = new TrafficLight();
		eastStraightTL.setStartPoint(900, 200);
		eastStraightTL.setEndPoint(900, 200);
		eastStraightTL.setTrafficLightPosition(875, 215);

		Lane eastStraightBeforeTL = new Lane("EastGoStraightLane",
				eastExtension, eastStraightTL);
		Lane eastStraightAfterTL = new Lane("EastGoStraightLane",
				eastStraightTL, eastDestination2);

		// Starting in the east, turning south
		TrafficLight eastTurnSouthTL = new TrafficLight();
		eastTurnSouthTL.setStartPoint(900, 250);
		eastTurnSouthTL.setEndPoint(900, 250);
		eastTurnSouthTL.setTrafficLightPosition(875, 265);

		LaneExtension eastTurnSouthExtension = new LaneExtension(950, 250);
		Lane eastTurnSouthLane1 = new Lane("EastTurnSouthLane1",
				eastExtension, eastTurnSouthExtension);
		Lane eastTurnSouthLane2 = new Lane("EastTurnSouthLane2",
				eastTurnSouthExtension, eastTurnSouthTL);
		Lane eastTurnSouthLane3 = new Lane("EastTurnSouthLane3",
				eastTurnSouthTL, eastDestination3);


		// Starting in the North
		VehicleProducer northStart = new VehicleProducer();
		northStart.setStartPoint(850, 50);
		LaneExtension northExtension = new LaneExtension(850, 200);

    	// VehicleConsumer northDestination1 = new VehicleConsumer();
    	// northDestination1.setEndPoint(900, 20);
		VehicleConsumer northDestination2 = new VehicleConsumer();
		northDestination2.setEndPoint(850, 500);
    	// VehicleConsumer northDestination3 = new VehicleConsumer();
    	// northDestination3.setEndPoint(900, 450);

		Lane northIncomingLane = new Lane("NorthIncoming",
				northStart, northExtension);

		Lane northToSouthLane = new Lane("NorthToSouthLane",
				northExtension, northDestination2);



		// Starting in the South






		// Controller
		controller.getView().getTrafficView().addAllDrawables(
			// Lanes starting in the west
			westIncomingLane,
			westTurnNorthLane1, westTurnNorthLane2, westTurnNorthLane3,
			westStraightBeforeTL, westStraightAfterTL,
			westTurnSouthLane1, westTurnSouthLane2, westTurnSouthLane3,
			westTurnNorthTL, westStraightTL, westTurnSouthTL,

			// Lanes starting in the east
			eastIncomingLane,
			eastTurnNorthLane1, eastTurnNorthLane2, eastTurnNorthLane3,
			eastStraightBeforeTL, eastStraightAfterTL,
			eastTurnSouthLane1, eastTurnSouthLane2, eastTurnSouthLane3,
			eastTurnNorthTL, eastStraightTL, eastTurnSouthTL,

			// Starting in the north
			northIncomingLane, northToSouthLane
				);


		// West-East Traffic Lights
		TrafficLightObserver westLeftTLOberserver = new TrafficLightObserver (
				westTurnNorthTL);
		westLeftTLOberserver.setActionPNTargetPlace("WAITING");



    	return westTurnNorthTL;

    }


    /**
     * Build test Petri network for traffic light.
     */
	private static PetriNet buildNet(TrafficLight tl) throws PetriNetComponentException {
		// Create a Petri Net.
		PetriNet petriNet = APetriNet.with(AToken.called("Default").
				withColor(Color.BLACK)).
				and(APlace.withId("WAITING").externallyAccessible()).
				andFinally(APlace.withId("GREEN"));
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
    	DiscreteExternalActionCallTransition newTransition =
    			new DiscreteExternalActionCallTransition(
    					"SwitchToGreen", "SwitchToGreen", extAct );
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
