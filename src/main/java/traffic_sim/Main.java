package traffic_sim;

import javax.swing.SwingUtilities;

/**
 * Short example of the traffic simulation.
 */
public class Main
{
    public static void main(String[] args) {          
    	// Open the view on the sim 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {                                           
                View view = new View("-"); 
                TrafficGuiController controller = new TrafficGuiController(view);
                controller.control();
            }
        });  
        
        // Construct a simple traffic situation
        VehicleProducer prod = new VehicleProducer();
        prod.setStartPoint(100, 100);
        VehicleConsumer cons = new VehicleConsumer();
        cons.setEndPoint(510, 100);
        TrafficLight tl = new TrafficLight();
        tl.setStartPoint(310, 100);
        tl.setEndPoint(300, 100);
        Lane lane_east = new Lane("VorAmpel", prod, tl);
        Lane lane_east_2 = new Lane("NachAmpel", tl, cons);
        
        // Let the simulation run forever.
        try {
        	while(true) {
				Thread.sleep( 500 );
				TemporalTrafficObject.updateAll( 1. );
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
        
    }
}