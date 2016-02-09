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
import java.util.SimpleTimeZone;

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

	private static final int TIME_GREEN = 5000;
	private static final int TIME_YELLOW = 500;
	private static final int TIME_START_PHASE = 1000;
	private static final int TIME_YELLOW_RED = 500;
	public static FFWTimePetriNetRunner runner;

	// Call with: mvn exec:java -Dexec.args="false"
    public static void main(String[] args) {
     	// 1. Construct main window showing traffic situation (and exit button)
		final TrafficController controller = new TrafficController();
		boolean realTimeArg = true;
		if (args.length > 0) {
			try {
    			realTimeArg = Boolean.parseBoolean(args[0]);
		    } catch (NumberFormatException e) {
    		    System.err.println("Argument " + args[0] + " must be a boolean - indicating if the simulation should be run in real time (or not).");
    		}
    	}
		controller.setRealTimeControlFlag( realTimeArg );
		
    	// 2. Construct traffic simulation scene

        try {
			PetriNet currentPN = createEasyIntersection(controller, 100, 100);
        	// 3. Build the simple Petri network controlling traffic:
        	// when there are 3 or more cars in front of the traffic light
        	// (this is tested in the TrafficLightObserver above)
        	// the PetriNet place for WAITING is marked which causes
        	// the following transition to fire.
        	// This external transition is linked to an external action which switches
        	// the traffic light (this seems overly complicated in this trivial scenario
        	// but becomes important when one has to deal with many concurrent ongoing
        	// and possibly temporal processes depending on the petri net).
        	Collection<Place> placesPN = (currentPN).getPlaces();
	    	for (Place singlePlaceObj : placesPN) {
	    		System.out.println("Places: " + singlePlaceObj.getId() + " = "
	    				+ singlePlaceObj.getNumberOfTokensStored() );
   			}

	    	// Start running of the PN
    		runner = new FFWTimePetriNetRunner( currentPN );
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


    private static PetriNet createEasyIntersection(
    		TrafficController controller, int xCoordinate, int yCoordinate) throws PetriNetComponentException {

    	// Lane from east to west
    	VehicleProducer eastStart = new VehicleProducer(800, 100);
    	VehicleConsumer westDestination = new VehicleConsumer(100, 100);
    	Lane eastWestLane = new Lane("EastToWestLane",
    			eastStart, westDestination);

    	// Lane form west to east and south
    	VehicleProducer westStart = new VehicleProducer(100, 200);
    	VehicleConsumerEvaluation eastDestination1 = new VehicleConsumerEvaluation(800, 200, "EastEndTL");
    	VehicleConsumer southDestination = new VehicleConsumer(400, 500);
    	LaneExtension westLaneSplit = new LaneExtension(200, 200);
    	LaneExtension westLaneExtension = new LaneExtension(240, 240);
    	LaneExtension westToSouthTurn = new LaneExtension(400, 240);

    	TrafficLight westTrafficLight = new TrafficLight();
    	westTrafficLight.setEndPoint(440, 200);
    	westTrafficLight.setStartPoint(480, 200);
    	westTrafficLight.setTrafficLightPosition(420, 220);

    	// Incoming lane (until it splits)
    	Lane westIncomingLane = new Lane("WestIncoming",
    			westStart, westLaneSplit);
    	// Lane continuing to the east
    	Lane westTrafficLightLane = new Lane("WestTrafficLightLane",
    			westLaneSplit, westTrafficLight);
    	Lane eastDestinationLane1 = new Lane("EastDestinationLane1",
    			westTrafficLight, eastDestination1);
    	// Lane turning to the south
    	Lane westToSouthTurnLane1 = new Lane("WestToSouthTurnLane",
    			westLaneSplit, westLaneExtension);
    	Lane westToSouthTurnLane2 = new Lane("WestToSouthTurnLane2",
    			westLaneExtension, westToSouthTurn);
    	Lane southDestinationLane = new Lane("SouthDestinationLane",
    			westToSouthTurn, southDestination);


    	// Lanes from south to west and east
    	VehicleProducer southStart = new VehicleProducer(460, 500);
    	VehicleConsumerEvaluation westDestination2 = new VehicleConsumerEvaluation(100, 140, "WestEndTL");
    	VehicleConsumer eastDestination2 = new VehicleConsumer(800, 240);

    	LaneExtension southToWestTurn = new LaneExtension(460, 140);
    	LaneExtension southLaneSplit = new LaneExtension (460, 400);
    	LaneExtension southLaneExtension = new LaneExtension(500, 360);
    	LaneExtension southToEastTurn = new LaneExtension(500, 240);

    	TrafficLight southTrafficLight = new TrafficLight();
    	southTrafficLight.setEndPoint(460, 220);
    	southTrafficLight.setStartPoint(460, 180);
    	southTrafficLight.setTrafficLightPosition(480, 220);

    	// Incoming lane (until it splits)
    	Lane southIncomingLane = new Lane("SouthIncoming",
    			southStart, southLaneSplit);
    	// Lane turning to the west
    	Lane southTrafficLightLane = new Lane("SouthTrafficLightLane",
    			southLaneSplit, southTrafficLight);
    	Lane southWestTurnLane = new Lane("SouthToWestTurnLane",
    			southTrafficLight, southToWestTurn);
    	Lane westDestinationLane2 = new Lane("WestDestinationLane2",
    			southToWestTurn, westDestination2);
    	// Lane turning to the east
    	Lane southToEastTurnLane1 = new Lane("SouthToEastTurnLane1",
    			southLaneSplit, southLaneExtension);
    	Lane southToEastTurnLane2 = new Lane("SouthToEastTurnLane2",
    			southLaneExtension, southToEastTurn);
    	Lane eastDestinationLane2 = new Lane("EastDestinationLane2",
    			southToEastTurn, eastDestination2);

		// An object observing the simulator state (is pulled each simulation update)
		// and can change the marking in the Petri Network (in the target place)
		//new TrafficLightObserver(westTrafficLight).setActionPNTargetPlace("WAITING");
		//new TrafficLightObserver(southTrafficLight).setActionPNTargetPlace("WAITING");

    	// Add all drawables (lanes and traffic lights) to the view controller
    	controller.getView().getTrafficView().addAllDrawables(
			eastWestLane,
			westIncomingLane, westTrafficLightLane, eastDestinationLane1,
			westToSouthTurnLane1, westToSouthTurnLane2, southDestinationLane,
			westTrafficLight,
			southIncomingLane, southTrafficLightLane, southWestTurnLane,
			westDestinationLane2, southToEastTurnLane1, eastDestinationLane2,
			southToEastTurnLane2,
			southTrafficLight);

    	return buildNet(southTrafficLight,westTrafficLight);
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
	private static PetriNet buildNet(TrafficLight southTrafficLight,TrafficLight westTrafficLight) throws PetriNetComponentException {
		// Create a Petri Net.
		PetriNet net = new PetriNet();
		net.addToken(new ColoredToken("Default",Color.BLACK));
		HashMap<String,String> weights = new HashMap<>();
		weights.put("Default","1");
		DiscreteExternalActionCallTransition turnPhaseWest = new DiscreteExternalActionCallTransition("turnPhaseWest", "turnPhaseWest",() -> {
			westTrafficLight.setTrafficLightGreen(Color.YELLOW);
		});
		turnPhaseWest.setTimed(true);
		turnPhaseWest.setDelay(TIME_START_PHASE);
		net.addTransition(turnPhaseWest);
		DiscreteExternalActionCallTransition turnPhaseSouth = new DiscreteExternalActionCallTransition("turnPhaseSouth", "turnPhaseSouth",() -> {
			southTrafficLight.setTrafficLightGreen(Color.YELLOW);
		});
		turnPhaseSouth.setTimed(true);
		turnPhaseSouth.setDelay(TIME_START_PHASE);
		net.addTransition(turnPhaseSouth);
		DiscretePlace isPhaseWest = new DiscretePlace("isPhaseWest", "isPhaseWest");
		DiscretePlace isPhaseSouth = new DiscretePlace("isPhaseSouth", "isPhaseSouth");
		net.addPlace(isPhaseWest);
		net.addPlace(isPhaseSouth);
		isPhaseSouth.incrementTokenCount("Default");
		net.addArc(new InboundNormalArc(isPhaseWest,turnPhaseWest,weights));
		net.addArc(new InboundNormalArc(isPhaseSouth,turnPhaseSouth,weights));

		//south Trafficlight
		TrafficLight light =  southTrafficLight;
		Place isGreen = new DiscretePlace("Green"+light);
		Place isYellow = new DiscretePlace("Yellow"+light);
		Place isRed = new DiscretePlace("Red"+light);
		net.addPlace(isGreen);
		net.addPlace(isYellow);
		net.addPlace(isRed);
		isRed.incrementTokenCount("Default");
		DiscreteExternalActionCallTransition turnRed = new DiscreteExternalActionCallTransition("turnRed" + light, "turnRed" + light, () -> {
			light.setTrafficLightGreen(Color.RED);
		});
		turnRed.setTimed(true);
		turnRed.setDelay(TIME_YELLOW);
		net.addTransition(turnRed);
		DiscreteExternalActionCallTransition turnYellow = new DiscreteExternalActionCallTransition("turnYellow" + light, "turnYellow" + light, () -> {
			light.setTrafficLightGreen(Color.YELLOW);
		});
		turnYellow.setTimed(true);
		turnYellow.setDelay(TIME_GREEN);
		net.addTransition(turnYellow);
		DiscreteExternalActionCallTransition turnGreen = new DiscreteExternalActionCallTransition("turnGreen" + light, "turnGreen" + light, () -> {
			light.setTrafficLightGreen(Color.GREEN);
		});
		turnGreen.setTimed(true);
		turnGreen.setDelay(TIME_YELLOW_RED);
		net.addTransition(turnGreen);
		net.addArc(new InboundNormalArc(isGreen,turnYellow,weights));
		net.addArc(new OutboundNormalArc(turnYellow,isYellow,weights));
		net.addArc(new InboundNormalArc(isYellow,turnRed,weights));
		net.addArc(new OutboundNormalArc(turnRed,isRed,weights));
		net.addArc(new InboundNormalArc(isRed,turnGreen,weights));
		net.addArc(new InboundNormalArc(isYellow,turnGreen,weights));
		net.addArc(new OutboundNormalArc(turnGreen,isGreen,weights));
		net.addArc(new OutboundNormalArc(turnPhaseSouth,isYellow,weights));
		net.addArc(new OutboundNormalArc(turnRed,isPhaseWest,weights));
		net.addArc(new InboundInhibitorArc(isRed,turnRed));

		//south Trafficlight
		TrafficLight light2 =  westTrafficLight;
		isGreen = new DiscretePlace("Green"+light2);
		isYellow = new DiscretePlace("Yellow"+light2);
		isRed = new DiscretePlace("Red"+light2);
		net.addPlace(isGreen);
		net.addPlace(isYellow);
		net.addPlace(isRed);
		isRed.incrementTokenCount("Default");
		turnRed = new DiscreteExternalActionCallTransition("turnRed" + light2, "turnRed" + light2, () -> {
			light2.setTrafficLightGreen(Color.RED);
		});
		turnRed.setTimed(true);
		turnRed.setDelay(TIME_YELLOW);
		net.addTransition(turnRed);
		turnYellow = new DiscreteExternalActionCallTransition("turnYellow" + light2, "turnYellow" + light2, () -> {
			light2.setTrafficLightGreen(Color.YELLOW);
		});
		turnYellow.setTimed(true);
		turnYellow.setDelay(TIME_GREEN);
		net.addTransition(turnYellow);
		turnGreen = new DiscreteExternalActionCallTransition("turnGreen" + light2, "turnGreen" + light2, () -> {
			light2.setTrafficLightGreen(Color.GREEN);
		});
		turnGreen.setTimed(true);
		turnGreen.setDelay(TIME_YELLOW_RED);
		net.addTransition(turnGreen);
		net.addArc(new InboundNormalArc(isGreen,turnYellow,weights));
		net.addArc(new OutboundNormalArc(turnYellow,isYellow,weights));
		net.addArc(new InboundNormalArc(isYellow,turnRed,weights));
		net.addArc(new OutboundNormalArc(turnRed,isRed,weights));
		net.addArc(new InboundNormalArc(isRed,turnGreen,weights));
		net.addArc(new InboundNormalArc(isYellow,turnGreen,weights));
		net.addArc(new OutboundNormalArc(turnGreen,isGreen,weights));
		net.addArc(new OutboundNormalArc(turnPhaseWest,isYellow,weights));
		net.addArc(new OutboundNormalArc(turnRed,isPhaseSouth,weights));
		net.addArc(new InboundInhibitorArc(isRed,turnRed));

		return net;
	}

}
