package traffic_sim;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Frame showing simple traffic simulation.
 */
public class View {


    private final RoadPane trafficView;
    private JFrame frame;
    private JButton button;

    public View(){
        frame = new JFrame("Traffic Sim");
        frame.getContentPane().setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,400);

        this.trafficView = new RoadPane();

        frame.getContentPane().add(trafficView, BorderLayout.CENTER);
        button = new JButton("EXIT");
        frame.getContentPane().add(button, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    public JButton getButton(){
        return button;
    }

    public RoadPane getTrafficView() {
        return trafficView;
    }

}