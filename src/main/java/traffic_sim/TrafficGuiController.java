package traffic_sim;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

/**
 * Simple GUI Controller.
 *
 * Providing Exit button.
 */
public class TrafficGuiController {

    private View view;
    private ActionListener actionListener;
    
    protected static JPanel animationView;
    
    public TrafficGuiController(View view){
        this.view = view;
    }
    
    public void control(){        
        actionListener = new ActionListener() {
              public void actionPerformed(ActionEvent actionEvent) {                  
                  linkBtnAndLabel();
              }
        };                
        view.getButton().addActionListener(actionListener);   
    }
    
    private void linkBtnAndLabel(){    
		System.exit(0);           
    }    
}