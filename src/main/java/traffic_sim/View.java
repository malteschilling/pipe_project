package traffic_sim;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Frame showing simple traffic simulation.
 */
public class View {
      
    private JFrame frame;
    private JLabel label;
    private JButton button;
    
    //private SimpleHandPane simView;
    
    public View(String text){
        frame = new JFrame("Traffic Sim");                                    
        frame.getContentPane().setLayout(new BorderLayout());                                          
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);           
        frame.setSize(800,400);
        frame.setVisible(true);
        
        RoadPane trafficView = new RoadPane();
        
        TrafficGuiController.animationView = trafficView;
        
        frame.getContentPane().add(trafficView, BorderLayout.CENTER);
        //label = new JLabel(text);
        //frame.getContentPane().add(label, BorderLayout.CENTER);
        
        button = new JButton("EXIT");        
        frame.getContentPane().add(button, BorderLayout.SOUTH);
    }
 
	public void setText(String text){
        label.setText(text);
    }
        
    public JButton getButton(){
        return button;
    }
     
}