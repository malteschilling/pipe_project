package pipe_project;

import uk.ac.imperial.pipe.runner.RealTimePetriNetRunner;
import uk.ac.imperial.pipe.models.petrinet.*;

/**
 * A PetriNetRunner that handles time as well.
 * 
 * Importantly, RealTimePetriNetRunner is running in real time.
 * Therefore, it creates its own thread and starts it and runs in the background.
 * It can be used to synchronize access to external places ..., but access
 * is guarded by semaphores. Only when the runner is not working actively on the 
 * PN it allows to change PN places or fire externally transitions. 
 * 
 */
public class FFWTimePetriNetRunner extends RealTimePetriNetRunner {
	
	private long initialFiringTime, nextFiringTime, lastFiringTime;
	
	public FFWTimePetriNetRunner(PetriNet petriNet) {
		super(petriNet);
	}


	public void stepFastForwardPetriNet(int duration) {
		logger.info("run ExecutablePetriNet "+executablePetriNet.getName().getName());
		start();
		/*if ( (realCurrentTime - realStartTime) <  (pnCurrentTime - pnStartTime) ){
			try {
				semaphore.release();
				//System.out.println("Semaphore: " + semaphore);
				Thread.sleep( (pnCurrentTime - pnStartTime) - (realCurrentTime - realStartTime) );
				semaphore.acquire();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		} */
		// Afterwards both clocks are synchronized again and 
		// PN is advanced parallel to realtime
		initialFiringTime = this.executablePetriNet.getTimedState().getCurrentTime();
		nextFiringTime = initialFiringTime;
		do {	
			lastFiringTime = nextFiringTime;
			nextFiringTime = fireAllCurrentEnabledTransitionsAndGetNextFiringTime(nextFiringTime);
			/*try {
				semaphore.release();
				Thread.sleep( (nextFiringTime - lastFiringTime) );
				semaphore.acquire();
			} catch(InterruptedException e) {
				e.printStackTrace();
			} */			
		} while ((nextFiringTime >= 0) & ( (initialFiringTime + duration) >= nextFiringTime ));
		if ((initialFiringTime + duration) > lastFiringTime ) {
			setCurrentTimeExecutablePetriNet(initialFiringTime + duration);
		}
		end(); 	
	}

}
