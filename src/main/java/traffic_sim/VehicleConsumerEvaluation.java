package traffic_sim;

import java.awt.Point;

/**
 * VehicleConsumerLogger
 *
 * Extends the VehicleConsumer, logs data from the leaving vehicles (number, average time).
 */
public class VehicleConsumerEvaluation extends VehicleConsumer {

	private String name;
	private int number_of_cars = 0;
	private double total_time = 0.;
	
	public VehicleConsumerEvaluation(int xCoordinate, int yCoordinate, String name) {
    	super(xCoordinate, yCoordinate);
		this.name = name;
    }

    /*
     * A VehicleConsumer has to implement this method to check
     * if vehicles are allowed to leave a lane (e.g. for a traffic light this
     * is not always the case).
     */
    @Override
	public boolean tryToConsumeVehicle(Vehicle veh) {
		number_of_cars = number_of_cars + 1;
		total_time = total_time + ( TemporalTrafficObject.getCurrentTime() - veh.getTimeOfProduction() );
		
		if (number_of_cars % 10 == 0) {
			System.out.println("TURNOUT AT " + this.name + " - Cars: " + number_of_cars + " ; AverageTime: " + this.getAverageTimeOfVehicles() );
		}
		
    	return super.tryToConsumeVehicle(veh);
    }
    
    public double getAverageTimeOfVehicles() {
    	return (total_time/number_of_cars);
    }
    
    public int getNumberOfVehicles() {
    	return number_of_cars;
    }

}