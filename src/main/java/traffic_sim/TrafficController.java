package traffic_sim;

//import uk.ac.imperial.pipe.runner.RealTimePetriNetRunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import pipe_project.FFWTimePetriNetRunner;

import javax.swing.JPanel;

/**
 * Simple GUI Controller.
 *
 * Providing Exit button.
 */
public class TrafficController {

    private View view;
    private ActionListener actionListener;
    
    private static final double UPDATE_STEP = 0.1;
    // The simulation can be run in realtime - or just in evaluation mode
	// = there will be no sleep cycles in between (and no view updates).
    private static boolean REALTIME_CONTROL = true;

    public TrafficController(){
        this.view = new View();
        actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        };
        view.getButton().addActionListener(actionListener);
    }

    public void control(FFWTimePetriNetRunner runner){
        while (true) {
            // Stepping the Petri Net every second (this should be tuned to a lower value
            // when your system is running).
            // and for the rest of the time the thread sleeps.
            if (REALTIME_CONTROL) {
	            runner.stepPetriNetSynchronized( (int) (UPDATE_STEP*1000) );
	        } else {
	        	runner.stepFastForwardPetriNet( (int) (UPDATE_STEP*1000) );
	        }
            // And calling an update on the traffic simulation.
            TemporalTrafficObject.updateAll( UPDATE_STEP );
            view.getTrafficView().repaint();
        }
    }
    
    public boolean getRealTimeControlFlag() {
    	return REALTIME_CONTROL;
    }
    
    public void setRealTimeControlFlag( boolean realTime ) {
    	REALTIME_CONTROL = realTime; 
	}
	
    public View getView() {
        return view;
    }

}