package traffic_sim;

import uk.ac.imperial.pipe.runner.RealTimePetriNetRunner;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * Simple GUI Controller.
 *
 * Providing Exit button.
 */
public class TrafficController {

    private View view;
    private ActionListener actionListener;

    public TrafficController(){
        this.view = new View();
        actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        };
        view.getButton().addActionListener(actionListener);
    }

    public void control(RealTimePetriNetRunner runner){
        while (true) {
            // Stepping the Petri Net every second (this should be tuned to a lower value
            // when your system is running).
            // and for the rest of the time the thread sleeps.
            runner.stepPetriNetSynchronized(1000);
            // And calling an update on the traffic simulation.
            TemporalTrafficObject.updateAll( 1. );
            view.getTrafficView().repaint();
        }
    }

    public View getView() {
        return view;
    }

}